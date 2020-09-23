package com.mycnproject;

import java.util.Scanner;

import com.mycnproject.udp.*;



public class send {
    private Scanner sc= null;
	private String filename;
	public send(Scanner s) {
		this.sc = s;
	}
    

    public void send_interface() throws Exception{
    	
    	synchronized (System.out) {
    		System.out.println("Choose any one of following");
        	System.out.println("1] Send a file");
        	System.out.println("2] Receive a file");
        	int ans = Integer.parseInt(sc.nextLine());
        	if(ans == 2) {
        		System.out.println("Will wait till file is received");
        		return;
        	}else if(ans != 1) {
        		send_interface();
        	}
		}
   
       synchronized (System.out) {
    	   System.out.printf("Enter the file to be sent (full path): ");
    	   filename = sc.nextLine();
    	   System.out.println("---------------");
	  }
        
        file F = new file();

        //checks if file is available
        boolean exists = F.file_exists_check(filename);  
        if(!exists){           //if file to be send does not exist
            System.out.println("File not found");
        	send_interface();
        }
        packet p = new packet("fstart",filename+"@"+F.filesize);
        p.encodepacket_and_send();
        
        
        F.sendfile(F); //passed file pointer to it
        
        
        packet px = new packet("fend","done");
        px.encodepacket_and_send();
        
        System.out.println("file sent successfully");
        
    }
    
    

   
    
}