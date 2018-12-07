package edu.gvsu.cis.cis656.lab5.client;//import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.representation.Representation;

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
//import java.util.Vector;


//import edu.gvsu.cis.cis656.lab2.PresenceService;
//import edu.gvsu.cis.cis656.lab2.edu.gvsu.cis.cis656.lab5.client.RegistrationInfo;

/************************************************
 * edu.gvsu.cis.cis656.lab5.client.ChatClient connects to a PresenceService using
 * Java RMI
 * 
 * @author Nick Sutton
 *
 ***********************************************/
public class ChatClient {

	private String chatServer;
	private int chatServerPort;
//	public static final String APPLICATION_URI = "http://localhost:8080";
	private String chatURL = "https://";
	private ServerSocket inboundSocket;
	private int port;
	private String hostname;
	private RegistrationInfo reginfo;
	private String username;
	private boolean myStatus;
	
	public ChatClient(String username, String host, int port) {
		this.username = username;
		chatServer = host;
		chatServerPort = port;
		this.port = 9999;
	}
	
	public void init() {
		

		chatURL += chatServer + ":" + chatServerPort;
		try {
			if(!register()) {
				System.err.println("Error registering with service.");
				Thread.sleep(3000);
				System.exit(0);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myStatus = true;
	}

	private boolean register(){
		Form form = new Form();

		form.add("name", username);
		form.add("host", hostname);
		form.add("port", String.valueOf(port));

		// construct request to create a new widget resource
		String usersResourceURL = chatURL + "/v1/users";
		Request request = new Request(Method.POST, usersResourceURL);

		// set the body of the HTTP POST command with form data.
		request.setEntity(form.getWebRepresentation());

		// Invoke the client HTTP connector to send the POST request to the server.
		Response resp = new Client(Protocol.HTTP).handle(request);


		// now, let's check what we got in response.
		return resp.getStatus().isSuccess();
	}

	private RegistrationInfo[] listRegisteredUsers(){
		RegistrationInfo[] regusers = null;
		// Let's do an HTTP GET of widget 1 and ask for JSON response.
		String usersResourceURL = chatURL + "/v1/users";
		Request request = new Request(Method.GET,usersResourceURL);

		// We need to ask specifically for JSON
		request.getClientInfo().getAcceptedMediaTypes().
				add(new Preference(MediaType.APPLICATION_JSON));
		Response resp = new Client(Protocol.HTTP).handle(request);

		// Let's see what we got!
		if(resp.getStatus().equals(Status.SUCCESS_OK)) {
			Representation responseData = resp.getEntity();
			try {
				String jsonString = responseData.getText();
				JSONArray jObj = new JSONArray(jsonString);
				regusers = new RegistrationInfo[jObj.length()];
				for(int i = 0; i < regusers.length; i++){
					RegistrationInfo r = new RegistrationInfo();
					JSONObject jsonUser = jObj.getJSONObject(i);
					r.setUserName(jsonUser.getString("username"));
					r.setHost(jsonUser.getString("host"));
					r.setPort(jsonUser.getInt("port"));
					r.setStatus(jsonUser.getBoolean("status"));
					regusers[i] = r;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException je) {
				je.printStackTrace();
			}
		}

		return regusers;

	}

	public String getConnectedUsers() {
		RegistrationInfo[] currentUsers;
		String results = "Other connected users:\r\n";
		currentUsers = listRegisteredUsers();
		String available = "Available";
		String busy = "Busy";
		String currentStatus;
		if(currentUsers.length == 1) {
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
		return results;
	}

	private void setStatus(boolean status){

		Form form = new Form();
		form.add("status", String.valueOf(status));

		// construct request to create a new widget resource
		String usersResourceURL = chatURL + "/v1/users/" + username;
		Request request = new Request(Method.PUT, usersResourceURL);

		// set the body of the HTTP PUT command with form data.
		request.setEntity(form.getWebRepresentation());

		// Invoke the client HTTP connector to send the PUT request to the server.
		Response resp = new Client(Protocol.HTTP).handle(request);
		myStatus = status;
	}

	public boolean setBusy() {
		if(!myStatus) {
			return false;
		}
		setStatus(false);
		return true;
	}

	public boolean setAvailable() {
		if(myStatus) {
			return false;
		}
		setStatus(true);
		return true;
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
		for(RegistrationInfo reg : listRegisteredUsers()) {
			if(!reg.getUserName().equals(username) && reg.getStatus()){
				sendChat(reg.getHost(), reg.getPort(), "Broadcast from " + this.username + ":  " + message);
			}
		}
	}

	public void sendDirectMessage(String username, String message) {

		for(RegistrationInfo reg : listRegisteredUsers()) {
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

	private void unregister(){
		String usersResourceURL = chatURL + "/v1/users/" + username;
		Request request = new Request(Method.DELETE, usersResourceURL);

		Response resp = new Client(Protocol.HTTP).handle(request);
	}
	
	public void exitChat() {
		try {
			unregister();
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
		int rmiPort = 8080;

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
		ChatClient client;
//		try {
			
			client = new ChatClient(username, rmiHost, rmiPort);

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
//		} catch(Exception e) {
			// do something
			System.out.println("Cannot connect to the server");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//		}
		
		System.out.println("Good bye, " + username + "!");
	}
}
