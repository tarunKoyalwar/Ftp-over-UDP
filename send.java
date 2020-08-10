import java.net.*;
import java.util.Scanner;
import java.io.*;

public class send extends master{
    private DatagramSocket ds = null;
    private Scanner sc = null;
     /**
      * @param s = socket address to bind 
      */
    public send(DatagramSocket s){
        ds = s;
    }

    public void get_peer_details() throws UnknownHostException {
        /**Gets details of friend machine */
        sc = new Scanner(System.in);
        System.out.printf("Enter peer ip : ");
        peerip = InetAddress.getByName(sc.nextLine());
        System.out.printf("Enter peer port : ");
        peerport = Integer.parseInt(sc.nextLine());
    }

    private void send_now(byte[] bx) throws IOException {
        /**sends byte array to friends */
        DatagramPacket dp = new DatagramPacket(bx, bx.length,peerip,peerport);
        ds.send(dp);
    }



    public void connect_to_ip(InetAddress myip,int myport) throws IOException, InterruptedException {
        /**send a connection request to friend 
         * being a connection request it is send multiple times
         */
       packet x = new packet(myip, myport);   //instance of packet is created
       byte[] buffer = x.encodepacket();        // details packet into bytearray
       for(int i=0;i<15;i++){
           send_now(buffer);
           if(i%5==0){
               Thread.sleep(2000);
           }
       }
       System.out.println("Connection Request sent");
    }

    public void send_interface() throws Exception{
        // sc = new Scanner(System.in);
        System.out.printf("Enter the file to be sent : ");
        // Thread.sleep(2000);
        String filename = sc.nextLine();
        file F = new file(filename);

        //this function @checkfile checks if file is already downloaded
        //if half downloaded or exists or not and creates one
        int stat = F.checkfile();   
        if(stat == 2){           //if file to be send does not exist
            send_interface();
        }
        packet p = new packet("auths",filename+"@"+F.filesize);
        byte[] bx= p.encodepacket();
        for(int i=0;i<5;i++){
            send_now(bx);
        }
        F.sendfile(ds);   //socket address is send to instance to read file chunk by chunk and send it
        packet px = new packet("auths","done");
        byte[] barray= p.encodepacket();
        for(int i=0;i<5;i++){
            send_now(barray);
        }
    }

   
    
}