import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class file extends master {
    private String filename;
    public long progress_pointer = 0;
    public long filesize = 0;
    private long actual_file_size = 0;
    private FileOutputStream files;
    private DatagramSocket ds = null;

    public file(String Filename) {
        filename = Filename;
    }

    public file(String Filename, long size) {
        actual_file_size = size;
    }

    public int checkfile() throws Exception {
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
        files.write(bx);
    }

    public void close_file() throws IOException {
        files.close();
    }
}

