/**
 * 
 */
package swpg3.net;

import java.nio.charset.StandardCharsets;

import swpg3.game.move.Move;

/**
 * Class that contains a Network Message and handles creating and reading from
 * them
 * 
 * @author eric
 *
 */
public class Message {
	private MessageType	type;
	private int			length;
	private byte[]		data;

	/**
	 * Initializes a Message with given details.
	 * 
	 * @param type
	 *            Type of the message.
	 * @param length
	 *            Length of the Payload.
	 * @param data
	 *            Payload as byte Array.
	 */
	public Message(MessageType type, int length, byte[] data)
	{
		this.type = type;
		this.length = length;
		this.data = data;
	}

	/**
	 * @return The type of the message.
	 */
	public MessageType getType()
	{
		return type;
	}

	/**
	 * @return The length of the Payload.
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * @return The payload as byte array.
	 */
	public byte[] getData()
	{
		return data;
	}

	// ###############################################################
	// Message type 1
	// ###############################################################
	/**
	 * Creates a new message that is used to transmit the group number supplied.
	 * 
	 * @param groupNumber
	 *            The number that should be transmitted.
	 * @return The new Message.
	 */
	public static Message newGroupNumberInitMessage(byte groupNumber)
	{
		byte[] data = new byte[1];
		data[0] = groupNumber;
		return new Message(MessageType.GROUP_NUMBER_INIT, 1, data);
	}

	// ###############################################################
	// Message type 2
	// ###############################################################
	/**
	 * Retrieves a map String out of the Message if it is of the correct type.
	 * 
	 * @return Map String contained in the Message.
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public String retrieveMap() throws WrongMessageException
	{
		if (type != MessageType.MAP_INIT)
		{
			throw new WrongMessageException();
		}

		String s = new String(data, StandardCharsets.US_ASCII);
		return s;
	}

	// ###############################################################
	// Message type 3
	// ###############################################################
	/**
	 * Retrieves the assigned playernumber out of the Message if it is of the
	 * correct type.
	 * 
	 * @return The Playernumber in [1,n].
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public byte retrievePlayerNumber() throws WrongMessageException
	{
		if (type != MessageType.PLAYER_NUMBER_ASSIGN)
		{
			throw new WrongMessageException();
		}

		return data[0];
	}

	// ###############################################################
	// Message type 4 (Move Request)
	// ###############################################################
	/**
	 * Retrieves the time limit out of the Message if it is of the
	 * correct type.
	 * 
	 * @return The time limit in milliseconds.
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public int retrieveTimeLimit() throws WrongMessageException
	{
		if (type != MessageType.MOVE_REQUEST)
		{
			throw new WrongMessageException();
		}
		int timeLimit = 0;
		for (int i = 0; i < 4; i++)
		{
			timeLimit |= Byte.toUnsignedInt(data[i]);
			if (i != 3)
			{
				timeLimit <<= 8;
			}
		}
		return timeLimit;

	}

	/**
	 * Retrieves the depth limit out of the Message if it is of the
	 * correct type.
	 * 
	 * @return The depth limit supplied by the server.
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public int retrieveDepthLimit() throws WrongMessageException
	{
		if (type != MessageType.MOVE_REQUEST)
		{
			throw new WrongMessageException();
		}
		return Byte.toUnsignedInt(data[4]);
	}

	// ###############################################################
	// Message type 5
	// ###############################################################
	/**
	 * Creates a new Message containing the Move that should be replied.
	 * @param move The Move to be encapsulated in the message. Neither gets modified nor cloned.
	 * @return The new message.
	 */
	public static Message newMoveReply(Move move)
	{
		byte xhigh = (byte) ((move.getCoordinates().x & 0xFF00) >> 8);
		byte xlow = (byte) (move.getCoordinates().x & 0xFF);
		byte yhigh = (byte) ((move.getCoordinates().y & 0xFF00) >> 8);
		byte ylow = (byte) (move.getCoordinates().y & 0xFF);

		byte[] data = new byte[5];
		data[0] = xhigh;
		data[1] = xlow;
		data[2] = yhigh;
		data[3] = ylow;
		data[4] = move.getSpecialFieldInfo();
		return new Message(MessageType.MOVE_RESPONSE, 5, data);
	}

	// ###############################################################
	// Message type 6
	// ###############################################################
	/**
	 * Retrieves the announced move out of the Message if it is of the
	 * correct type.
	 * 
	 * @return The move announced by the server.
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public Move retrieveAnouncedMove() throws WrongMessageException
	{
		if (type != MessageType.MOVE_ANNOUNCE)
		{
			throw new WrongMessageException();
		}

		int x = 0;
		int y = 0;

		x |= Byte.toUnsignedInt(data[0]);
		x <<= 8;
		x |= Byte.toUnsignedInt(data[1]);

		y |= Byte.toUnsignedInt(data[2]);
		y <<= 8;
		y |= Byte.toUnsignedInt(data[3]);

		return new Move(x, y, data[4], data[5]);
	}

	// ###############################################################
	// Message type 7
	// ###############################################################
	/**
	 * Retrieves the playernumber of the disqualified player out of the Message if it is of the
	 * correct type.
	 * 
	 * @return The playernumber of the disqualified player in [1,n].
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public byte retrieveDisqualifiedPlayer() throws WrongMessageException
	{
		if (type != MessageType.DISQUALIFICATION)
		{
			throw new WrongMessageException();
		}

		return data[0];
	}

	// ###############################################################
	// Message type 8
	// ###############################################################
	// No Method needed
	// ###############################################################
	// Message type 9
	// ###############################################################
	// No Method needed
	// ###############################################################
	// Message type 10
	// ###############################################################
	/**
	 * Retrieves the distributed gamestate out of the Message if it is of the
	 * correct type.
	 * 
	 * @return The gamestate as a String.
	 * @throws WrongMessageException
	 *             Thrown if message does not contain the required data as it is the
	 *             wrong type.
	 */
	public String retrieveGameState() throws WrongMessageException
	{
		if (type != MessageType.CURRENT_GAME_STATE)
		{
			throw new WrongMessageException();
		}

		String s = new String(data, StandardCharsets.US_ASCII);
		return s;
	}
}
