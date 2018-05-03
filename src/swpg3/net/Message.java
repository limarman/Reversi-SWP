/**
 * 
 */
package swpg3.net;

import java.nio.charset.StandardCharsets;

import swpg3.Move;

/**
 * Class that contains a Network Message
 * @author eric
 *
 */
public class Message {
	private MessageType type;
	private int length;
	private byte[] data;
	
	/**
	 * @param type
	 * @param length
	 * @param data
	 */
	public Message(MessageType type, int length, byte[] data)
	{
		this.type = type;
		this.length = length;
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public MessageType getType()
	{
		return type;
	}

	/**
	 * @return the length
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * @return the data
	 */
	public byte[] getData()
	{
		return data;
	}
	
	//###############################################################
	//					Message type 1
	//###############################################################
	public static Message newGroupNumberInitMessage(byte groupNumber)
	{
		byte[] data = new byte[1];
		data[0] = groupNumber;
		return new Message(MessageType.GROUP_NUMBER_INIT, 1, data);
	}
	
	//###############################################################
	//					Message type 2
	//###############################################################
	public String retrieveMap() throws WrongMessageException
	{
		if(type != MessageType.MAP_INIT)
		{
			throw new WrongMessageException();
		}
		
		String s = new String(data, StandardCharsets.US_ASCII);
		return s;
	}
	//###############################################################
	//					Message type 3
	//###############################################################
	public byte retrievePlayerNumber() throws WrongMessageException
	{
		if(type != MessageType.PLAYER_NUMBER_ASSIGN)
		{
			throw new WrongMessageException();
		}
		
		return data[0];
	}
	//###############################################################
	//					Message type 4 (Move Request)
	//###############################################################
	public int retrieveTimeLimit() throws WrongMessageException
	{
		if(type != MessageType.MOVE_REQUEST)
		{
			throw new WrongMessageException();
		}
		int timeLimit = 0;
		for(int i = 0; i < 4; i++)
		{
			timeLimit |= data[i];
			if(i != 3) {timeLimit <<= 8;}
		}
		return timeLimit;
		
	}
	public int retrieveDepthLimit() throws WrongMessageException
	{
		if(type != MessageType.MOVE_REQUEST)
		{
			throw new WrongMessageException();
		}
		return (int) data[4];
	}
	//###############################################################
	//					Message type 5
	//###############################################################
	public static Message newMoveReply(Move move)
	{
		byte xhigh = (byte)((move.getCoordinates().x & 0xFF00) >> 8);
		byte xlow = (byte)(move.getCoordinates().x & 0xFF);
		byte yhigh = (byte)((move.getCoordinates().y & 0xFF00) >> 8);
		byte ylow = (byte)(move.getCoordinates().y & 0xFF);
		
		byte[] data = new byte[5];
		data[0] = xhigh;
		data[1] = xlow;
		data[2] = yhigh;
		data[3] = ylow;
		data[4] = move.getSpecialFieldInfo();
		return new Message(MessageType.MOVE_RESPONSE, 5, data);
	}
	//###############################################################
	//					Message type 6
	//###############################################################
	public Move retrieveAnouncedMove() throws WrongMessageException
	{
		if(type != MessageType.MOVE_ANNOUNCE)
		{
			throw new WrongMessageException();
		}
		
		int x = 0;
		int y = 0;
		
		x |= data[0];
		x <<= 8;
		x |= data[1];
		
		y |= data[2];
		y <<= 8;
		y |= data[3];
		
		return new Move(x, y, data[4], data[5]);
	}
	//###############################################################
	//					Message type 7
	//###############################################################
	public byte retrieveDisqualifiedPlayer() throws WrongMessageException
	{
		if(type != MessageType.DISQUALIFICATION)
		{
			throw new WrongMessageException();
		}
		
		return data[0];
	}
	//###############################################################
	//					Message type 8
	//###############################################################
	// No Method needed
	//###############################################################
	//					Message type 9
	//###############################################################
	// No Method needed
	//###############################################################
	//					Message type 10
	//###############################################################
	public String retrieveGameState() throws WrongMessageException
	{
		if(type != MessageType.CURRENT_GAME_STATE)
		{
			throw new WrongMessageException();
		}
		
		String s = new String(data, StandardCharsets.US_ASCII);
		return s;
	}
}
