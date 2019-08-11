package Communication;

import java.io.*;  
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.Vector;

import Communication.Message.MessageType;
import Player.Player;  

//This class represents the server. It creates 1 thread that allows the user to send an exit message to terminate the server
//and it creates 1 thread for every (distinct) player that connects to it. This thread is in a loop to see what kind of message comes in (switch statement)
//when 10 messages have been send, the system shuts down all connections.
public class ServerMessenger {

	protected static boolean hasStopped = false;
	protected static int port = 5054;
	protected static ServerSocket ss;
	static Vector<ClientHandler> clientHandlers = new Vector<>();
	static Map<ClientHandler,List<UUID>> clientHandlerIDs = new HashMap<>();
	static int numberOfClients;

	public static void main(String[] args){
		try {
			initializeServerVariables(); 

			while (!hasStopped)  
			{ 
				try
				{ 
					Socket s = ss.accept(); 
					
					ClientHandler clienthandler = initializeClientHandler(s);

					Thread t = clienthandler;
					t.start(); 

					numberOfClients++;

				} 
				catch (SocketException e){ 
					e.printStackTrace();
				} 
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static ClientHandler initializeClientHandler(Socket s) {
		ClientHandler clienthandler = new ClientHandler(s);
		clientHandlers.add(clienthandler);
		List<UUID> uuids= new ArrayList<>();
		clientHandlerIDs.put(clienthandler, uuids);
		return clienthandler;
	}

	private static void initializeServerVariables() throws IOException {
		ss = new ServerSocket(port);
		Scanner sc = new Scanner(System.in);

		// create a new exit listener
		ServerInputThread exithandler = new ServerInputThread(sc);
		Thread exitThread = exithandler;
		exitThread.start();
	}
	
	
}

//exithandler class
class ServerInputThread extends Thread{

	Scanner scanner;

	public ServerInputThread(Scanner scanner)  
	{ 
		this.scanner = scanner; 
	} 

	@Override
	public void run() {
		while(true) {
			String command;
			command = scanner.nextLine();
			if(command.equalsIgnoreCase("exit")) {
				ServerMessenger.hasStopped = true;
				try {
					ServerMessenger.ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}

//ClientHandler class 
class ClientHandler extends Thread  
{ 
	private DataInputStream dis; 
	private DataOutputStream dos; 
	final Socket s;
	private UUID id;
	boolean isloggedin;
	boolean isRegistered;


	// Constructor 
	public ClientHandler(Socket s)  
	{ 
		this.s = s; 
		this.isloggedin = true; 
		setupStreams();
	} 

	private void setupStreams() {
		try {
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run()  
	{ 
		Message receivedMessage = new Message.Builder("transfer").build();
		try(s) { 
			while(this.isloggedin) {
				String messageVariables = dis.readUTF();
				receivedMessage.populateWithString(messageVariables);

				switch(receivedMessage.getType()){
				case REGISTER:
					addPlayer(receivedMessage);
					sendMessageToAllPlayers(makeRegisterMessage(receivedMessage.getText(),receivedMessage.getSender()));
					break;
				case LOGOUT:
					System.out.println("exiting system");
					closeAll();
					break; 
				case SEND:
					writeMessageToClient(receivedMessage);
					break;
				case RECEIVE:
					writeMessageToClient(receivedMessage);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) { 
			//e.printStackTrace(); 
		} 
	}
	
	private Message makeRegisterMessage(String name,UUID uuid) {
		return new Message.Builder(name)
				.withSender(uuid)
				.withType(MessageType.REGISTER)
				.build();
	}

	private void addPlayer(Message messageToSend) {
		this.id = messageToSend.getSender();
		this.isRegistered = true;
		ServerMessenger.clientHandlerIDs.get(this).add(messageToSend.getSender());
		ServerMessenger.clientHandlers.add(this);
	}

	private void closeAll() throws IOException {
		for (Map.Entry<ClientHandler,List<UUID>> entry : ServerMessenger.clientHandlerIDs.entrySet()) { 
			ClientHandler ch = entry.getKey();			
			ch.close();
		}
		ServerMessenger.hasStopped = true;
		ServerMessenger.ss.close();
		System.exit(0);
	}
	
	private void close() throws IOException {
		this.dos.writeUTF("END");
		this.dos.flush();
		this.isloggedin = false; 
		this.dis.close(); 
		this.dos.close(); 
		this.s.close();
	}

	private void writeMessageToClient(Message message) throws IOException {
		for (Map.Entry<ClientHandler,List<UUID>> entry : ServerMessenger.clientHandlerIDs.entrySet()) { 
			ClientHandler ch = entry.getKey();
			
			if (entry.getValue().contains(message.getReceiver()) && ch.isloggedin && ch.isRegistered)  
			{
				ch.dos.writeUTF(message.convertToString());
				break; 
			} 
		}
	}
	
	//won't send to not logged in users!
	private void sendMessageToAllPlayers(Message message) throws IOException {
		for (Map.Entry<ClientHandler,List<UUID>> entry : ServerMessenger.clientHandlerIDs.entrySet()) { 
			ClientHandler ch = entry.getKey();
			if (ch.isloggedin)  
			{
				ch.dos.writeUTF(message.convertToString());
			} 
		}
	}	
} 
