import java.util.*;
import java.io.IOException;
import java.net.*;

public class receive extends master {
    private DatagramSocket ds = null;

    public receive(DatagramSocket dx) {
        ds = dx;
    }

    public void receive_data() throws Exception {
        System.out.println("dAeMoN ReAdY for Receiving");
        while (!killdaemon) {

            byte[] b = new byte[packetsize];

            // allocating packet and its buffer
            DatagramPacket dp = new DatagramPacket(b, b.length);
            ds.receive(dp);
            process_data(dp.getData());

        }
        System.out.println("dAeMoN Killed");

    }

    public void process_data(byte[] bs) {
        byte[] headerdata = Arrays.copyOfRange(bs, 0, headerlen);
        byte[] bodydata = Arrays.copyOfRange(bs, bodylen, packetsize);
        String headString = new String(headerdata);

        if (receivebuffer.size() >= 50) {
            free();
            receivebuffer.putIfAbsent(headString, bodydata);
            filter_and_print();
        } else {
            receivebuffer.putIfAbsent(headString, bodydata);
            filter_and_print();
        }
    }
//verification needed received ip and sent ip same or not
    public boolean completehandshake() throws IOException {
        System.out.println("Waiting for handshake completion");
        boolean handshake_incomplete = true;
        while(handshake_incomplete){
            byte[] b = new byte[packetsize];

            //allocating packet and its buffer
            DatagramPacket dp = new DatagramPacket(b, b.length);
            ds.receive(dp);
            System.out.println(new String(dp.getData()));
            byte[] bx=dp.getData();
            byte[] headerdata = Arrays.copyOfRange(bx, 0, headerlen);
            byte[] bodydata = Arrays.copyOfRange(bx,headerlen,bx.length);
            String headString = new String(headerdata);
            if(headString.contains("request")){
                String bodystring =new String(bodydata).trim();
                String[] data = bodystring.split("@");
                if(data[0].contains(peerip.toString()) && data[1].contains(String.valueOf(peerport))){
                    System.out.println("Ip and port matched handshake Successful");
                    handshake_incomplete = false;
                }
                System.out.println("peer ip : "+data[0]+"and port : "+data[1]+"received");
            }else{
            System.out.println("wrong packet received waiting for correct one");
            }
        }
        System.out.println("Connection Established ");

        return true;
    }

}