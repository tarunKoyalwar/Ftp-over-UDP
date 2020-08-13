package com.mycnproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class file extends master {
    public String filename;
    public long progress_pointer = 0;
    public long filesize = 0;
    private long actual_file_size = 0;
    private FileOutputStream files;
    private DatagramSocket ds = null;
    private String download_dir = "/home/tarun/Downloads/";


    public file(String Filename) {
        filename = Filename;
    }

    public file(long size) {
        actual_file_size = size;
    }
    
    public boolean file_exists_check() {
    	File F =  new File(filename);
    	if(F.exists()) {
    		if(F.isDirectory()) {
    			System.out.println("Entered File is a Directory please enter a file");
    			return false;
    		}else if(F.isFile()) {
    			filesize = F.length();
    			System.out.println("File is available proceeding to send");
    			return true;
    		}
    	}
    	System.out.println("File does not exist try entering full path ");
    	return false;
    }

    public int checkfile() throws Exception {
        /**
        //this function @checkfile checks if file is already downloaded
        //if half downloaded or exists or not and creates one
         */
    	filename = download_dir+filename;
        File file = new File(filename);
        if (file.exists()) {
            System.out.println("File exists checking integrity");
            filesize = file.length(); // in bytes
            if (filesize == actual_file_size) {
                System.out.println("File already downloaded skipping");
                return 0; // download complete
            } else if (filesize < actual_file_size) {
                progress_pointer = (long) Math.ceil((double) filesize / bodylen);
                progress_pointer -= 1;
                System.out.println("File due on completion");
                return 1; // download incomplete
            }
        } else {
            System.out.println("File does not exist");
            return 2;
        }
        return -1;
    }

    private void send_packet(byte[] bx) throws IOException {
        DatagramPacket dp = new DatagramPacket(bx,bx.length,peerip,peerport);
        ds.send(dp);
        
    }

    public void sendfile(DatagramSocket dsa) throws Exception {
        ds = dsa;
        System.out.println("[debug] file sending has been initiated");
        FileInputStream fis = new FileInputStream(new File(filename));
        int read, buffer_length = bodylen;
        long count = 0;
        // long size = filesize;
        byte[] barray = new byte[buffer_length];

        //reading chunk by chunk in multiples of buffer_length
        while ((read = fis.read(barray)) > 0) {
            if (count < progress_pointer) {
                count += 1;
                continue;
            }
            packet p = new packet("authf"+count++,barray);
            send_packet(p.encodepacket());
        }
        // sending done
        System.out.println("[debug]File sharing has completed");

    }

    public void start_receving() throws FileNotFoundException {
        files = new FileOutputStream(filename,true);
    }

    public void write(byte[] bx) throws IOException {
//    	System.out.print("Writing it");
        files.write(bx);
    }

    public void close_file() throws IOException {
        files.close();
    }
}

