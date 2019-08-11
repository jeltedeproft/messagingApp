package messageApplicationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import CommunicationTest.LocalMessengerTest;
import CommunicationTest.MessageTest;
import CommunicationTest.NetworkMessengerTest;
import PlayerTest.PlayerTest;

@RunWith(Suite.class)
@SuiteClasses({ LocalMessengerTest.class, 
	MessageTest.class, 
	NetworkMessengerTest.class, 
	PlayerTest.class })

public class AllTests {
	
}
