package com.mycnproject.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class Udp {
	private static DatagramSocket ds = null;
	private static DatagramSocket drs= null;
	private static Inet6Address peerip = null;
	private static Inet4Address peeripv4 = null;
	private static int defaultport = 8888;
	public static boolean stopit = false;
	private static long total_sent = 0;
	private static long total_resent = 0;
	private static long total_redo_sent = 0;
	public static long total_received = 0;
	public static long total_acks_received = 0;
	private static long total_acks_sent = 0;
	private List<Long> temp = new ArrayList<>();
	
	public Udp() throws SocketException {
		
	}
	
	private void initialize_sockets() throws Exception{
		Udp.ds = new DatagramSocket();
		Udp.drs = new DatagramSocket(defaultport);
		drs.setReuseAddress(true);
		ds.setReuseAddress(true);
	}
	
	public boolean connectipv6(String address) throws Exception {
		initialize_sockets();
		peerip = (Inet6Address) Inet6Address.getByName(address);
		if(peerip.isReachable(5000)) {
			System.out.println("Using ipv6..... Host alive -> "+peerip.toString());
			return true;
		}
		return false;
	}
	
	public boolean connectipv4(String address) throws Exception{
		initialize_sockets();
		peeripv4 = (Inet4Address) InetAddress.getByName(address);
		if(peeripv4.isReachable(5000)) {
			System.out.println("Using ipv4..... Host alive -> "+peeripv4.toString());
			return true;
		}
		return false;
	}
	
	public static void stop_connection() throws Exception{
		byte[] barray = packet.create_pkill();
		DatagramPacket dp;
		System.out.println("kill envoked");
		if(peerip != null) {
			dp = new DatagramPacket(barray, barray.length,peerip,defaultport);
		}else {
			dp = new DatagramPacket(barray, barray.length,peeripv4,defaultport);
		}
		for(int i=0;i<5;i++) {
			ds.send(dp);
		}
	}
	
	private void get_missing_packets() {
		long start = 0;
		synchronized (params.acks_tosend) {
			start = params.acks_tosend.get(0);
			for(int i=1;i<params.acks_tosend.size();i++) {
				if(params.acks_tosend.get(i)-1==start) {
					start = params.acks_tosend.get(i);
				}else {
					for(long j=start+1;j<params.acks_tosend.get(i);j++) {
						temp.add(j);
					}
					start=params.acks_tosend.get(i);
				}
			}
		}
		synchronized (params.redoqueue) {
			params.redoqueue.addAll(temp);
			temp.clear();
		}
	}
	
	public void spawn_receiver_threads() throws Exception{
		 byte[] b = new byte[params.packetsize];
		 
         DatagramPacket dp = new DatagramPacket(b, b.length);
         drs.receive(dp);
         
         
         
         byte[] received =new byte[dp.getLength()];
         System.arraycopy(b, 0, received, 0, dp.getLength());
         
         packet p = new packet(received);
         p.receive_to_buffer();   //sends to treemap
         
         synchronized (params.buffer) {
        	 for(Long l:params.buffer.keySet()) {
        		 System.out.println("packet received "+l);
        		synchronized (params.acks_tosend) {
           			params.acks_tosend.add(l);	
  				}
        		 
        		if(l==params.last_put+1) {
        			params.last_put+=1;
        			 
        		    synchronized (params.received) {
              			params.received.add(params.buffer.get(l));
     				}
              		synchronized (params.buffer) {
              			params.buffer.remove(l);
     				}
              		
        		 }else {
        			 get_missing_packets();
        		 }
        	 }
//        	 params.buffer.forEach((k,v) -> { if(k==params.last_put+1) {temp.add(k);params.last_put+=1;}});
		}
         
        transmission_management();
//        
        return;
	}
	
    
	private DatagramPacket getpacket(byte[] barray) {
		DatagramPacket dp;
//		System.out.println("[debug]barray length : "+barray.length);
		if(peerip != null) {
			dp = new DatagramPacket(barray, barray.length,peerip,defaultport);
		}else {
			dp = new DatagramPacket(barray, barray.length,peeripv4,defaultport);
		}
		return dp;
	}
	
	private void send_acks() throws IOException {
		if(params.acks_tosend.size()<1) {
			return;
		}else {
			synchronized (params.acks_tosend) {
      		  for(Long L:params.acks_tosend) {
               	   byte[] bx = packet.create_ack(L);
               	   ds.send(getpacket(bx));
                   total_acks_sent+=1;
                  }
      		  params.acks_tosend.clear();
			}
			return;
		}
	}
	
	private void send_send_pool() throws IOException{
		if(params.send_pool.size()<1) {
			return;
		}else {
			 synchronized (params.send_pool) {
                for(Long l:params.send_pool.keySet()) {
                	DatagramPacket dp = getpacket(params.send_pool.get(l));
             	   ds.send(dp);
             	   System.out.println("packet sent "+l);
             	   total_sent+=1;
             	   params.resend_stage_pool.put(l,dp);
             	   params.send_pool.remove(l);
                }
			}
			 return;
		}
	}
	
	private void send_redoqueue() throws IOException{
		synchronized (params.redoqueue) {
		if(params.redoqueue.size()<1) {
			return;
		}else {
            for(Long L:params.redoqueue) {
              	   byte[] bx = packet.create_redo(L);
              	   System.out.println("redo sent for "+L);
              	   ds.send(getpacket(bx));
                   total_redo_sent+=1;
                   temp.add(L);              
			}
              params.redoqueue.removeAll(temp);
              temp.clear();
			return;
		}
		}
	}
	
	private void send_resend_queue() throws IOException{
		if(params.resend_queue.size() < 1) {
			return;
		}else {
			synchronized (params.resend_queue) {
            	//resend pool
                synchronized (params.resend_stage_pool) {
                	 for(Long L:params.resend_queue) {
                		  if(params.resend_stage_pool.get(L)==null) {
                			  System.out.println("[debug] weird packet already deleted "+L);
                			  return;
                		  }
                    	   Udp.ds.send(params.resend_stage_pool.get(L));
                    	   System.out.println("packet resent "+L);
                    	   params.resend_stage_pool.remove(L);
                    	   total_resent+=1;
                       }
				}
			}
		}
	}
	
	public void spawn_sender_thread() throws Exception{
      send_send_pool();
	}
	
	public void transmission_management() throws IOException{
		send_acks();
		send_redoqueue();
		send_resend_queue();
	}
	
	public static void get_statistics() {
		System.out.println("total sent : "+total_sent);
		System.out.println("total received : "+total_received);
		System.out.println("total acks sent : "+total_acks_sent);
		System.out.println("total acks received : "+total_acks_received);
		System.out.println("total resent : "+total_resent);
		System.out.println("total redo sent : "+total_redo_sent);
	}
	

}
