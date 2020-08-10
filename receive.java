import java.util.*;
import java.net.*;

public class receive extends master {
    private DatagramSocket ds = null;
    private static int file_count=0;

    /**
     * @param dx = socket address to bind to receive packet
     * Note : socket address is used from stuntest class
     */
    public receive(DatagramSocket dx) {
        ds = dx;
    }

    public boolean completehandshake() throws Exception {
        System.out.println("[debug] Waiting for Connection");


        while (true) {
            byte[] b = new byte[packetsize];

            // allocating packet and its buffer
            DatagramPacket dp = new DatagramPacket(b, b.length);
            ds.receive(dp);
            // System.out.println("received packet");
            packet p = new packet(dp.getData());
            
            //received packet i.e byte array is converted
            // to meaningful operations using @packet.decode()
            ArrayList<Object> parser = p.decodepacket();        
            /**return format of decodepacket
             * @param 1 : request /file /attribute specifying type of packet
             * @param 2 : specifies ip address/sequence number/filename
             * @param 3 : specifies port / binary file data / size
             */

            // System.out.println("gg "+parser.get(1)+" "+parser.get(2));
            //
            if (((String)parser.get(0)).equals("request")) {
                if (((String) parser.get(1)).contains(peerip.toString())) {
                    if (((String) parser.get(2)).contains("" + peerport)) { 
                        //if friends ip address entered and the received packet data 
                        //same connection is considered as successfully established(or reachable)
                        handshake_complete = true;
                        System.out.println("Connection Successful");
                        break;
                    }
                    System.out.println("[debug] peer port mismatch");
                }
                System.out.println("[debug] peer ip mismatch");
            } else {
                System.out.println("[debug] connection mismatch");
            }
        }
        System.out.println("[debug] Connection Established ");

        return true;
    }

    public void receive_file() throws Exception {

        System.out.println("[debug] File receiving has started");
        long count=0;
        file rF = null;

        while(true){
            byte[] b = new byte[packetsize];

            //allocating packet and its buffer
            DatagramPacket dp = new DatagramPacket(b, b.length);
            ds.receive(dp);
            packet p = new packet(dp.getData());

            ArrayList<Object> parser = p.decodepacket();

            if(((String)parser.get(0)).equals("attribute")){
                if(((String)parser.get(1)).equals("completed")){
                    //this means file is sent by sender and has been received
                    //so operation is successful
                    rF.close_file();
                    System.out.println("File downloading completed");
                    break;
                }
                // ignore the below part is just a temporary fix for some problems
                file_count++;       
                if(file_count>1){
                    continue;
                }
                //gets file 
                rF = new file((String)parser.get(1),(Long)parser.get(2));
                rF.filename ="/home/tarun/iot/"+((String) parser.get(1)).trim();
                
                //this function @checkfile checks if file is already downloaded
                //if half downloaded or exists or not and creates one
                int x = rF.checkfile();
                if(x == 0){
                    break;
                }else if(x == 1){
                    count = rF.progress_pointer;
                    rF.start_receving();
                }else if(x == 2){
                    System.out.println("   File does not exist creating a new one ");
                    rF.start_receving();
                }else if(x == -1){
                    System.out.println("[debug] Something went wrong ");
                }
            }else if(((String)parser.get(0)).equals("file")){
                //writing chunk by chunk into file
                if(count < (Long)parser.get(1)){
                    rF.write((byte[]) parser.get(2));
                }else{
                    continue;
                }
            }
        }


    }

}