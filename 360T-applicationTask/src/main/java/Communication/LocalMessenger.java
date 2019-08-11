package Communication;

import Player.Player;

//This class is responsible for sending messages locally
public class LocalMessenger{
	public void sendMessage(Message message, Player receiver) {
		if(message.getCounter() < 10) {
			receiver.receiveMessage(message);
		}
	}
	
	public void checkExitCondition(int messagesSend) {
		if(messagesSend >= 10) {
			System.out.println("received 10 messages, shutting down system");
			System.exit(0);
		}
	}
}
