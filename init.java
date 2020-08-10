import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;




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