package com.mycnproject.udp;

import java.nio.*;
import java.util.Arrays;

public class packet extends params{
    /**
     * @apiNote : class used for converting bytes to useful and vice versa
     * can be understood as a dictionary for the program
     */
    private long seq_no = 0L;
    private String header;
    private String body;
    private byte[] headerinbytes = new byte[headerlen];
    private byte[] bodyinbytes = new byte[bodylen];
    private byte[] orgpacket = null;
    private byte[] seq_no_inbytes = new byte[seq_bits];
    private byte[] data_body = new byte[packetsize-seq_bits];
    public static long pcount =0L;

    public packet(byte[] ba){
        orgpacket = ba;
    }

    public packet(String head,String tail){
        /** packet structure used for sending string packets */
        header= head;
        body = tail;
        System.arraycopy(header.getBytes(),0,headerinbytes,0,header.getBytes().length);
        bodyinbytes = body.getBytes();
    }
  

    public packet(String head,byte[] tail){
        /**packet structure used for sending file packets */
        header = head;
        bodyinbytes = tail;
        System.arraycopy(header.getBytes(),0,headerinbytes,0,header.getBytes().length);
    }

    public void encodepacket_and_send() throws Exception{
    	pcount += 1;
    	String cx = String.valueOf(pcount);
    	System.arraycopy(cx.getBytes(), 0, seq_no_inbytes,0,cx.getBytes().length);
        /**joins and creates byte array and returns it */
        byte[] data1 = ByteBuffer.allocate(seq_bits+headerlen)
        		    .put(seq_no_inbytes)
                    .put(headerinbytes)
                    .array();
        byte[] data = new byte[data1.length+bodyinbytes.length];
        System.arraycopy(data1, 0, data, 0, data1.length);
        System.arraycopy(bodyinbytes, 0, data, data1.length, bodyinbytes.length);
       int count=0;
       synchronized (params.send_pool) {
    	   while(params.send_pool.size()>98) {
           	Thread.sleep(200);
           }
           
//           System.out.println("[debug]barray length : "+data.length);

           params.send_pool.put(pcount,data);
	}
       params.instance.spawn_sender_thread();
    }
    
    public static byte[] create_ack(Long l) {
    	String z = "ack"+String.valueOf(l);
    	byte[] data = ByteBuffer.allocate(packetsize)
                .put(z.getBytes())
                .array();
    	
//    	System.out.println("created ack");
    	
    	return data;
    }
    
    public static byte[] create_redo(Long l) {
    	String z = "redo"+String.valueOf(l);
    	byte[] data = ByteBuffer.allocate(packetsize)
                .put(z.getBytes())
                .array();
    	
    	return data;
    }
    
    private void get_ack(byte[] bx) {
    	String ack = new String(bx);
    	ack.trim();
    	String no = ack.replaceAll("[^0-9]", "");
    	params.send_pool.remove(Long.parseLong(no));
//    	System.out.println("ack received");
    }
    private void get_redo(byte[] bx) {
    	String ack = new String(bx);
    	ack.trim();
    	String no = ack.replaceAll("[^0-9]", "");
    	params.resend_queue.add(Long.parseLong(no));
    }
    
    //put to buffer but unique
    public void receive_to_buffer() throws Exception {
    	seq_no_inbytes = Arrays.copyOfRange(orgpacket,0,seq_bits);
    	data_body = Arrays.copyOfRange(orgpacket,seq_bits,orgpacket.length);	
    	String seqno = new String(seq_no_inbytes);
    	if(seqno.contains("ack")) {
    		System.out.println("ack received "+seqno);
    		get_ack(seq_no_inbytes);
    		return;
    	}else if(seqno.contains("redo")) {
    		get_redo(seq_no_inbytes);
    		return;
    	}
    	String numberOnly= seqno.replaceAll("[^0-9]", "");
    	seq_no = Long.parseLong(numberOnly);
    	
    	synchronized (params.buffer) {

        	if(!params.buffer.isEmpty()) {
        		last_packet = params.buffer.firstKey();
        		if(seq_no < last_packet) {
        			return;
        		}else {
        			params.buffer.putIfAbsent(seq_no,orgpacket);
        		}
        	}else {
        		if(seq_no > last_packet) {
        		params.buffer.putIfAbsent(seq_no,orgpacket);
        	}
        	}
		}
    	
    }
       
}