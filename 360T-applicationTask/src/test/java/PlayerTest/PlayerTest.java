package PlayerTest;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import Communication.LocalMessenger;
import Communication.Message;
import Communication.Message.MessageType;
import Communication.Messenger;
import Player.Player;
import org.junit.Assert;

public class PlayerTest {
	Messenger messenger;
	Player initiator;
	Player receiver;
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Before
	public void setup() {
		messenger = new Messenger();
    	
        initiator = new Player(messenger,"bob");
        receiver = new Player(messenger,"lisa");
        
        messenger.registerPlayer(initiator);
        messenger.registerPlayer(receiver);
	}
	
	@Test
	public void testPlayersAreRegistered(){
		Map<UUID,Player> players = messenger.getPlayerstoUUID();
		Assert.assertTrue(players.containsKey(initiator.getUUID()));
		Assert.assertTrue(players.containsKey(receiver.getUUID()));
	}
	
    @Test
    public void testSendOneMessage() {
    	initiator.sendMessage("testing message send function ","lisa");
    	Assert.assertEquals(1, initiator.getMessageCountForPlayer(receiver.getUUID()));
    }
    
    @Test
    public void testSendMultipleMessages() {
    	initiator.sendMessage("testing 1 message ","lisa");
    	Assert.assertEquals(1, initiator.getMessageCountForPlayer(receiver.getUUID()));
    	
    	initiator.sendMessage("testing 2 messages ","lisa");
    	Assert.assertEquals(2, initiator.getMessageCountForPlayer(receiver.getUUID()));
    	
    	initiator.sendMessage("testing 3 messages ","lisa");
    	Assert.assertEquals(3, initiator.getMessageCountForPlayer(receiver.getUUID()));
    }
    
    @Test
    public void testSendEmptyMessage() {
    	initiator.sendMessage("","lisa");
    	Assert.assertEquals(1, initiator.getMessageCountForPlayer(receiver.getUUID()));
    }
    
    @Test
    public void testSendNonExistingUUID() {
    	initiator.sendMessage("this UUID does not exist","");
    	Assert.assertEquals(0, initiator.getMessageCountForPlayer(java.util.UUID.randomUUID()));
    }
    
    @Test
    public void testReceiveMessage() {
		Message message = new Message.Builder("this is a test message")
				.withSender(receiver.getUUID())
				.withCounter(5)
				.withReceiver(initiator.getUUID())
				.withType(MessageType.RECEIVE)
				.build();
    	initiator.receiveMessage(message);
    	Assert.assertEquals(1, initiator.getMessageCountForPlayer(receiver.getUUID()));
    }
    
    @Test
    public void testExitWhen10Messages() {
    	messenger.sendMessage("bob","1","lisa");
    	messenger.sendMessage("bob","2","lisa");
    	messenger.sendMessage("bob","3","lisa");
    	messenger.sendMessage("bob","4","lisa");
    	messenger.sendMessage("bob","5","lisa");
    	messenger.sendMessage("bob","6","lisa");
    	messenger.sendMessage("bob","7","lisa");
    	messenger.sendMessage("bob","8","lisa");
    	messenger.sendMessage("bob","9","lisa");
    	exit.expectSystemExitWithStatus(0);
    	messenger.sendMessage("bob","10","lisa");
    }
}
