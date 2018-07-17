/**
 * 
 */
package swpg3.net;

/**
 * Enum to describe the different types of messages that are distributed over the network.
 * 
 * @author eric
 *
 */
public enum MessageType {
	GROUP_NUMBER_INIT(1),
	MAP_INIT(2),
	PLAYER_NUMBER_ASSIGN(3),
	MOVE_REQUEST(4),
	MOVE_RESPONSE(5),
	MOVE_ANNOUNCE(6),
	DISQUALIFICATION(7),
	END_FIRST_PHASE(8),
	END_SECOND_PHASE(9),
	CURRENT_GAME_STATE(10),
	INVALID(255);
	
	public final byte typeNumber;
	private MessageType(int i)
	{
		typeNumber = (byte) i;
	}
	
	/**
	 * Maps a number to the corresponding MessageType according to the specification.
	 * @param b The number to be mapped.
	 * @return The corresponding MessageType.
	 */
	public static MessageType fromTypeNumber(byte b)
	{
		switch(b)
		{
			case 1:
				return MessageType.GROUP_NUMBER_INIT;
			case 2:
				return MessageType.MAP_INIT;
			case 3:
				return MessageType.PLAYER_NUMBER_ASSIGN;
			case 4:
				return MessageType.MOVE_REQUEST;
			case 5:
				return MessageType.MOVE_RESPONSE;
			case 6:
				return MessageType.MOVE_ANNOUNCE;
			case 7:
				return MessageType.DISQUALIFICATION;
			case 8:
				return MessageType.END_FIRST_PHASE;
			case 9:
				return MessageType.END_SECOND_PHASE;
			case 10:
				return MessageType.CURRENT_GAME_STATE;
			default:
				return MessageType.INVALID;
		}
	}
	
}
