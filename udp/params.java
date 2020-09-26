package com.mycnproject.udp;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;


public class params {
    /**
     * This class is only used for sharing parameters and 
      buffers between threads
     */
	protected static int packetsize = 1495;
    protected static int seq_bits = 10;
    protected static int headerlen = 8;
    protected static int bodylen = packetsize-(headerlen+seq_bits);
    protected static Long last_packet = 0L;
    protected static Long last_put = 0L;
//    protected static long file_start_pointer = 0L;

    protected static HashMap<Long,byte[]> send_pool = new HashMap<Long,byte[]>(100);
    protected static HashMap<Long,DatagramPacket> resend_stage_pool = new HashMap<>(100);
    protected static ArrayList<Long> resend_queue = new ArrayList<Long>(100);
    protected static ArrayList<Long> redoqueue = new ArrayList<Long>(100);
    protected static ArrayList<Long> acks_tosend = new ArrayList<>(100);
    protected static TreeMap<Long,byte[]> buffer = new TreeMap<>();
    protected static ArrayList<byte[]> received = new ArrayList<>(100);
    protected static Udp instance = null;
    protected static boolean stopit = false;
   
}