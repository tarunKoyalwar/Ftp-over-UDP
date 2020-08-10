import java.util.*;
import java.net.*;

public class receive extends master {
    private DatagramSocket ds = null;

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
            packet p = new packet(dp.getData());

            ArrayList<Object> parser = p.decodepacket();
            if (((String)parser.get(0)).equals("request")) {
                if (((String) parser.get(1)).contains(peerip.toString())) {
                    if (((String) parser.get(2)).contains("" + peerport)) {
                        handshake_complete = true;
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
                    rF.close_file();
                    System.out.println("File downloading completd");
                }
                rF = new file((String)parser.get(1),(Long)parser.get(2));
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
                if(count < (Long)parser.get(1)){
                    rF.write((byte[]) parser.get(2));
                }else{
                    continue;
                }
            }
        }


    }

}