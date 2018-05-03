/**
 * 
 */
package swpg3.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Singleton class that Manages all Network transmissions
 * 
 * @author eric
 *
 */
public final class NetworkManager {
	private static NetworkManager instance = null;
	private Socket socket;
	private DataInputStream serverReader;
	private DataOutputStream serverWriter;
	private boolean connected;
	
	/**
	 * Singleton constructor. Connects Manager to host:port
	 * @param host to connect to
	 * @param port to connect to
	 */
	private NetworkManager(String host, int port)
	{
		try
		{
			socket = new Socket(host, port);
			serverReader = new DataInputStream(socket.getInputStream());
			serverWriter = new DataOutputStream(socket.getOutputStream());
			connected = true;
		} catch (IOException e)
		{
			connected = false;
		}
	}
	
	/**
	 * @return the instance of NetworkManager
	 * @throws InstantiationException If NetworkManager is not initialized
	 */
	public static NetworkManager getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		return null;
	}
	/**
	 * Initialize the NetworkManager
	 * @param host hostname/IP to connect to
	 * @param port portnumber to connect to
	 * @return instance of the NetworkManager
	 * @throws InstantiationException if NetworkManager is already initialized
	 */
	public static NetworkManager initialize(String host, int port)
	{
		if(instance == null)
		{
			instance = new NetworkManager(host, port);
			return instance;
		}
		return null;
	}

	/**
	 * Check if the Manager connected successfully
	 * @return true, if Manager connected; false, otherwise
	 */
	public boolean isConnected()
	{
		return connected;
	}
	
	/**
	 * Closes underlying socket and StreamReaders, if not closed already.
	 */
	public boolean close()
	{
		boolean success = true;
		try
		{
			serverReader.close();
			serverWriter.close();
		} catch (IOException e)
		{
			success = false;
		}
		
		if(!socket.isClosed())
		{
			try
			{
				socket.close();
			} catch (IOException e)
			{
				success = false;
				connected = false;
			}
		}
		return success;
	}
	
	/**
	 * Send a Message over the Network
	 * @param m Message to be sent
	 * @throws IOException 
	 */
	public void sendMessage(Message m) throws IOException
	{
		serverWriter.writeByte(m.getType().typeNumber);
		serverWriter.writeInt(m.getLength());
		serverWriter.write(m.getData(), 0, m.getLength());
		serverWriter.flush();
	}
	
	/**
	 * Retrieve the next Message over Network.
	 * This Method will halt until the next Message is received completely
	 * @return Message that was received
	 * @throws IOException If an Error occurred while reading
	 * @throws EOFException If EOF occurred while reading
	 */
	public Message getNewMessage() throws IOException, EOFException
	{
		byte typeNumber;
		int length;
		byte[] data;
		
		typeNumber = serverReader.readByte();
		length = serverReader.readInt();
		
		data = new byte[length];
		serverReader.readFully(data);
		
		return new Message(MessageType.fromTypeNumber(typeNumber), length, data);
	}

}
