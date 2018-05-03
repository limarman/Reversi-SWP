/**
 * 
 */
package swpg3.main;

import java.io.IOException;

import swpg3.Move;
import swpg3.net.Message;
import swpg3.net.MessageType;
import swpg3.net.NetworkManager;
import swpg3.net.WrongMessageException;

/**
 * Main Programm class as Singleton
 * @author eric
 *
 */
public class Phteven{
	//###############################################################
	//					Singleton stuff
	//###############################################################
	private static Phteven instance = null;
	
	private Phteven()
	{
		
	}
	public static Phteven getInstance()
	{
		if(instance == null)
		{
			instance = new Phteven();
		}
		return instance;
	}
	
	//###############################################################
	//					static stuff
	//###############################################################
	public static byte getPlayerNumber()
	{
		return getInstance().playerNumber;
	}
	//###############################################################
	//					not Singleton stuff
	//###############################################################
	private byte groupNumber = 3;
	private byte playerNumber;
	private boolean errorLog;
	private boolean shouldClose;
	private NetworkManager net;
	
	/**
	 * Initializes Phteven by connecting to the server and transmitting the group number Message.
	 * @param hostname
	 * @param port
	 * @param verbose true, if logging to console is wished
	 */
	public void initialize(String hostname, int port, boolean errorLog)
	{
		this.errorLog = errorLog;
		// Connect to server
		net = NetworkManager.initialize(hostname, port);
		if(!net.isConnected())
		{
			if(errorLog) {System.out.println("ERROR: Could connect to server!");}
			System.exit(1);
		}
		
		// Send GroupNumber
		try
		{
			net.sendMessage(Message.newGroupNumberInitMessage(groupNumber));
		} catch (IOException e)
		{
			if(errorLog) {System.out.println("ERROR: Could not send Message (1)!");}
			cleanUp();
			System.exit(1);
		}
		
	}
	
	/**
	 * main Event Loop of Program.
	 * Receives a Message and handles it
	 */
	public void mainLoop()
	{
		shouldClose = false;
		
		while(!shouldClose)
		{
			Message message = null;
			try
			{
				message = net.getNewMessage();
			} catch (IOException e)
			{
				if(errorLog) {System.out.println("ERROR: Could not read message!");}
				shouldClose = true;
				break;
			}
			
			try
			{
				handleMessage(message);
			} catch (WrongMessageException e)
			{
				if(errorLog) {System.out.println("ERROR: Can't handle the Message");} // should never occur
			}
			
		}
	}
	
	/**
	 * Cleans up before closing program
	 */
	public void cleanUp()
	{
		if(!net.close()) {System.out.println("ERROR_ Could not clean up properly!");}
	}
		
	/**
	 * handles an incoming message
	 * @param m Message to be handled
	 * @throws WrongMessageException 
	 */
	private void handleMessage(Message m) throws WrongMessageException
	{
		if(m.getType() == MessageType.MAP_INIT) // MessageType 2
		{
			String map = m.retrieveMap();
			// Initialize Map
			// TODO ...
		}
		else if(m.getType() == MessageType.PLAYER_NUMBER_ASSIGN) // MessageType 3
		{
			// Assign playernumber:
			playerNumber = m.retrievePlayerNumber();
		}
		else if(m.getType() == MessageType.MOVE_REQUEST) // MessageType 4
		{
			// Retrieve limits
			int timeLimit = m.retrieveTimeLimit();
			int depthLimit = m.retrieveDepthLimit();
			// Request Move from AI
			
		}
		else if(m.getType() == MessageType.MOVE_ANNOUNCE) // MessageType 6
		{
			Move move = m.retrieveAnouncedMove();
			// Apply Move to the map
			
		}
		else if(m.getType() == MessageType.DISQUALIFICATION) // MessageType 7
		{
			byte disqualified = m.retrieveDisqualifiedPlayer();
			// remove Disqualified player.
			// if its us: eixt programm
		}
		else if(m.getType() == MessageType.END_FIRST_PHASE) // MessageType 8
		{
			// AI.enterBombingPhase()
		}
		else if(m.getType() == MessageType.END_SECOND_PHASE) // MessageType 9
		{
			// Exit Program
			shouldClose = true;
		}
		else if(m.getType() == MessageType.CURRENT_GAME_STATE) // MessageType 10
		{
			//String state = m.retrieveGameState();
			// ignore or update Map:
		}
		else
		{
			throw new WrongMessageException();
		}
	}
	
	//###############################################################
	//					Entry Point
	//###############################################################
	/**
	 * Main Entry Point
	 * @param args CommandLine Arguments. Must be hostname and port
	 */
	public static void main(String[] args)
	{
		//Check cmdArgs:
		if(args.length != 2)
		{
			System.out.println("ERROR: Call with hostname and port as parameters");
			System.exit(0);
		}
		int port = -1;
		
		try {
			port = Integer.parseInt(args[1]);
		}catch(Exception e)
		{
			System.out.println("ERROR: port must be a number");
			System.exit(1);
		}
		
		
		// In piece may he rest
		Phteven hooking = Phteven.getInstance();
		
		hooking.initialize(args[0], port, true);
		
		// main Event Loop:
		hooking.mainLoop();
		
		// Exit clean up:
		hooking.cleanUp();
	}

}
