import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;

public class send{
     
    //max udp packet size 65527 
    //In practical safest packet size satisfying MTU size is approx 1400

    public static final int BUFFER_SIZE=1400;

    public static DatagramPacket packet(String s,int port) throws Exception{
        byte[] bs=s.getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        DatagramPacket dp = new DatagramPacket(bs,bs.length,ia, port);
        return dp;
    }


    public static void main(String[] args) throws IOException,SocketException{
        DatagramSocket ds = new DatagramSocket();
        int port = 9989;
        Scanner sc=null;
        System.out.println("Sending to port 9989");
        System.out.println("Enter the info to send ");
        System.out.println("Send 'terminate' to stop the communication");

        try {
            sc = new Scanner(System.in);
            while(true){
                System.out.printf(">> ");
                String input= sc.nextLine();
                ds.send(packet(input, port));
                if(input.equalsIgnoreCase("terminate")){
                    break;
                }
            }
        } catch (Exception e) {
                System.out.println(e.getMessage());
        }finally{
            sc.close();
            ds.close();
        }
        System.out.println("Done transmitting");
    }
}