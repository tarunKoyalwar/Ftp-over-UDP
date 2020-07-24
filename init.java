import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

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
            master.handshake_status = x;
            if(x){
            // r.resuming();
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
    private Scanner sc= null;

    public sendthread(DatagramSocket dx,InetAddress ip,int port) {
        ds = dx;
        myip=ip;
        myport=port;
    }

    public void getinput() throws IOException {
        sc = new Scanner(System.in);
        System.out.printf("Enter peer ip : ");
        peerip = InetAddress.getByName(sc.nextLine());
        System.out.printf("Enter peer port : ");
        peerport = Integer.parseInt(sc.nextLine());
        String x = sc.nextLine();   //removing null after int
    }

    public void startsending(send x) throws IOException {
        System.out.println("<- Control received to sender thread start typing ->");
        boolean continuesending = true;
        int count = 0;
        while (continuesending) {
            System.out.printf(">>");
            String input = sc.nextLine();
            x.join_and_send_strings("auths" + count++, input);
            if(input.equalsIgnoreCase("terminate")){
                continuesending = false;
            }
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
            while(!master.handshake_status){
                Thread.sleep(10000);
            }
            // sender.waiting();
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