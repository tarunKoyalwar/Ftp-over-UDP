import java.net.*;


public class receivethread extends Thread {
    private DatagramSocket ds = null;

    public receivethread(DatagramSocket s) {
        ds = s;
    }

    public void run() {
        boolean x = false;
        receive r = new receive(ds);        //creates instance of receive class
        try{
            x = r.completehandshake();       //completes the handshake 
            master.handshake_complete = x;         //variable used to unblock the sender thread
            if(x){
            // r.resuming();
            r.receive_file();                  // starts receiving file
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}