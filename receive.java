import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;

public class receive{
    //safest udp packet size in terms of MTU
    public static final int BUFFER_SIZE=1400;

    public static void main(String[] args) throws Exception{

        DatagramSocket sc = new DatagramSocket(9989);
        String sig="terminate";
        System.out.println("Started connection at port 9989");
        System.out.println("Waiting for info");

        while(true){

            byte[] b = new byte[BUFFER_SIZE];

            //allocating packet and its buffer
            DatagramPacket dp = new DatagramPacket(b, b.length);
            sc.receive(dp);
            
            String s5=new String(dp.getData());   //bytes to string
            String s4=s5.trim();
            System.out.printf("<< ");
            System.out.println(s4);
            
            //wait until terminate request received
            if(sig.equalsIgnoreCase(s4)){
                System.out.println("Closed the connection");
                break;
            }

        }
        sc.close();
        System.out.println("Done Receiving");


        
    }
}