package com.mycnproject;

import com.mycnproject.udp.*;
import java.util.*;

import javax.sql.rowset.spi.SyncFactory;

public class receive extends params {
	private ArrayList<Object> data = new ArrayList<>(2);
	private file F = new file();
	private boolean file_in_progress = false;
	
	public receive() {
		
	}
	
	private void decode_bytes(byte[] bx) {
		byte[] headers= new byte[headerlen];
    	headers = Arrays.copyOfRange(bx,seq_bits-1,seq_bits+headerlen-1);
    	byte[] bodies = new byte[bodylen];
    	bodies = Arrays.copyOfRange(bx,seq_bits+headerlen-1,bx.length-1);
    	String header = new String(headers);
    	data.add(header);
    	data.add(bodies);
    	System.out.println("header length : "+header.length()+" : "+bodies.length);
	}
	
	private void handle_data() throws Exception {
		String header = (String)data.get(0);
		byte[] barray = (byte[]) data.get(1);
		data.clear();
		if(header.contains("fstart")) {
			String[] value = new String(barray).trim().split("@");
			synchronized (System.out) {
				System.out.println("Downloading File : "+value[0]+"of Size : "+Long.parseLong(value[1]));
			}
			F.actual_file_size = Long.parseLong(value[1]);
			int z = F.checkfile(value[0]);
			if(z==0) {
				return;
			}else if(z==2) {
				synchronized (System.out) {
					System.out.println("Creating New File ");
				}
				F.start_receving();
				file_in_progress = true;
				
			}
		}else if(header.contains("fbin")) {
			if(file_in_progress) {
				System.out.println("received file size: "+barray.length);
				F.write(barray);
			}else {
				synchronized (System.out) {
					System.out.println("Somethings wrong");
				}
			}
		}else if(header.contains("fend")) {
			if(file_in_progress) {
				F.close_file();
				System.out.println("done receiving exitting");
				params.stopit = true;
			}else {
				synchronized (System.out) {
					System.out.println("Somethings wrong opted to close file");
				}
			}
		}
	}
	
	public void get_data() throws Exception {
		synchronized (params.received) {
			if(!params.received.isEmpty()) {
				for(byte[] bx:params.received) {
					decode_bytes(bx);
					handle_data();				
				}
			}else {
				Thread.sleep(300);
			}
			params.received.clear();
		}
	}
}
