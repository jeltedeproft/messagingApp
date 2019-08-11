package CommunicationTest;

import java.util.UUID;

import org.junit.Test;

import Communication.Message;
import Communication.Message.MessageType;
import org.junit.Assert;

public class MessageTest {

	@Test
	public void testMessageCreation() {
		UUID senderID = java.util.UUID.randomUUID();
		UUID receiverID = java.util.UUID.randomUUID();
		
		Message message = new Message.Builder("testing if this message gets properly created")
				.withSender(senderID)
				.withCounter(5)
				.withReceiver(receiverID)
				.withType(MessageType.SEND)
				.build();
		
		Assert.assertEquals("testing if this message gets properly created", message.getText());
		Assert.assertEquals(senderID, message.getSender());
		Assert.assertEquals(receiverID, message.getReceiver());
		Assert.assertEquals(5, message.getCounter());
		Assert.assertEquals(MessageType.SEND, message.getType());			
	}
	
	@Test
	public void testPartialMessageCreation() {
		Message message = new Message.Builder("testing if this message gets properly created")
				.withCounter(5)
				.withType(MessageType.SEND)
				.build();
		
		Assert.assertEquals("testing if this message gets properly created", message.getText());

		Assert.assertEquals(5, message.getCounter());
		Assert.assertEquals(MessageType.SEND, message.getType());		
	}
	
	@Test
	public void testConvertToString() {
		UUID senderID = java.util.UUID.randomUUID();
		UUID receiverID = java.util.UUID.randomUUID();
		
		Message message = new Message.Builder("to string")
				.withSender(senderID)
				.withCounter(5)
				.withReceiver(receiverID)
				.withType(MessageType.SEND)
				.withNetwork(true)
				.build();
		
		Assert.assertEquals("to string" + Message.DELIMITER +
				senderID.toString() + Message.DELIMITER +
				receiverID.toString() + Message.DELIMITER +
				"5" + Message.DELIMITER +
				"SEND"+ Message.DELIMITER +
				"true",message.convertToString());
	}
}
