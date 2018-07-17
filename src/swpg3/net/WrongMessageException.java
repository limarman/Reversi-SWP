/**
 * 
 */
package swpg3.net;

/**
 * An Exception to be thrown if data is tried to be extracted from a wrong typed message.
 * @author eric
 *
 */
public class WrongMessageException extends Exception {
	// Not needed but gets rid off the warning:
	private static final long serialVersionUID = 42L;
	
	public WrongMessageException()
	{
		super();
	}
	public WrongMessageException(String message)
	{
		super(message);
	}
	public WrongMessageException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public WrongMessageException(Throwable cause)
	{
		super(cause);
	}
}
