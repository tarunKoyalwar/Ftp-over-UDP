package com.mycnproject;

import java.net.*;
import java.io.*;

public class sendthread extends Thread {
    private DatagramSocket ds = null;
    private InetAddress myip = null;
    private int myport = 0;
     
    /**
     * @param ds = socket address to bind 
     * @param myip = external ip address of the user
     * @param myport = external port of the user
     * These all are obtained from @stun class
     */
    public sendthread(DatagramSocket ds,InetAddress myip,int myport) {   
        this.ds = ds;
        this.myip=myip;
        this.myport=myport;
    }

    public void run() {
        try{
            send sender = new send(ds);            //creates instance of send class
            sender.get_peer_details();             // user enters ip and port of his friend
            sender.connect_to_ip(myip, myport);    // sends a connection request to his friend 
            synchronized(this){
                System.out.println("[debug] senderthread has resumed");     //thread will wait until connection is establisheds
                System.out.println(master.handshake_complete);
            }
            sender.send_interface();              // user gives filename and sends it

        }catch(Exception e){
            e.printStackTrace();
        }
    }
  }


