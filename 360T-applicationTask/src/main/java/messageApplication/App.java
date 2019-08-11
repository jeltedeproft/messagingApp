package messageApplication;

import java.util.Scanner;

import Communication.Message;
import Communication.Messenger;
import Communication.Message.MessageType;
import Player.Player;

//This class is the main starting point for the client. It initiates a conversation loop with the player
//the commans are :
//SEND : sends a message to another player (which can be local or network)
//REGISTER : registers a new player
//LOGOUT : terminate the client

public class App 
{
	private static Scanner scanner;
	private static Messenger messenger;
	
	private static String message;
	private static String name;
	private static String recipient;
	private static int timesToSendMessage = 1;
	
	private static Player me;
	private static boolean loggedOn = true;
	
	
    public static void main( String[] args ){
    	messenger = new Messenger();
        scanner = new Scanner(System.in);  
    	introduction();
    }
    
    private static void introduction() {

        System.out.println("This messaging app allows you to send messages either locally or across different java processes.");
        System.out.println("for network communication, first start up the server at port 5054 : ");
        
        System.out.println("type your name : ");       
        name = scanner.nextLine();
        me = new Player(messenger,name);
        messenger.registerPlayer(me);
        
        inputLoop();
    }
    
    private static void inputLoop() {
    	while(loggedOn) {
	        System.out.println("type 'SEND' to start sending a message, 'REGISTER' to register another player or 'LOGOUT' to disconnect");   
	        String command = scanner.nextLine();
	        processCommand(command);
    	}
    }
    
    private static void processCommand(String command) {
    	switch(command) {
    	case "SEND":
    		getRecipient();
    		for(int i=0; i<timesToSendMessage ;i++) {
    			messenger.sendMessage(name,message,recipient);
    		}
    		timesToSendMessage = 1;
    		break;
    	case "REGISTER":
    		registerPlayer();
    		break;
    	case "LOGOUT":
    		messenger.sendMessage(createLogoutMessage());
    		messenger.getNetworkMessenger().stopConnection();
    		loggedOn = false;
    		System.exit(0);
    	}
    }
    
    private static Message createLogoutMessage() {
    	return new Message.Builder("END")
				.withType(MessageType.LOGOUT)
				.withNetwork(true)
				.build();
    }
    
    private static void getRecipient() {
    	System.out.println("Who do you want to send it to?");
    	recipient = scanner.nextLine();
		if(messenger.checkIfPlayerExists(recipient)) {
			getMessage();
		}else {
			System.out.println("Player not existing");
			getRecipient();
		}
    }
    
    private static void getMessage() {
		System.out.println("What is the message?");
		message = scanner.nextLine();
		getTimes();
    }
    
    private static void getTimes() {
		System.out.println("How many times?");
		timesToSendMessage = Integer.parseInt(scanner.nextLine());
    }
    
    private static void registerPlayer() {
    	System.out.println("What is the name?");
    	name = scanner.nextLine();
    	Player player = new Player(messenger,name);
    	messenger.registerPlayer(player);
    }
}
