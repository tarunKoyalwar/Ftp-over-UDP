import java.net.*;
import java.util.Scanner;
import java.io.*;

public class send extends master{
    private DatagramSocket ds = null;
    private Scanner sc = null;

    public send(DatagramSocket s){
        ds = s;
    }

    public void get_peer_details() throws UnknownHostException {
        sc = new Scanner(System.in);
        System.out.printf("Enter peer ip : ");
        peerip = InetAddress.getByName(sc.nextLine());
        System.out.printf("Enter peer port : ");
        peerport = Integer.parseInt(sc.nextLine());
        sc.close();  
        sc = null;
    }

    private void send_now(byte[] bx) throws IOException {
        DatagramPacket dp = new DatagramPacket(bx, bx.length,peerip,peerport);
        ds.send(dp);
    }



    public void connect_to_ip(InetAddress myip,int myport) throws IOException, InterruptedException {
       packet x = new packet(myip, myport);
       byte[] buffer = x.encodepacket();
       for(int i=0;i<10;i++){
           send_now(buffer);
           if(i==5){
               Thread.sleep(2000);
           }
       }
       System.out.println("Connection Request sent");
    }

    public void send_interface() throws Exception{
        sc = new Scanner(System.in);
        System.out.println("Enter the file to be sent");
        String filename = sc.nextLine();
        file F = new file(filename);
        int stat = F.checkfile();
        if(stat == 2){
            send_interface();
        }
        packet p = new packet("auths",filename+"@"+F.filesize);
        byte[] bx= p.encodepacket();
        for(int i=0;i<5;i++){
            send_now(bx);
        }
        F.sendfile(ds);
        packet px = new packet("auths","done");
        byte[] barray= p.encodepacket();
        for(int i=0;i<5;i++){
            send_now(barray);
        }
    }

   
    
}