package com.mycnproject;

import java.net.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;

public class packet{
    /**
     * @apiNote : class used for converting bytes to useful and vice versa
     * can be understood as a dictionary for the program
     */
    private final int packetsize = 1410;
    private final int headerlen = 15;
    private final int bodylen = packetsize-headerlen;
    private String header;
    private byte[] headerinbytes = new byte[headerlen];
    private String body;
    private byte[] bodyinbytes = new byte[bodylen];
    private byte[] orgpacket = null;
    private ArrayList<Object> parser = new ArrayList<Object>(4);

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
        /**packet structure used for sending connection request */
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
        /**joins and creates byte array and returns it */
        byte[] data = ByteBuffer.allocate(packetsize)
                    .put(headerinbytes)
                    .put(bodyinbytes)
                    .array();

        return data;
    }

    public ArrayList<Object> decodepacket() throws Exception{
        /**
         returns the byte array packet received into below types
        * @param 1 : request /file /attribute specifying type of packet
        * @param 2 : specifies ip address/sequence number/filename
        * @param 3 : specifies port / binary file data / size
        */
        headerinbytes = Arrays.copyOfRange(orgpacket, 0, headerlen-1);
        bodyinbytes = Arrays.copyOfRange(orgpacket,headerlen-1,orgpacket.length);
        header = new String(headerinbytes).trim();

        //auths header is used while sending information in strings
        //ex filename and filecompletion
        if(header.contains("auths")){
            // String numberOnly= header.replaceAll("[^0-9]", "");
            // int seqno =Integer.parseInt(numberOnly);
            parser.add("attribute");
            String body = new String(bodyinbytes);

            if(body.contains("done") || body.equals("done")){
                //returns that sending of file has been completed
                parser.add("completed");
                parser.add(0L);
                return parser;
            }

            //structure of file attributes is : filename@filesize
            String data[] = body.split("@");
            parser.add(data[0]);
            String numberOnly= data[1].replaceAll("[^0-9]", "");   //removes everything except digits
            // System.out.println("type is : "+Integer.parseInt(numberOnly));
            long y = Long.valueOf(numberOnly);
            parser.add(y);
            return parser;

        }else if(header.contains("authf")){
            //authf header used to send file binaries ex authf1,authf2 etc
            String numberOnly= header.replaceAll("[^0-9]", "");
            long seqno =Long.parseLong(numberOnly);
//            System.out.println("One file packet received");
            parser.add("file");
            parser.add(seqno);
            parser.add(bodyinbytes);
            return parser;
        }else if(header.contains("request")){
            //request header used for connection establishment
            //structure is :   ipaddress@port
            String[] data = new String(bodyinbytes).split("@");
            parser.add("request");
            parser.add(data[0]);
            parser.add(data[1]);

        }
        //Features under developement
        // else if(header.contains("redo")){
        //     parser.add("redo");
        //     String[] resendseq = new String(bodyinbytes).split(",");
        //     for(String x:resendseq){
        //         parser.add(Integer.parseInt(x));
        //     }
        //     return parser;
        // }else if(header.contains("alive")){
        //     parser.add("alive");
        //     return parser;
        // }

        parser.add("Corrupted or unknown");
        return parser;
    }
}