
import java.util.*;
import java.net.*;

public class master {
    protected static int packetsize = 1410;
    protected static int headerlen = 15;
    protected static int bodylen = packetsize-headerlen;
    protected static HashMap<String,byte[]> receivebuffer = new HashMap<String,byte[]>(50);
    protected static ArrayList<Long> resendpool = new ArrayList<Long>(50);
    protected static HashMap<String,byte[]> sendbuffer = new HashMap<String,byte[]>(50);
    protected static String last_header = null; 
    protected static int last_header_no = 0;
    protected static int rbuffersize = 0;
    protected static boolean retransmit = false;
    protected static int temp_count = 0;
    protected boolean killdaemon = false;
    protected static InetAddress peerip;
    protected static int peerport;
    protected static boolean handshake_complete = false;

    /**
     * Header + body used in the communication :
      
     **********Handshake**************
      1) request + ip in bytes '-' portno        x 10
      2) accept + connected                      x 10
     **********************************
      
     *****Communication *********
     * auths = string authorized to send
     * authf = file authorized to send
     * 
     * 5) authf + filename
     * 1) auths + data in bytes
     * 2) redo  + resendpool csv       x 5
     * 3) alive + lastreceived
     * 4) auths + terminate            x 5
     * 
     * Formula :    count = smallest_header-last_header
     */

     //int a =Integer.parseInt(line.replaceAll("[\\D]", ""));
    
}