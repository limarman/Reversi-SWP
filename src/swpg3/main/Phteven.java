/**
 * 
 */
package swpg3.main;

import java.io.IOException;

import swpg3.MapManager;
import swpg3.Move;
import swpg3.ai.AI;
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
	private AI ai;
	
	/**
	 * Initializes Phteven by connecting to the server and transmitting the group number Message.
	 * @param hostname
	 * @param port
	 * @param verbose true, if logging to console is wished
	 */
	public void initialize(String hostname, int port, boolean errorLog)
	{
		mapMan = MapManager.getInstance();
		ai = AI.getInstance();
		// Connect to server
		Logger.log(LogLevel.DETAIL, "Connecting to server: " + hostname + ":" + port);
		net = NetworkManager.initialize(hostname, port);
		if(!net.isConnected())
		{
			Logger.log(LogLevel.ERROR, "Could not connect to server!");
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
			ai.initialize();
			Logger.log(LogLevel.INFO, "Received Map.");
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
			
			Logger.log(LogLevel.INFO, "Received Moverequest: (" + timeLimit + ", " + depthLimit + ")");
						
						
			// Request Move from AI
			Move bestMove = AI.getInstance().getBestMove(playerNumber);
			
//			HashSet<Move> moves = mapMan.getCurrentMap().getPossibleMoves(playerNumber);
			try
			{
				net.sendMessage(Message.newMoveReply(bestMove));
				Logger.log(LogLevel.INFO, "Replied with Move: " + bestMove);
			} catch (IOException e)
			{
			}
			
		}
		else if(m.getType() == MessageType.MOVE_ANNOUNCE) // MessageType 6
		{
			Move move = m.retrieveAnouncedMove();
			Logger.log(LogLevel.INFO, "Received Move: " + move);
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
			Logger.log(LogLevel.INFO, "Received new game state!");
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
		// Parse in args:
		CliParser parser = new CliParser();
		
		// options:
		CliOption serverOpt = 
				new CliOption('s', "server", true, CliOptionType.STRINGPARAM, "localhost", "Serveraddress to connect to");
		CliOption portOpt = 
				new CliOption('p', "port", true, CliOptionType.INTPARAM, "12345", "Serverport to connect to");
		CliOption loglevelOpt =
				new CliOption('l', "loglevel", false, CliOptionType.INTPARAM, "3", "Loglevel: 0-None to 5-Debug");
		
		parser.addOption(serverOpt);
		parser.addOption(portOpt);
		parser.addOption(loglevelOpt);
		
		//actual parsing:
		if(!parser.parse(args))
		{
			System.exit(1);
		}
		
		// Initialize the Logger:
		Logger.init(LogLevel.fromInt(loglevelOpt.getInt()));
		
		Logger.log(LogLevel.DEBUG, "Logger initialized in DEBUG Mode. Prepare for a Wall of Text :P");
		// In piece may he rest
		Phteven hooking = Phteven.getInstance();
		
		hooking.initialize(serverOpt.getString(), portOpt.getInt(), true);
		
		// main Event Loop:
		hooking.mainLoop();
		
		// Exit clean up:
		hooking.cleanUp();
	}

}
