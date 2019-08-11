package CommunicationTest;

import org.junit.Test;

import Communication.LocalMessenger;
import Communication.Messenger;
import Player.Player;

import org.junit.Assert;

public class LocalMessengerTest {
	Messenger messenger;
	Player initiator;
	Player receiver;
	
	@Test
	public void testIfUserGetsRegistered() {
		messenger = new Messenger();
    	
        initiator = new Player(messenger,"bob");
        receiver = new Player(messenger,"lisa");
        
        messenger.registerPlayer(initiator);
        messenger.registerPlayer(receiver);
        
        Assert.assertTrue(messenger.checkIfPlayerExists("bob"));
        Assert.assertEquals(initiator, messenger.getPlayerstoUUID().get(initiator.getUUID()));
	}

}
