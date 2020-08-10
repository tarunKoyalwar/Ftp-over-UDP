import java.io.IOException;
import java.lang.*;
import java.net.*;
import java.util.*;

public class sendthread extends Thread {
    private DatagramSocket ds = null;
    private InetAddress myip = null;
    private int myport = 0;
     
    /**
     * @param dx = socket address to bind 
     * @param ip = external ip address of the user
     * @param port = external port of the user
     * These all are obtained from @stuntest class
     */
    public sendthread(DatagramSocket dx,InetAddress ip,int port) {   
        ds = dx;
        myip=ip;
        myport=port;
    }

    public void run() {
        try{
            send sender = new send(ds);            //creates instance of send class
            sender.get_peer_details();             // user enters ip and port of his friend
            sender.connect_to_ip(myip, myport);    // sends a connection request to his friend 
            while(!master.handshake_complete){       // waits until handshake is completed
                Thread.sleep(10000);
            }
            sender.send_interface();              // user gives filename and sends it

        }catch(Exception e){
            e.printStackTrace();
        }
    }
  }


