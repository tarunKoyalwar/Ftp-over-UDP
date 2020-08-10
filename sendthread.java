import java.io.IOException;
import java.lang.*;
import java.net.*;
import java.util.*;

public class sendthread extends Thread {
    private DatagramSocket ds = null;
    private InetAddress myip = null;
    private int myport = 0;

    public sendthread(DatagramSocket dx,InetAddress ip,int port) {
        ds = dx;
        myip=ip;
        myport=port;
    }

    public void run() {
        try{
            send sender = new send(ds);
            sender.get_peer_details();
            sender.connect_to_ip(myip, myport);
            while(!master.handshake_complete){
                Thread.sleep(10000);
            }
            sender.send_interface();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
  }


