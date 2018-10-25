package edu.gvsu.cis.cis656.lab3.client;

import edu.gvsu.cis.cis656.lab3.RegistrationInfo;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;

public class BroadcastSubscriptionService implements Runnable{

    private Context context;
    private String user;

    BroadcastSubscriptionService(Context context, String user){
        this.context = context;
        this.user = user;
    }

    @Override
    public void run() {

        //  Socket to talk to server

        Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:9999");
        subscriber.subscribe("".getBytes());


        while (!Thread.currentThread().isInterrupted()) {
            try{
                String broadcast = subscriber.recvStr(0);
                System.out.println(broadcast);
                System.out.println(">>  ");
//                String[] splitString = broadcast.split("|");
//                if(!(splitString[0] == user)) {
//                    System.out.println(splitString[1]);
//                    System.out.print(">>  ");
//                }
            }
            catch(Exception e){
                return;
            }


        }

//        ZMQ.Poller poller = new ZMQ.Poller(1);
//        poller.register(subscriber, ZMQ.Poller.POLLIN);
//        while (!Thread.currentThread().isInterrupted()) {
//            poller.poll(100);
//            if (poller.pollin(0)) {
//                String content = subscriber.recvStr();
//                System.out.println(content);
//            }
//        }

        System.out.println("Exited thread bcast");
    }
}
