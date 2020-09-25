package com.mycnproject;

import java.util.Scanner;

import com.mycnproject.udp.*;



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
            while(!Udp.stopit){
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
    		if(!temp.matches("[a-zA-Z0-9]+")) {
    			System.out.println("using localhost");
    			if(ipv4) {
    				temp="localhost";
    			}else {
    				temp="ip6-localhost";
    			}
    		}
    		System.out.println("-----------------------");
		}
	    if(ipv4) {
	    	con.connectipv4(temp);
	    }else {
	    	con.connectipv6(temp);
	    }
	    
	    send fsender = new send(sc);
	    receiver_thread treceive = new receiver_thread(con);

		treceive.start();
		fsender.send_interface();
	    
        treceive.join();
        
       
        System.out.println("Exiting the program");

    }
//    
}