/**
 * 
 */
package swpg3.net;

/**
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
