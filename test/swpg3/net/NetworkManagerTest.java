/**
 * 
 */
package swpg3.net;

import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import swpg3.Move;

/**
 * @author eric
 *
 */
class NetworkManagerTest {
	static NetworkManager net;
	static ServerSocket servSock;
	static Socket clientSock;
	static DataInputStream servIn;
	static DataOutputStream servOut;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		servSock = new ServerSocket(12345);
		
		net = NetworkManager.initialize("127.0.0.1", 12345);
		
		clientSock = servSock.accept();
		servIn = new DataInputStream(clientSock.getInputStream());
		servOut = new DataOutputStream(clientSock.getOutputStream());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		servIn.close();
		servOut.close();
		clientSock.close();
		servSock.close();
		net.close();
	}

	/**
	 * Test method for {@link swpg3.net.NetworkManager#sendMessage(swpg3.net.Message)}.
	 */
	@Test
	void testSendMessage()
	{
		if(!net.isConnected())
		{
			fail("not connected!");
		}
		
		Message m = Message.newMoveReply(new Move(0xabcd, 0x0123, (byte)0x15, (byte)0x03));
		byte[] compArr = {0x05, 0x00, 0x00, 0x00, 0x05, (byte) 0xab, (byte) 0xcd, 0x01, 0x23, 0x15};
		
		try
		{
			net.sendMessage(m);
			byte[] inArr = new byte[compArr.length];
			servIn.readFully(inArr);
			
			for(int i = 0; i < compArr.length; i++)
			{
				assertTrue(inArr[i] == compArr[i], "Data missmatch: " + i);
			}
		} catch (IOException e)
		{
			fail("Network Error");
		}
		
	}

	/**
	 * Test method for {@link swpg3.net.NetworkManager#getNewMessage()}.
	 */
	@Test
	void testGetNewMessage()
	{
		if(!net.isConnected())
		{
			fail("not connected!");
		}
		
		byte[] data = {0x04, 0x00, 0x00, 0x00, 0x05, (byte) 0xab, (byte) 0xcd, 0x01, 0x23, 0x15};
		byte[] compData = {(byte) 0xab, (byte) 0xcd, 0x01, 0x23, 0x15};
		try
		{
			servOut.write(data);
			servOut.flush();
			
			Message m = net.getNewMessage();
			
			assertTrue(m.getType() == MessageType.MOVE_REQUEST, "Type missmatch");
			assertTrue(m.getLength() == 5, "Length missmatch!");
			assertTrue(m.getData().length == 5, "Data length missmatch");
			
			for(int i = 0; i < compData.length; i++)
			{
				assertTrue(m.getData()[i] == compData[i], "Data missmatch: " + i);
			}
		} catch (IOException e)
		{
			fail("Network Error");
		}
		
	}

}
