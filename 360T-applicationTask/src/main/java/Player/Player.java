package Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import Communication.Message;
import Communication.Message.MessageType;
import Communication.Messenger;

//this is the player class, it has a name and a unique ID. It keeps track of how many messages each other
//player has sent him and formulates an appropriate response to incoming messages.
public class Player {
	
	private UUID uuid = java.util.UUID.randomUUID();
	private String myName;
	
	private Map<UUID,Integer> messageCountOtherPlayers = new HashMap<UUID,Integer>();

	protected Messenger messenger;
	
	public Player(Messenger messenger, String name) {
		this.myName = name;
		this.messenger = messenger;
	}
	
	public Player(Messenger messenger, String name, UUID uuid) {
		this.myName = name;
		this.messenger = messenger;
		this.uuid = uuid;
	}
	
	public void sendMessage(String text,String name) {
		messenger.sendMessage(myName,text,name);
	}
	
	public void receiveMessage(Message message) {
		switch(message.getType()) {
			case SEND:
				Player sendPlayer = messenger.getNetworkMessenger().getPlayers().get(message.getSender());
				returnResponse(message);
				break;
			case RECEIVE:
				incrementMessageCountForPlayer(message);
		}
	}
	
	public void returnResponse(Message message) {
		messenger.sendMessage(new Message.Builder(message.getText())
				.withSender(uuid)
				.withCounter(message.getCounter())
				.withReceiver(message.getSender())
				.withType(MessageType.RECEIVE)
				.withNetwork(message.getNetwork())
				.build());
	}
	
	public void incrementMessageCountForPlayer(Message message) {
		Integer messagesSend = getMessageCountForPlayer(message.getSender());
		System.out.println("received message : " + message.getText() + messagesSend);
		messageCountOtherPlayers.replace(message.getSender(), ++messagesSend);
		
		checkExitCondition(message,messagesSend);
	}
	
	public int getMessageCountForPlayer(UUID id) {
		if(messageCountOtherPlayers.containsKey(id)) {
			return messageCountOtherPlayers.get(id);
		}else {
			messageCountOtherPlayers.put(id, 0);
			return 0;
		}
	}
	
	public int getMessageCountForPlayer(String name) {
		UUID id = messenger.getNetworkMessenger().getUUIDFromName(name);
		if(messageCountOtherPlayers.containsKey(id)) {
			return messageCountOtherPlayers.get(id);
		}else {
			messageCountOtherPlayers.put(id, 0);
			return 0;
		}
	}
	
	private void checkExitCondition(Message message,int messagesSend) {
		if((message.getNetwork() != null) &&  message.getNetwork()){
			messenger.getNetworkMessenger().checkExitCondition(messagesSend);
		}else {
			messenger.getLocalMessenger().checkExitCondition(messagesSend);
		}
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return myName;
	}
}
