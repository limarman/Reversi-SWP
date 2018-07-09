/**
 * 
 */
package swpg3.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import swpg3.main.logging.LogLevel;
import swpg3.main.logging.LogTag;
import swpg3.main.logging.Logger;

/**
 * Singleton class that Manages all Network transmissions.
 * 
 * @author eric
 *
 */
public final class NetworkManager {
	private static NetworkManager	instance	= null;
	private Socket					socket;
	private DataInputStream			serverReader;
	private DataOutputStream		serverWriter;
	private boolean					connected;

	/**
	 * Singleton constructor. Connects Manager to host:port
	 * 
	 * @param host
	 *            to connect to
	 * @param port
	 *            to connect to
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
	 * Returns the instance of the Network Manager
	 * 
	 * @return the instance of NetworkManager, if initialized; null, otherwise
	 */
	public static NetworkManager getInstance()
	{
		if (instance != null)
		{
			return instance;
		}
		return null;
	}

	/**
	 * Initialize the NetworkManager, if not already initialized. Also connects the
	 * specified host and port
	 * 
	 * @param host
	 *            hostname/IP to connect to
	 * @param port
	 *            portnumber to connect to
	 * @return instance of the NetworkManager
	 */
	public static NetworkManager initialize(String host, int port)
	{
		if (instance == null)
		{
			instance = new NetworkManager(host, port);
			return instance;
		}
		return instance;
	}

	/**
	 * Check if the Manager connected successfully
	 * 
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

		if (!socket.isClosed())
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
	 * 
	 * @param m
	 *            Message to be sent
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
	 * Retrieve the next Message over Network. This Method will halt until the next
	 * Message is received completely
	 * 
	 * @return Message that was received
	 * @throws IOException
	 *             If an Error occurred while reading
	 * @throws EOFException
	 *             If EOF occurred while reading
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

		Logger.log(LogLevel.DEBUG, LogTag.DEBUG, String.format("%12d - Message Recieved Completly", System.nanoTime()));

		return new Message(MessageType.fromTypeNumber(typeNumber), length, data);
	}

}
