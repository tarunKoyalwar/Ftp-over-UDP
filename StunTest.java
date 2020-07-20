package udpftp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.javawi.jstun.attribute.ChangeRequest;
import de.javawi.jstun.attribute.ErrorCode;
import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.attribute.MessageAttribute;
import de.javawi.jstun.attribute.MessageAttributeException;
import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderParsingException;
import de.javawi.jstun.util.UtilityException;


public class stuntest{
    public static InetAddress publicip = null;
    public static int publicport = 0;
    public static DatagramSocket s = null; 

    public static DatagramPacket stunsend() throws Exception{
        MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
        // sendMH.generateTransactionID();

        // add an empty ChangeRequest attribute. Not required by the
        // standard,
        // but JSTUN server requires it

        ChangeRequest changeRequest = new ChangeRequest();
        sendMH.addMessageAttribute(changeRequest);

        byte[] data = sendMH.getBytes();
        DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName("stun.l.google.com"), 19302);
        return p;
    }

    public static void afterreceive(byte[] x) throws UnknownHostException, UtilityException {
        MessageHeader receiveMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingResponse);
        // System.out.println(receiveMH.getTransactionID().toString() + "Size:"
        // + receiveMH.getTransactionID().length);
        try {
                receiveMH.parseAttributes(x);
        } catch (MessageAttributeParsingException e) {
                e.printStackTrace();
        }

        System.out.println("Your ip and port is : ");
        MappedAddress ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
        publicip = ma.getAddress().getInetAddress();
        publicport = ma.getPort();
        System.out.println(ma.getAddress()+" "+ma.getPort());

    }

    public static void control() throws Exception{

        s = new DatagramSocket();
        s.send(stunsend());

        DatagramPacket rp;
        rp = new DatagramPacket(new byte[32], 32);
        s.receive(rp);
        afterreceive(rp.getData());
    }

}
