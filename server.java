import java.lang.*;
import java.util.*;
import java.net.*;
import java.nio.*;
import java.io.*;

public class server{

    public static int udpport;
    public static int packetsize = 1410;
    public static int offset = 15;
    public static int payload = packetsize-offset;
    public static String last = "null";
    public static byte[][] data = new byte[30][payload];
    public static List<String> list=new ArrayList<String>(30);

    public static void port() throws IOException{
        System.out.printf("Enter the UDP port to listen on : ");
        Scanner scan = new Scanner(System.in);
        udpport = scan.nextInt();
        scan.close();
        if(udpport==0){
            server.port();
        }
    }

    public static boolean  processing(byte[] bs){

        byte[] offsetx = Arrays.copyOfRange(bs,0, offset);
        byte[] payloadx = Arrays.copyOfRange(bs,offset,packetsize);
        String data0 = new String(offsetx);
        String data1 = new String(payloadx);

        if(data0.equalsIgnoreCase("type") && (last.equals("null") || last.contains("string"))){
            last = data0;
            if(data1.equalsIgnoreCase("string")){
                System.out.println("Connection Established and will recieve strings");
            }
            return false;
        }else if(data0.contains("string")){
            System.out.printf("<< ");
            System.out.println(data1);
            if(data1.contains("terminate")){
                return true;
            }
        }
        return false;
        
    }



    public static void main(String[] args) throws Exception{
        
        System.out.println("Starting up the Server");
        port();

        DatagramSocket sc = new DatagramSocket(udpport);
        System.out.println("Started connection at port :"+udpport);
        System.out.println("Waiting for info");

        while(true){

            byte[] b = new byte[packetsize];

            //allocating packet and its buffer
            DatagramPacket dp = new DatagramPacket(b, b.length);
            sc.receive(dp);
            // System.out.println("packet recieved");
            boolean flag = processing(dp.getData());
            // System.out.println("Flag recieved  : "+flag);
            if(flag){
                System.out.println("Terminated");
                break;
            }

        }
        sc.close();
        System.out.println("Done Receiving");


    }
}