import java.net.*;


public class receivethread extends Thread {
    private DatagramSocket ds = null;

    public receivethread(DatagramSocket s) {
        ds = s;
    }

    public void run() {
        boolean x = false;
        receive r = new receive(ds);
        try{
            x = r.completehandshake();
            master.handshake_complete = x;
            if(x){
            // r.resuming();
            r.receive_file();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}