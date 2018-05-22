/**
 * 
 */
package swpg3.net;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import swpg3.game.move.Move;

/**
 * @author eric
 *
 */
class MessageTest {

	/**
	 * Test method for {@link swpg3.net.Message#newGroupNumberInitMessage(byte)}.
	 */
	@Test
	void testNewGroupNumberInitMessage()
	{
		Message m = Message.newGroupNumberInitMessage((byte)5);
		
		assertTrue(m.getType() == MessageType.GROUP_NUMBER_INIT, "Wrong type");
		assertTrue(m.getLength() == 1, "Length missmatch");
		assertTrue(m.getData().length == 1, "Array Length missmatch");
		assertTrue(m.getData()[0] == 5, "Data missmatch!");
	}

	/**
	 * Test method for {@link swpg3.net.Message#retrieveMap()}.
	 */
	@Test
	void testRetrieveMap()
	{
		String mapString = "2\n3\n4 5\n4 4\n0 1 2 3\n4 5 6 7\n8 - c i\nb x - -\n0 0 0 <-> 1 3 4";
		byte[] mapArr = mapString.getBytes(StandardCharsets.US_ASCII);
		
		Message m = new Message(MessageType.fromTypeNumber((byte)2), mapArr.length, mapArr);
		
		assertTrue(m.getType() == MessageType.MAP_INIT, "Type missmatch!");
		assertTrue(m.getLength() == mapArr.length, "length missmatch!");
		try
		{
			assertTrue(m.retrieveMap().compareTo(mapString) == 0, "String missmatch!");
			
		} catch (WrongMessageException e)
		{
			fail("String Error!");
		}
	}

	/**
	 * Test method for {@link swpg3.net.Message#retrievePlayerNumber()}.
	 */
	@Test
	void testRetrievePlayerNumber()
	{
		byte playerNumber = 8;
		byte[] noArr = new byte[1];
		noArr[0] = playerNumber;
		
		Message m = new Message(MessageType.fromTypeNumber((byte)3), 1, noArr);
		
		assertTrue(m.getType() == MessageType.PLAYER_NUMBER_ASSIGN, "Type missmatch!");
		assertTrue(m.getLength() == noArr.length, "length missmatch!");
		try
		{
			assertTrue(m.retrievePlayerNumber() == playerNumber, "Data missmatch!");
			
		} catch (WrongMessageException e)
		{
			fail("Data Error!");
		}
	}

	/**
	 * Test method for {@link swpg3.net.Message#retrieveTimeLimit()}.
	 */
	@Test
	void testRetrieveLimits()
	{
		byte[] dataArr = {(byte)0x00, (byte)0x20, (byte)0x01, (byte)0xab, (byte)0xdf};
		Message m = new Message(MessageType.fromTypeNumber((byte)4), 5, dataArr);
		
		assertTrue(m.getType() == MessageType.MOVE_REQUEST, "Type missmatch!");
		assertTrue(m.getLength() == dataArr.length, "length missmatch!");
		try
		{
			assertTrue(m.retrieveTimeLimit() == 0x002001ab, "Data missmatch: timeLimit!");
			assertTrue(m.retrieveDepthLimit() == 0xdf, "Data missmatch: depthLimit!");
			
		} catch (WrongMessageException e)
		{
			fail("Data Error!");
		}
	}

	/**
	 * Test method for {@link swpg3.net.Message#newMoveReply(swpg3.game.move.Move)}.
	 */
	@Test
	void testNewMoveReply()
	{
		Move move = new Move(0xabcd, 0x0012, (byte)0x15, (byte)0x08);
		Message m = Message.newMoveReply(move);
		byte[] comArr = {(byte)0xab, (byte)0xcd, (byte)0x00, (byte)0x12, (byte)0x15};
		
		
		assertTrue(m.getType() == MessageType.MOVE_RESPONSE, "Type missmatch!");
		assertTrue(m.getLength() == 5, "length missmatch!");
		assertTrue(m.getData().length == comArr.length, "Data length missmatch");
		
		for(int i = 0; i < comArr.length; i++)
		{
			assertTrue(comArr[i] == m.getData()[i], "Data missmatch: " + i + "!");
		}
	}

	/**
	 * Test method for {@link swpg3.net.Message#retrieveAnouncedMove()}.
	 */
	@Test
	void testRetrieveAnouncedMove()
	{
		byte[] dataArr = {(byte)0xab, (byte)0xcd, (byte)0x00, (byte)0x12, (byte)0x15, (byte)0x08};
		Message m = new Message(MessageType.fromTypeNumber((byte)6), dataArr.length, dataArr);
		
		assertTrue(m.getType() == MessageType.MOVE_ANNOUNCE, "Type missmatch!");
		assertTrue(m.getLength() == dataArr.length, "length missmatch!");
		
		try 
		{
			Move move = m.retrieveAnouncedMove();
			assertTrue(move.getCoordinates().x == 0xabcd, "Data missmatch: x coord!");
			assertTrue(move.getCoordinates().y == 0x0012, "Data missmatch: y coord!");
			assertTrue(move.getSpecialFieldInfo() == 0x15, "Data missmatch: special field!");
			assertTrue(move.getPlayerNumber() == 0x08, "Data missmatch: playerNumber!");
		}catch (WrongMessageException e)
		{
			fail("Data Error!");
		}
	}

	/**
	 * Test method for {@link swpg3.net.Message#retrieveDisqualifiedPlayer()}.
	 */
	@Test
	void testRetrieveDisqualifiedPlayer()
	{
		byte[] data = {(byte)0xab};
		Message m = new Message(MessageType.fromTypeNumber((byte)7), data.length, data);
		
		assertTrue(m.getType() == MessageType.DISQUALIFICATION, "Type missmatch!");
		assertTrue(m.getLength() == data.length, "length missmatch!");
		
		assertTrue(m.getData().length == 1, "Data length missmatch!");
		
		try
		{
			assertTrue(m.retrieveDisqualifiedPlayer() == (byte)0xab, "Data missmatch");
		} catch (WrongMessageException e)
		{
			fail("Data Error!");
		}
	}

	/**
	 * Test method for {@link swpg3.net.Message#retrieveGameState()}.
	 */
	@Test
	void testRetrieveGameState()
	{
		String mapString = "2\n1\n0 5 6\n1 4 4\n0 1 2 3\n4 5 6 7\n8 - c i\nb x - -";
		byte[] mapArr = mapString.getBytes(StandardCharsets.US_ASCII);
		
		Message m = new Message(MessageType.fromTypeNumber((byte)10), mapArr.length, mapArr);
		
		assertTrue(m.getType() == MessageType.CURRENT_GAME_STATE, "Type missmatch!");
		assertTrue(m.getLength() == mapArr.length, "length missmatch!");
		try
		{
			assertTrue(m.retrieveGameState().compareTo(mapString) == 0, "String missmatch!");
			
		} catch (WrongMessageException e)
		{
			fail("String Error!");
		}
	}

}
