package edu.gvsu.cis.cis656.lab2.client;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.PrintStream;
//import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Vector;


import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

/************************************************
 * ChatClient connects to a PresenceService using
 * Java RMI
 * 
 * @author Nick Sutton
 *
 ***********************************************/
public class ChatClient {

	private PresenceService service;
	private ServerSocket inboundSocket;
	private int port;
	private String hostname;
	private RegistrationInfo reginfo;
	private String username;
	
	public ChatClient(PresenceService service, String username) {
		this.service = service;
		this.username = username;
		port = 9999;
	}
	
	public void init() {
		
		reginfo = new RegistrationInfo(username, hostname, port, true);
		try {
			if(!service.register(reginfo)) {
				System.err.println("Username already in use. Please select another.");
				Thread.sleep(3000);
				System.exit(0);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getConnectedUsers() {
		Vector<RegistrationInfo> currentUsers;
		String results = "Other connected users:\r\n";
		try {
			currentUsers = service.listRegisteredUsers();
			String available = "Available";
			String busy = "Busy";
			String currentStatus;
			if(currentUsers.size() == 1) {
				results = "You are the only connected user";
				return results;
			}
			for(RegistrationInfo user : currentUsers) {
				if(!user.getUserName().equals(username)) {
					if(user.getStatus())
						currentStatus = available;
					else
						currentStatus = busy;
					results += user.getUserName() + " - " + currentStatus + "\r\n";
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public boolean setBusy() {
		if(!reginfo.getStatus()) {
			return false;
		}
		reginfo.setStatus(false);
		try {
			service.updateRegistrationInfo(reginfo);
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean setAvailable() {
		if(reginfo.getStatus()) {
			return false;
		}
		reginfo.setStatus(true);
		try {
			service.updateRegistrationInfo(reginfo);
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void startInboundMessageService() {
		inboundSocket = null;

        while(true) {
        	try {
        		inboundSocket = new ServerSocket(port);
        		break;
        	} catch (IOException e) {
        		// Port in use. Select new port;
        		port--;
        	}
        }
        
        hostname = inboundSocket.getInetAddress().getHostAddress();

        Thread thread  = new Thread(new ProcessIncomingRequest(inboundSocket));
        thread.start();
	}
	
	public void sendBroadcastMessage(String message) {
		try {
			for(RegistrationInfo reg : service.listRegisteredUsers()) {
				if(!reg.getUserName().equals(username) && reg.getStatus()){
					sendChat(reg.getHost(), reg.getPort(), "Broadcast from " + this.username + ":  " + message);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendDirectMessage(String username, String message) {
		
		try {
			for(RegistrationInfo reg : service.listRegisteredUsers()) {
				if(reg.getUserName().equals(username)){
					if(reg.getStatus()) {
						sendChat(reg.getHost(), reg.getPort(), "DM from " + this.username + ":  " + message);
					} else {
						System.out.println("Sorry, " + username + " is busy and cannot chat");
					}
					return;
				}
			}
			System.out.println("Sorry, " + username + " is not connected");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendChat(String host, int port, String message) {
		try {
			PrintStream os;

			Socket clientSocket = new Socket(host, port);
			os = new PrintStream(clientSocket.getOutputStream());

			os.println(message);
			os.close();
			clientSocket.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exitChat() {
		try {
			service.unregister(username);
			inboundSocket.close();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		if(args.length < 1) {
			System.err.println("Missing username. Exiting");
			System.err.println("Press any key to exit...");
			Scanner s = new Scanner(System.in);
			if(s.hasNext()) {
				s.close();
				System.exit(0);
			}
		}

		String username = args[0];
		String rmiHost = "localhost";
		int rmiPort = 1099;

		if(args.length > 1) {
			String temphost = args[1];

			if(temphost != null) {
				// Need more sanitation here...
				if(temphost.indexOf(":") >= 0) {
					rmiHost = temphost.substring(0, temphost.indexOf(":"));
					rmiPort = Integer.parseInt(temphost.substring(temphost.indexOf(":") + 1));
				} else {
					rmiHost = args[1];
				}
			}
		}

		// Connect to Presence Service and register
		PresenceService service;
		ChatClient client;
		try {
			String name = "PresenceService";
			Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
			service = (PresenceService) registry.lookup(name);
			
			client = new ChatClient(service, username);

			client.startInboundMessageService();
			client.init();
			
			System.out.println("Welcome, " + username + ", you are now connected!");

			Scanner chatScanner = new Scanner(System.in);
			while(true) {
				System.out.print(">>  ");
				String readline = chatScanner.nextLine();

				if(readline.equals("friends")){
					System.out.println(client.getConnectedUsers());
				} else if(readline.startsWith("talk")) {
					String newString = readline.substring(4).trim();
					String recipient = newString.substring(0, newString.indexOf(" "));
					String message = newString.substring(newString.indexOf(" ") + 1).trim();
					if(recipient.equals(username)) {
						System.out.println("One may not talk to oneself");
					}
					client.sendDirectMessage(recipient, message);
				} else if(readline.startsWith("broadcast")) {
					client.sendBroadcastMessage(readline.substring(readline.indexOf(" ") + 1));
				} else if(readline.equals("busy")) {
					if(client.setBusy()) {
						System.out.println("Status successfully changed to busy");
					} else {
						System.out.println("You are already busy");
					}
				} else if(readline.equals("available")) {
					if(client.setAvailable()) {
						System.out.println("Status successfully changed to available");
					} else {
						System.out.println("You are already available");
					}
				} else if(readline.equals("exit")){
					client.exitChat();
					chatScanner.close();
					break;
				} else {
					System.out.println("I'm sorry, " + username + ", I'm "
							+ "afraid I can't do that.");
				}
			}
		} catch(Exception e) {
			// do something
			System.out.println("Cannot connect to the server");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		System.out.println("Good bye, " + username + "!");
	}
}
