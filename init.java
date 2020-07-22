import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

import jdk.nashorn.internal.runtime.ECMAErrors;

class receivethread extends Thread {
    private DatagramSocket ds = null;

    public receivethread(DatagramSocket s) {
        ds = s;
    }

    public void run() {
        boolean x = false;
        receive r = new receive(ds);
        try{
            x = r.completehandshake();
            r.handshake_status = x;
            if(x){
            r.resuming();
            r.receive_data();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}

class sendthread extends Thread {
    private DatagramSocket ds = null;
    private InetAddress peerip = null;
    private int peerport = 0;
    private InetAddress myip = null;
    private int myport = 0;

    public sendthread(DatagramSocket dx,InetAddress ip,int port) {
        ds = dx;
        myip=ip;
        myport=port;
    }

    public void getinput() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.printf("Enter peer ip : ");
        peerip = InetAddress.getByName(sc.nextLine());
        System.out.printf("Enter peer port : ");
        peerport = Integer.parseInt(sc.nextLine().replaceAll("[\\D]", ""));
        sc.close();
    }

    public void startsending(send x) throws IOException {
        System.out.println("<- Control received to sender thread start typing ->");
        Scanner sc = new Scanner(System.in);
        String input = "sender thread";
        int count = 0;
        while (!input.contains("terminate")) {
            System.out.printf(">>");
            input = sc.nextLine();
            x.join_and_send_strings("auths" + count++, input);
        }
        sc.close();

    }

    public void run() {
        try{
            getinput();
            send sender = new send(peerport, ds, peerip);
            sender.handshake(myip,myport);
            Thread.sleep(2000);
            sender.handshake(myip, myport);
            sender.waiting();
            startsending(sender);

        }catch(IOException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
  }



public class init extends stuntest {

    public static void main(String[] args) throws Exception{
        control();

        //starting the threads
        receivethread object0 = new receivethread(s);
        sendthread object1 = new sendthread(s,publicip,publicport);

        object0.setName("Receiver Thread");
        object1.setName("Sender thread");
        object0.start();
        object1.start();

    }
    
}