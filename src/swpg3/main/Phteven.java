/**
 * 
 */
package swpg3.main;

import java.io.IOException;
import java.util.HashSet;

import swpg3.MapManager;
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
	private boolean shouldClose;
	private NetworkManager net;
	private MapManager mapMan;
	
	/**
	 * Initializes Phteven by connecting to the server and transmitting the group number Message.
	 * @param hostname
	 * @param port
	 * @param verbose true, if logging to console is wished
	 */
	public void initialize(String hostname, int port, boolean errorLog)
	{
		mapMan = MapManager.getInstance();
		// Connect to server
		net = NetworkManager.initialize(hostname, port);
		if(!net.isConnected())
		{
			Logger.log(LogLevel.ERROR, "Could not connect to server!)");
			System.exit(1);
		}
		
		// Send GroupNumber
		try
		{
			net.sendMessage(Message.newGroupNumberInitMessage(groupNumber));
		} catch (IOException e)
		{
			Logger.log(LogLevel.ERROR, "Could not send Message (1)!");
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
				Logger.log(LogLevel.ERROR, "Could not read message!");
				shouldClose = true;
				break;
			}
			
			try
			{
				handleMessage(message);
			} catch (WrongMessageException e)
			{
				Logger.log(LogLevel.ERROR, "Can't handle the Message"); // should never occur
			}
			
		}
	}
	
	/**
	 * Cleans up before closing program
	 */
	public void cleanUp()
	{
		if(!net.close()) {Logger.log(LogLevel.WARNING, "Could not clean up properly!");}
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
			mapMan.initializeMap(map);
			Logger.log(LogLevel.INFO, "Recieved Map.");
			Logger.logMap(LogLevel.DETAIL, mapMan.getCurrentMap());
		}
		else if(m.getType() == MessageType.PLAYER_NUMBER_ASSIGN) // MessageType 3
		{
			// Assign playernumber:
			playerNumber = m.retrievePlayerNumber();
			Logger.log(LogLevel.INFO, "We have been assigned Number: " + playerNumber);
		}
		else if(m.getType() == MessageType.MOVE_REQUEST) // MessageType 4
		{
			// Retrieve limits
			int timeLimit = m.retrieveTimeLimit();
			int depthLimit = m.retrieveDepthLimit();
			
			Logger.log(LogLevel.INFO, "Recieved Moverequest: (" + timeLimit + ", " + depthLimit + ")");
			// Request Move from AI
			HashSet<Move> moves = mapMan.getCurrentMap().getPossibleMoves(playerNumber);
			for (Move move : moves)
			{
				try
				{
					net.sendMessage(Message.newMoveReply(move));
					Logger.log(LogLevel.INFO, "Replied with Move: " + move);
				} catch (IOException e)
				{
				}
				break;
			}
			
		}
		else if(m.getType() == MessageType.MOVE_ANNOUNCE) // MessageType 6
		{
			Move move = m.retrieveAnouncedMove();
			Logger.log(LogLevel.INFO, "Recieved Move: " + move);
			// Apply Move to the map
			mapMan.applyMove(move);
			Logger.log(LogLevel.DETAIL, "Applied Move:");
			Logger.logMap(LogLevel.DETAIL, mapMan.getCurrentMap());
		}
		else if(m.getType() == MessageType.DISQUALIFICATION) // MessageType 7
		{
			byte disqualified = m.retrieveDisqualifiedPlayer();
			Logger.log(LogLevel.INFO, "Player disqualified: " + disqualified);
			
			// remove Disqualified player.
			mapMan.getCurrentMap().getPlayer(disqualified).disqualify();
			
			
			// if its us: exit programm
			if(playerNumber == disqualified)
			{
				Logger.log(LogLevel.WARNING, "We were disqualified!");
				shouldClose = true;
			}
		}
		else if(m.getType() == MessageType.END_FIRST_PHASE) // MessageType 8
		{
			// AI.enterBombingPhase()
			mapMan.toggleGamePhase();
			Logger.log(LogLevel.INFO, "First Phase has ended!");
		}
		else if(m.getType() == MessageType.END_SECOND_PHASE) // MessageType 9
		{
			// Exit Program
			shouldClose = true;
			Logger.log(LogLevel.INFO, "Game has ended!");
		}
		else if(m.getType() == MessageType.CURRENT_GAME_STATE) // MessageType 10
		{
			//String state = m.retrieveGameState();
			// ignore or update Map:
			Logger.log(LogLevel.INFO, "Recieved new game state!");
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
		Logger.init(LogLevel.DETAIL);
		//Check cmdArgs:
		if(args.length != 2)
		{
			Logger.log(LogLevel.ERROR, "Call with hostname and port as parameters");
			System.exit(0);
		}
		int port = -1;
		
		try {
			port = Integer.parseInt(args[1]);
		}catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, "port must be a number");
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
