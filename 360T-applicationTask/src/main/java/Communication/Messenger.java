package Communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Communication.Message.MessageType;
import Player.Player;

//This class is responsible for deciding whether a message should be send trough the network or trough a local method
//It encapsulates the player from the method used to send the message.
public class Messenger {
	private LocalMessenger localMessenger;
	private NetworkMessenger networkMessenger;
	private List<Player> players = new ArrayList<>();
	private Map<UUID,Player> playerstoUUID = new HashMap<UUID,Player>();
	private Map<String,UUID> nameToUUID = new HashMap<String,UUID>();
	
	public Messenger() {
		networkSetup();
		localSetup();
	}
	
    private void networkSetup() {
    	networkMessenger = new NetworkMessenger(this);
    }
	
    private void localSetup() {
    	localMessenger = new LocalMessenger();
    }
	
	public void registerPlayer(Player player) {
    	//register locally
    	players.add(player);
    	playerstoUUID.put(player.getUUID(), player);
    	nameToUUID.put(player.getName(), player.getUUID()); 
    	
		//register on server
    	Message registerMessage = new Message.Builder(player.getName())
				.withSender(player.getUUID())
				.withType(MessageType.REGISTER)
				.withNetwork(true)
				.build();
    	networkMessenger.sendMessage(registerMessage);
    	networkMessenger.registerPlayer(player);
	}
	
	public void sendMessage(Message message) {
		if(playerLocallyExists(message.getReceiver())) {
			localMessenger.sendMessage(message,playerstoUUID.get(message.getReceiver()));
		}else{
			networkMessenger.sendMessage(message); 
		}
	}
	
	public void sendMessage(String sender,String text,String receiver) {
		if(playerLocallyExists(receiver)) {
			Message message = createMessage(sender,text,receiver,false);
			localMessenger.sendMessage(message,playerstoUUID.get(getUUIDFromName(receiver)));
		}else{
			Message message = createMessage(sender,text,receiver,true);
			networkMessenger.sendMessageWithReceiver(message, receiver); 
		}
	}
	
	private Message createMessage(String sender,String text,String receiver, Boolean network) {
		Player senderPlayer = playerstoUUID.get(getUUIDFromName(sender));
		int counter = senderPlayer.getMessageCountForPlayer(receiver);
		return new Message.Builder(text)
				.withSender(getUUIDFromName(sender))
				.withCounter(counter)
				.withReceiver(getUUIDFromName(receiver))
				.withType(MessageType.SEND)
				.withNetwork(network)
				.build();
	}
	
    public Boolean checkIfPlayerExists(String name) {
    	return (playerLocallyExists(name) || playerNetworkExists(name));	
    }
    
    private boolean playerLocallyExists(String name) {
    	return playerstoUUID.containsKey(getUUIDFromName(name));
    }
    
    private boolean playerLocallyExists(UUID uuid) {
    	return playerstoUUID.containsKey(uuid);
    }
    
    private boolean playerNetworkExists(String name) {
    	return networkMessenger.playerExistsOnServer(name);
    }
    
	public UUID getUUIDFromName(String name) {
		return nameToUUID.get(name);
	}
	
	public LocalMessenger getLocalMessenger() {
		return localMessenger;
	}
	
	public NetworkMessenger getNetworkMessenger() {
		return networkMessenger;
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public Map<UUID,Player> getPlayerstoUUID(){
		return playerstoUUID;
	}
}
