package edu.gvsu.cis.cis656.lab3.client;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.concurrent.ExecutorService;

public class InboundReqService implements Runnable {

    private int port;
    private ZMQ.Context context;

    public InboundReqService(ZMQ.Context context, int port){
        this.context = context;
        this.port = port;
    }

    @Override
    public void run() {
//        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to clients
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:" + port);

        while (!Thread.currentThread().isInterrupted()) {
            // Wait for next request from the client
            byte[] request = responder.recv(0);
            System.out.println(new String (request));
            System.out.print(">>  ");

            // Do some 'work'
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Send reply back to client
            String reply = "World";
            responder.send(reply.getBytes(), 0);
        }

        System.out.println("Exited thread msg");
        responder.close();
//        context.term();
    }
}
