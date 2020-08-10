import java.net.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;

import jdk.jfr.DataAmount;

public class packet{
    private final int packetsize = 1410;
    private final int headerlen = 15;
    private final int bodylen = packetsize-headerlen;
    private String header;
    private byte[] headerinbytes = new byte[headerlen];
    private String body;
    private byte[] bodyinbytes = new byte[bodylen];
    private byte[] orgpacket = null;
    private ArrayList<Object> parser = new ArrayList<Object>();

    public packet(byte[] ba){
        orgpacket = ba;
    }

    public packet(String head,String tail){
        /** packet structure used for sending string packets */
        header= head;
        body = tail;
        System.arraycopy(header.getBytes(),0,headerinbytes,0,header.getBytes().length);
        bodyinbytes = body.getBytes();
    }

    public packet(InetAddress myip,int myport){
        header = "request";
        System.arraycopy(header.getBytes(),0,headerinbytes,0,header.getBytes().length);
        body = myip.toString()+"@"+myport;
        bodyinbytes = body.getBytes();
    }

    public packet(String head,byte[] tail){
        /**packet structure used for sending file packets */
        header = head;
        bodyinbytes = tail;
        System.arraycopy(header.getBytes(),0,headerinbytes,0,header.getBytes().length);
    }

    public byte[] encodepacket(){
        byte[] data = ByteBuffer.allocate(packetsize)
                    .put(headerinbytes)
                    .put(bodyinbytes)
                    .array();

        return data;
    }

    public ArrayList<Object> decodepacket() throws Exception{
        headerinbytes = Arrays.copyOfRange(orgpacket, 0, headerlen-1);
        bodyinbytes = Arrays.copyOfRange(orgpacket,headerlen-1,orgpacket.length);
        header = new String(headerinbytes).trim();
        if(header.contains("auths")){
            // String numberOnly= header.replaceAll("[^0-9]", "");
            // int seqno =Integer.parseInt(numberOnly);
            parser.add("attribute");
            String body = new String(bodyinbytes);
            if(body.contains("done") || body.equals("done")){
                parser.add("completed");
                parser.add(0L);
                return parser;
            }
            String data[] = body.split("@");
            parser.add(data[0]);
            parser.add(Long.parseLong(data[1]));

            return parser;
        }else if(header.contains("authf")){
            String numberOnly= header.replaceAll("[^0-9]", "");
            long seqno =Long.parseLong(numberOnly);
            parser.add("file");
            parser.add(seqno);
            parser.add(new String(bodyinbytes));
            return parser;
        }else if(header.contains("request")){
            String[] data = new String(bodyinbytes).split("@");
            parser.add("request");
            parser.add(data[0]);
            parser.add(data[1]);
        }else if(header.contains("redo")){
            parser.add("redo");
            String[] resendseq = new String(bodyinbytes).split(",");
            for(String x:resendseq){
                parser.add(Integer.parseInt(x));
            }
            return parser;
        }else if(header.contains("alive")){
            parser.add("alive");
            return parser;
        }

        parser.add("Corrupted or unknown");
        return parser;
    }
}