package edu.gvsu.cis.cis656.lab2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ProcessIncomingRequest implements Runnable {
    private ServerSocket inboundSocket;
    private Socket socket;

    public ProcessIncomingRequest(ServerSocket inboundSocket) {
        super();
        this.inboundSocket = inboundSocket;
    }

    @Override
    public void run() {
        String line;
        BufferedReader is;

        
        
        while(!inboundSocket.isClosed()) {
        	try {
        		socket = inboundSocket.accept();
        		is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        		line = is.readLine();
        		System.out.println(line);
        		System.out.print(">>  ");
        		is.close();


        	} catch (IOException e) {
        		
        	}

        }

    }
}
