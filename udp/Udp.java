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



public class Udp {
	private static DatagramSocket ds = null;
	private Inet6Address peerip = null;
	private Inet4Address peeripv4 = null;
	private int defaultport = 8888;
	public static boolean stopit = false;
	private static long total_sent = 0;
	private static long total_resent = 0;
	private static long total_redo_sent = 0;
	private static long total_received = 0;
	private static long total_acks_received = 0;
	private static long total_acks_sent = 0;
	
	public Udp() throws SocketException {
		ds = new DatagramSocket(defaultport);
		ds.setReuseAddress(true);
	}
	
	public boolean connectipv6(String address) throws UnknownHostException,IOException {
		peerip = (Inet6Address) Inet6Address.getByName(address);
		if(peerip.isReachable(5000)) {
			System.out.println("Using ipv6..... Host alive");
			return true;
		}
		return false;
	}
	
	public boolean connectipv4(String address) throws Exception{
		peeripv4 = (Inet4Address) InetAddress.getByName(address);
		if(peeripv4.isReachable(5000)) {
			System.out.println("Using ipv4..... Host alive");
			return true;
		}
		return false;
	}
	
	public void spawn_receiver_threads() throws Exception{
		 byte[] b = new byte[params.packetsize];
		 
         DatagramPacket dp = new DatagramPacket(b, b.length);
         ds.receive(dp);
         
         
         byte[] received =new byte[dp.getLength()];
         System.arraycopy(b, 0, received, 0, dp.getLength());
         
         packet p = new packet(received);
         p.receive_to_buffer();   //sends to treemap
         
         List<Long> temp = new ArrayList<>(100);
         synchronized (params.buffer) {
        	 params.buffer.forEach((k,v) -> { if(k==params.last_put+1) {temp.add(k);params.last_put+=1;}});
		}
         
         
         if(!temp.isEmpty()) {
         	for(Long l:temp) {
         		synchronized (params.received) {
         			params.received.add(params.buffer.get(l));
				}
         		synchronized (params.buffer) {
         			params.buffer.remove(l);
				}
         		synchronized (params.acks_tosend) {
         			params.acks_tosend.add(l);	
				}
//         		System.out.println("acks to send "+l);
         	}
         }
         temp.clear();
         
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
             	   total_sent+=1;
             	   params.resend_stage_pool.put(l,dp);
             	   params.send_pool.remove(l);
                }
			}
			 return;
		}
	}
	
	private void send_redoqueue() throws IOException{
		if(params.redoqueue.size()<1) {
			return;
		}else {
			synchronized (params.redoqueue) {
                 for(Long L:params.redoqueue) {
              	   byte[] bx = packet.create_redo(L);
              	   ds.send(getpacket(bx));
                   total_redo_sent+=1;
                   params.redoqueue.remove(L);
                 }
                
			}
			return;
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
                    	   ds.send(params.resend_stage_pool.get(L));
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
	

}
