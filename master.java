package com.mycnproject;

import java.net.*;

public class master {
    /**
     * This class is only used for sharing parameters and 
      buffers between threads
     */
    protected static int packetsize = 1410;
    protected static int headerlen = 15;
    protected static int bodylen = packetsize-headerlen;
//    protected static boolean receiving_file = false;
    // protected static HashMap<String,byte[]> receivebuffer = new HashMap<String,byte[]>(50);
    // protected static ArrayList<Long> resendpool = new ArrayList<Long>(50);
    // protected static HashMap<String,byte[]> sendbuffer = new HashMap<String,byte[]>(50);
    // protected static String last_header = null; 
    // protected static int last_header_no = 0;
    // protected static int rbuffersize = 0;
    // protected static boolean retransmit = false;
    // protected static int temp_count = 0;
    // protected boolean killdaemon = false;
    protected static InetAddress peerip;
    protected static int peerport;
    protected static boolean handshake_complete = false;

   
}