package com.mycnproject;



public class init extends stun {

    public static void main(String[] args) throws Exception{
        control();   //method in stun class to get ip and port

        //starting the threads
        receivethread object0 = new receivethread(s);
        sendthread object1 = new sendthread(s,publicip,publicport);

        object0.setName("Receiver Thread");
        object1.setName("Sender thread");
        object0.start();
        object1.start();
        
        object1.join();
        object0.join();
        System.out.println("Exiting the program");

    }
    
}