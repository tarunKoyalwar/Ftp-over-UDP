package com.mycnproject;

import java.net.*;


public class receivethread extends Thread {
    private DatagramSocket ds = null;

    public receivethread(DatagramSocket s) {
        ds = s;
    }

    public void run() {
        boolean x = false;
        receive r = new receive(ds);        //creates instance of receive class
        try{
        	
        	synchronized(this){                                 //completes the handshake 
                System.out.println("[debug]receiver thread alive");
            x = r.completehandshake();
            System.out.println("Handshake completion status : "+master.handshake_complete); 
            }
        	
            master.handshake_complete = x;         //variable used to unblock the sender thread
            if(x){
            r.receive_file();                  // starts receiving file
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}