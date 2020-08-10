import java.lang.*;
import java.util.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class client {

    public static int udpport;
    public static int packetsize = 1410;
    public static int offset = 15;
    public static int payload = packetsize-offset;
    public static Scanner sc = new Scanner(System.in);

    public static String port() throws IOException{
        System.out.printf("Enter the UDP port to send : ");
        udpport = sc.nextInt();
        if(udpport==0){
            server.port();
        }
        System.out.printf("Client has started decide what to send : (string,file) : ");
        String x = sc.nextLine();
        String input =sc.nextLine();
        // scan.close();
        return input;

    }

    public static byte[] joinByteArray(byte[] byte1, byte[] byte2) {
        
        return ByteBuffer.allocate(packetsize)
                .put(byte1)
                .put(byte2)
                .array();

    }

    public static byte[] offsets(String S){
        byte[] bs = new byte[offset];
        bs = S.getBytes();
        return bs;
    }

    public static DatagramPacket packet(byte[] data) throws Exception{
        InetAddress ia = InetAddress.getLocalHost();
        DatagramPacket dp = new DatagramPacket(data,data.length,ia,udpport);
        return dp;
    }

    public static void stringshare() throws Exception{
        System.out.println("String share has started");
        DatagramSocket ds = new DatagramSocket();
        System.out.println("Enter the info to send ");
        System.out.println("Send 'terminate' to stop the communication");
        // Scanner sc = new Scanner(System.in);
        int count=0;
        InputStream in = new BufferedInputStream(System.in);
        byte[] offsetx = new byte[offset];

        try {
            while(true){
                System.out.printf(">> ");
                byte[] payloads = new byte[payload];
                in.read(payloads);
                String offs ="string"+count++;
                byte[] offsbuffer = offs.getBytes();
                System.arraycopy(offsbuffer,0, offsetx,0,offsbuffer.length);
                ds.send(packet(joinByteArray(offsetx,payloads)));
                if(new String(payloads).contains("terminate")){
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




    public static void main(final String[] args) throws Exception {
        String input = port();
        System.out.println("you chose "+input);
        if(input.equalsIgnoreCase("string")){
            DatagramSocket sc = new DatagramSocket();
            sc.send(packet(joinByteArray(offsets("type"),"@string".getBytes())));
            sc.close();
            client.stringshare();
        }
        

        
    }
    
}




/*
    public static void fileshare() throws Exception{
        System.out.println("File share has started");
        DatagramSocket ds = new DatagramSocket();
        System.out.println("Enter the nameoffile to send ");
        String filename = sc.nextLine();
        int count=100000;
        try {
            FileInputStream fs = new FileInputStream(filename);
            byte[] buffer = new byte[payload];
            int read = 0;
            while((read = fs.read(buffer))>0){
                ds.send(packet(buffer,udpport));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally{
            String S = "End";
            ds.send(packet(S, port));
            ds.close();
        }

    }
*/