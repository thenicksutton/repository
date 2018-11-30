package edu.gvsu.cis.cis656.chat;

import edu.gvsu.cis.cis656.message.MessageComparator;
import edu.gvsu.cis.cis656.queue.PriorityQueue;
import edu.gvsu.cis.cis656.clock.VectorClock;
import edu.gvsu.cis.cis656.message.Message;
import edu.gvsu.cis.cis656.message.MessageTypes;

import java.net.*;
import java.util.Map;
import java.util.Scanner;

public class ChatClient {

    private int pid;
    private String username;
    private Scanner scanner;
    protected DatagramSocket ds;
    private VectorClock clock;

    public ChatClient(){
        clock = new VectorClock();
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("SocketException error: " + e.getMessage());
            System.exit(-1);
        }

        System.out.println("Welcome to the chat program");
        System.out.print("Username:  ");
        scanner = new Scanner(System.in);
        username = scanner.nextLine();

        // Register with server
        Message register = new Message(MessageTypes.REGISTER, username, 0, clock, "");
        try {
            Message.sendMessage(register, ds, InetAddress.getLocalHost(), 8000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Message response = Message.receiveMessage(ds);
        if(response.type == MessageTypes.ACK){
            pid = response.pid;
            clock.addProcess(pid, 0);
            startListener();
            // prompt next line
        } else {
            System.err.println("Error when registering");
            close();
        }

    }

    /**
     * Method that starts the listener thread process
     */
    private void startListener(){
        ListenerThread listenerThread = new ListenerThread(ds, clock, pid);
        Thread t = new Thread(listenerThread);
        t.start();
    }

    /**
     * Sends the given string message to the server who is responsible for distributing
     * @param chat The message to be sent
     */
    private void sendChat(String chat){
        // Increment my own clock
        clock.tick(pid);
        Message m = new Message(MessageTypes.CHAT_MSG, username, pid, clock, chat);
        try {
            Message.sendMessage(m, ds, InetAddress.getLocalHost(), 8000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    private void close(){
        scanner.close();
        System.exit(-1);
    }

    public static void main(String [] args){

        ChatClient client = new ChatClient();
        client.inputShell();
    }

    /**
     * Front end that user enters chats and displays chats from others
     */
    public void inputShell(){

        while(true) {
            try {

                // Read a line
                String chat = scanner.nextLine();
                sendChat(chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listens for incoming chats
     */
    class ListenerThread implements Runnable {

        DatagramSocket socket;
        VectorClock vc;
        int pid;

        public ListenerThread(DatagramSocket socket, VectorClock vc, int pid) {
            this.socket = socket;
            this.vc = vc;
            this.pid = pid;
        }


        public void run() {

            PriorityQueue<Message> queue = new PriorityQueue<>(new MessageComparator());

            while (true) {
                Message messageIn = Message.receiveMessage(socket);
                queue.add(messageIn);
                Message topMsg = queue.peek();
                while (topMsg != null) {
                    // Check to make sure that top message is indeed the next message we are
                    // expecting to receive
                    if (isNextMessage(topMsg.ts, topMsg.pid)){
                        topMsg = queue.poll();
                        System.out.println(topMsg.sender + ":  " + topMsg.message);
                        vc.update(topMsg.ts);
                        topMsg = queue.peek();
                    } else{
                        // No, receive next message
                        topMsg = null;
                    }
                }
            }
        }

        private boolean isNextMessage(VectorClock inboundClock, int inboundPid){
            // If not already in our vector clock, add it with count 0
            if(vc.getTime(inboundPid) == -1) {
                vc.addProcess(inboundPid, 0);
            }

            // First make sure time from inbound pid is next val in our clock
            if(inboundClock.getTime(inboundPid) == vc.getTime(inboundPid) + 1){
                for(Map.Entry<String, Integer> e : inboundClock.clock.entrySet()){
                    int thisPid = Integer.parseInt(e.getKey());
                    // If any other pid times are higher than what's in our clock
                    // return false as we are missing messages
                    if(thisPid != inboundPid && e.getValue() > vc.getTime(thisPid)){
                        return false;
                    }
                }

                // We can be sure this is the next value
                return true;
            }
            // We are missing messages, return false
            return false;
        }
    }
}
