package Communication;

import java.io.Serializable;
import java.util.UUID;

//this class represents a message that can be send. I opted to use the builder pattern here
//because often I do not need all of the variables in a message
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String DELIMITER = "~";

	public enum MessageType{
		SEND,
		RECEIVE,
		REGISTER,
		LOGOUT
	}
	
	private String text;
	private UUID idSender;
	private UUID idReceiver;
	private int counter;
	private MessageType type;
	private Boolean network;
	
	
	private Message() {
		
	}
	
	public UUID getReceiver() {
		return idReceiver;
	}
	
	public UUID getSender() {
		return idSender;
	}
	
	public String getText() {
		return text;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public Boolean getNetwork() {
		return network;
	}
	
	public String convertToString() {
		return text + DELIMITER 
				+ idSender + DELIMITER
				+ idReceiver + DELIMITER
				+ counter + DELIMITER
				+ type.name() + DELIMITER
				+ network;
	}
	
	public void populateWithString(String values) {
		String [] valuesArray =  values.split(DELIMITER);
		this.text = valuesArray[0];
		this.idSender = parseUUID(valuesArray[1]);
		this.idReceiver = parseUUID(valuesArray[2]);
		this.counter = Integer.parseInt(valuesArray[3]);
		this.type = MessageType.valueOf(valuesArray[4]);
		this.network = Boolean.parseBoolean(valuesArray[5]);
	}
	
	private UUID parseUUID(String uuid) {
		if(uuid.equalsIgnoreCase("null")) {
			return null;
		}else {
			return java.util.UUID.fromString(uuid);
		}
	}
		
	
	public static class Builder {

		private String text;
		private UUID idSender;
		private UUID idReceiver;
		private int counter;
		private MessageType type;
		private Boolean network;


		public Builder(String text) {
			this.text = text;
		}

		public Builder withSender(UUID idSender) {
			this.idSender = idSender;

			return this;
		}

		public Builder withReceiver(UUID idReceiver) {
			this.idReceiver = idReceiver;

			return this;
		}

		public Builder withCounter(int counter) {
			this.counter = counter;

			return this;
		}

		public Builder withType(MessageType type) {
			this.type = type;

			return this;
		}
		
		public Builder withNetwork(Boolean network) {
			this.network = network;

			return this;
		}
		
		public Message build() {
			Message message = new Message();
			message.text = this.text;
			message.idSender = this.idSender;
			message.idReceiver = this.idReceiver;
			message.counter = this.counter;
			message.type = this.type;
			message.network = this.network;
			return message;
		}
	}
}
