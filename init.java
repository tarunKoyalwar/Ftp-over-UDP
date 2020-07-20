package udpftp;

import udpftp.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

class receivethread extends Thread {
    private DatagramSocket ds = null;

    public receivethread(DatagramSocket s) {
        ds = s;
    }

    public void run() {
        boolean x = false;
        receive r = new receive(ds);
        try {
            x = r.completehandshake();
            r.handshake_status = x;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (x) {
            try {
                r.resuming();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                r.receive_data();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

class sendthread extends Thread {
    private DatagramSocket ds = null;
    private InetAddress iz = null;
    private int ports = 0;
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
        iz = InetAddress.getByName(sc.nextLine());
        System.out.printf("Enter peer port : ");
        ports = Integer.parseInt(sc.nextLine().replaceAll("[\\D]", ""));
        sc.close();
    }

    public void startsending(send x) throws IOException {
        Scanner sc = new Scanner(System.in);
        String input = null;
        int count = 0;
        while (!input.contains("terminate")) {
            System.out.printf(">>");
            input = sc.nextLine();
            x.join_and_send_strings("auths" + count++, input);
        }
        sc.close();

    }

    public void run() {
        try {
            getinput();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        send sender = new send(ports, ds, iz);
        try {
            sender.handshake(myip,myport);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sender.waiting();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            startsending(sender);
        } catch (IOException e) {
            // TODO Auto-generated catch block
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