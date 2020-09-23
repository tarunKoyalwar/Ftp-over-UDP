package com.mycnproject;

<<<<<<< HEAD


public class init extends stun {

    public static void main(String[] args) throws Exception{
        control();   //method in stun class to get ip and port

        //starting the threads
        receivethread object0 = new receivethread(s);
        sendthread object1 = new sendthread(s,publicip,publicport);

        object0.setName("Receiver Thread");
        object1.setName("Sender thread");
        object0.start();
        object1.start();
        
        object1.join();
        object0.join();
        System.out.println("Exiting the program");

    }
    
=======
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.mycnproject.udp.*;


class sender_thread extends Thread{
	private Udp instance = null;
	
	public sender_thread(Udp z) {
		this.instance = z;
	}
	@Override
	public void run() {
		 try{
         	System.out.println("[debug] sender thread started");
         	while(!instance.stopit) {
         		instance.spawn_sender_thread();
         	}
             
         }catch(Exception e){
             e.printStackTrace();
         }
	}
}

class receiver_thread extends Thread{
	private Udp instance = null;
	
	public receiver_thread(Udp r) {
		this.instance=r;
	}
	
	@Override
	public void run() {
		try {
			receive freceiver = new receive();
            synchronized (System.out) {
			System.out.println("[debug] Receiver thread started");	
			}
            while(!instance.stopit){
               instance.spawn_receiver_threads();
               freceiver.get_data();        
            }
            
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
public class init extends params{
	private static boolean ipv4 = false;
	private static Scanner sc = null;
	
	private static void banner() {
		System.out.println("Please use Positional arguments");
		System.out.println("-4 ipv4 address");
		System.out.println("-6 ipv6 connection");
		System.out.println("-h help");
		return;
	}

    public static void main(String[] args) throws Exception{
    	
    	if(args.length<1) {
    		banner();
    		return;
    	}else if(args.length==1) {
    		if(args[0].contains("-4")) {
    			ipv4 = true;
    		}else if(args[0].contains("-6")){
    			ipv4 = false;
    		}else if(args[0].contains("-h")) {
    			banner();
    			return;
    		}else {
    			banner();
    			return;
    		}
    	}else {
    		banner();
    		return;
    	}

    	Udp con = new Udp();
    	String temp;
    	params.instance = con;
    	synchronized (System.out) {
    		sc = new Scanner(System.in);
    		System.out.println("Enter the domain name of person to communicate");
    		System.out.println("Ex : laptop.tarun.project or ip address");
    		System.out.printf(">>");
    		temp = sc.nextLine();
    		System.out.println("-----------------------");
		}
	    if(ipv4) {
	    	con.connectipv4(temp);
	    }else {
	    	con.connectipv6(temp);
	    }
	    
	    
	    sender_thread tsend = new sender_thread(con);
	    receiver_thread treceive = new receiver_thread(con);
    	
	    treceive.start();
	    tsend.start();
	    
        send fsender = new send(sc); 
        fsender.send_interface();
        
       
        System.out.println("Exiting the program");

    }
//    
>>>>>>> 4c19838e4a20ca45b2ce10fb2ef86fa55b4ff47e
}