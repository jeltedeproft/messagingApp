package Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import Communication.Message.MessageType;
import Player.Player;

//This class is responsible for sending messages across the network. It uses a thread that constantly watches
//for incoming messages. When 10 messages have been send,the class stops the client.
public class NetworkMessenger{
	private final static String IP = "127.0.0.1";
	private final static int PORT = 5054;
	private Map<UUID,Player> players = new HashMap<UUID,Player>();

	private Messenger messenger;
	private Socket clientSocket;
	private DataOutputStream  dos;
	private DataInputStream dis;

	public NetworkMessenger(Messenger messenger) {
		startConnection(IP,PORT);
		this.messenger = messenger;
	}


	public void startConnection(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			dos = new DataOutputStream(clientSocket.getOutputStream()); 
			dis = new DataInputStream(clientSocket.getInputStream());

			Thread clientThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String message = null;
						while(!(message = dis.readUTF()).equals("END")) {	            		
							Message received = new Message.Builder("transfer").build();
							received.populateWithString(message);

							if(received.getType() == MessageType.REGISTER) {
								registerPlayer(received.getText(),received.getSender());
							}else {
								sendMessageToLocalPlayer(received);
							}
						}

						stopConnection();
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			});
			clientThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void stopConnection(){
		try {
			System.out.println("closing client");
			dis.close();
			dos.close();
			clientSocket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void registerPlayer(Player player) {
		players.put(player.getUUID(), player);
	}

	private void registerPlayer(String name, UUID uuid){
		if (!(players.containsKey(uuid))) {
			players.put(uuid, new Player(messenger,name,uuid));
		}
	}

	public void sendMessage(Message message) {  
		if (canSend(message)) {
			try {
				dos.writeUTF(message.convertToString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessageWithReceiver(Message message, String receiver) {
		if (canSend(message)){
			Message messageWithReceiver = new Message.Builder(message.getText())
					.withSender(message.getSender())
					.withReceiver(getKey(players,receiver))
					.withCounter(message.getCounter())
					.withNetwork(message.getNetwork())
					.withType(message.getType())
					.build();

			try {
				dos.writeUTF(messageWithReceiver.convertToString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessageToLocalPlayer(Message message) {
		if (canSend(message)) players.get(message.getReceiver()).receiveMessage(message);
	}

	private boolean canSend(Message message) {
		return message.getCounter() < 10;
	}

	public Map<UUID, Player> getPlayers() {
		return players;
	}

	public void checkExitCondition(int messagesSend) {
		if(messagesSend >= 10) {
			System.out.println("received 10 messages, shutting down system");
			this.sendMessage(new Message.Builder("END")
					.withType(MessageType.LOGOUT)
					.build());
		}
	}

	public boolean playerExistsOnServer(String name) {
		for (Map.Entry<UUID,Player> entry : players.entrySet()) { 
			if(entry.getValue().getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public UUID getUUIDFromName(String name) {
		return getKey(players,name);
	}

	public UUID getKey(Map<UUID, Player> players, String receiver) {
		for (Entry<UUID, Player> entry : players.entrySet()) {
			if (entry.getValue().getName().equals(receiver)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
