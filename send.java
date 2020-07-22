import java.lang.*;
import java.util.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class send extends master{
    private DatagramSocket ds = null;

    public send(int peerportgiven,DatagramSocket s,InetAddress peeripgiven){
        peerport=peerportgiven;
        ds = s;
        peerip = peeripgiven;
    }

    public void join_and_send_strings(String header,String body) throws IOException {

        byte[] headerstringinbytes = header.getBytes();
        byte[] bodystringinbytes = body.getBytes();
        byte[] headerinbytes = new byte[headerlen];
        byte[] bodyinbytes = new byte[bodylen];
        System.arraycopy(headerstringinbytes,0,headerinbytes,0,headerstringinbytes.length);
        System.arraycopy(bodystringinbytes,0,bodyinbytes,0,bodystringinbytes.length);
        // System.out.println("headerstringinbytes :"+new String(headerstringinbytes));
        
        byte[] data = ByteBuffer.allocate(packetsize)
                    .put(headerinbytes)
                    .put(bodyinbytes)
                    .array();

        DatagramPacket dp = new DatagramPacket(data,data.length,peerip,peerport);
        ds.send(dp);
        // System.out.println("send buffer : "+new String(data));

    }
    
    //method overloading to send a packet multiple times
    // ex :  handshake packets , retransmission , ack packets
    public void join_and_send_strings(String header,String body,int count) throws IOException{
        for(int i=0;i<count;i++){
            join_and_send_strings(header, body);
        }
    }

    public void handshake(InetAddress x,int port) throws IOException {
    /**
     **********Handshake**************
      1) request + ip in bytes '-' portno        x 10
      2) accept + connected                      x 10
     **********************************
     */
    String body = ""+x.toString()+"@"+port+"";
    System.out.println("Data sent is :"+body);
    join_and_send_strings("request",body,1000);

    System.out.println("Connection Request sent");

    }
    
}