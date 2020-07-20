package udpftp;

import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class master {

    public static int udpport;
    public static int packetsize = 1410;
    public static int headerlen = 15;
    public static int bodylen = packetsize-headerlen;
    public static HashMap<String,byte[]> receivebuffer = new HashMap<String,byte[]>(50);
    public static ArrayList<String> resendpool = new ArrayList<String>(50);
    public static int totalsent = 0;
    public static int totalreceived = 0;
    public static HashMap<String,byte[]> sendbuffer = new HashMap<String,byte[]>(50);
    public static String last_header = null; 
    public static int last_header_no = 0;
    public static int rbuffersize = 0;
    public static boolean retransmit = false;
    public static int temp_count = 0;
    public boolean killdaemon = false;
    public static InetAddress peeraddress;
    public static int peerport;
    public static boolean handshake_status = false;

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
     * 1) auths + data in bytes
     * 2) redo  + resendpool csv       x 5
     * 3) alive + totalreceived
     * 4) auths + terminate            x 5
     * 
     * Formula :    count = smallest_header-last_header
     */

     //int a =Integer.parseInt(line.replaceAll("[\\D]", ""));


    public void filter_and_print(){

        ArrayList<String> sortedKeys = new ArrayList<String>(receivebuffer.keySet());
        Collections.sort(sortedKeys);
        String least = sortedKeys.get(0);
        int a =Integer.parseInt(least.replaceAll("[\\D]", ""));
        if(last_header.contains("accept") && !sortedKeys.isEmpty()){
            if(a == 0){
                System.out.println(">>"+new String(receivebuffer.get(least)));  //convert body(byte[] -> string) and print
                last_header = least;
                last_header_no = a;
                receivebuffer.remove(least);
                return ;
            }
        } else if(last_header.contains("auth") && !sortedKeys.isEmpty()){
            if(a-last_header_no==1){
                if(new String(receivebuffer.get(least)).contains("terminate")){
                    killdaemon = true;
                }
                System.out.println(">>"+new String(receivebuffer.get(least)));  //convert body(byte[] -> string) and print
                last_header = least;
                last_header_no = a;
                receivebuffer.remove(least);
                temp_count = 0;
                return ;
            }
            temp_count+=1;
            if(temp_count>10){
                retransmit = true;
                for(int z =last_header_no+1;z<a;z++){
                    resendpool.add("auths"+z);                       //note we need another thread for resend and alive request
                }
            }
        }

    }

    public void free(){
        ArrayList<String> sortedKeys = new ArrayList<String>(receivebuffer.keySet());
        Collections.sort(sortedKeys);
        String least = sortedKeys.get(0);
        int a =Integer.parseInt(least.replaceAll("[\\D]", ""));
        int count = 50- (a - last_header_no);
        for(int i=50;i>=count;i--){
            resendpool.add(sortedKeys.get(0));
            receivebuffer.remove(sortedKeys.get(i));           // removes the max members of sorted array to make room
        }
        retransmit = true;
    }

    public void waiting() throws InterruptedException{
        System.out.println("Sender thread is waiting");
        synchronized(this){
            wait();

            System.out.println("Resumed again");
        }
    }

    public void resuming() throws InterruptedException{
        System.out.println("resuming");
        synchronized(this){
            notifyAll();
            System.out.println("Done");
        }
    }

    


    
}