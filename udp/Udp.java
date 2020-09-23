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
	private DatagramSocket ds = null;
	private Inet6Address peerip = null;
	private Inet4Address peeripv4 = null;
	private int defaultport = 8888;
	public boolean stopit = params.stopit;
	
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
		   //creating receiver thread and starting it
//		 System.out.println("[debug] Receive thread started");
		 byte[] b = new byte[params.packetsize];

         //allocating packet and its buffer
         DatagramPacket dp = new DatagramPacket(b, b.length);
         ds.receive(dp);
         byte[] received =new byte[dp.getLength()];
         System.arraycopy(b, 0, received, 0, dp.getLength()-1);
         packet p = new packet(received);
         System.out.println("[debug] received end : "+received.length);
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
         		System.out.println("acks to send "+l);
         	}
         }
         temp.clear();
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
	
	public void spawn_sender_thread() throws Exception{
//		System.out.println("[debug] sender thread started");
//		packet p = new packet("hii","adam");
//		p.encodepacket_and_send();
		//create and send acks
        if(params.acks_tosend.size()>0 || params.resend_queue.size()>0 || params.redoqueue.size()>0 || params.send_pool.size()>0) {
        	  synchronized (params.acks_tosend) {
        		  for(Long L:params.acks_tosend) {
                 	   byte[] bx = packet.create_ack(L);
                 	   ds.send(getpacket(bx));
                 	   System.out.println("Acks sent for "+L);
                    }
        		  params.acks_tosend.clear();
			}
//             System.out.println("running");
             
            synchronized (params.send_pool) {
            	 //send pool send
                for(Long l:params.send_pool.keySet()) {
             	   ds.send(getpacket(params.send_pool.get(l)));
//             	  System.out.println("sent "+l);
                }
			}
             synchronized (params.resend_queue) {
            	//resend pool
                 for(Long L:params.resend_queue) {
              	   ds.send(getpacket(params.send_pool.get(L)));
                 }
			}
             synchronized (params.redoqueue) {
            	//create and send redo
                 for(Long L:params.redoqueue) {
              	   byte[] bx = packet.create_redo(L);
              	   ds.send(getpacket(bx));
              	   System.out.println("[debug] redo for "+L);
                 }
			}
        }else {
        	Thread.sleep(200);
        }
        
	}

}
