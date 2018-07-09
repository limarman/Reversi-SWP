package swpg3.game;

/**
 * Wrapper Class for Integers. Used for pass by reference.
 * @author Ramil
 *
 */
public class IntegerWrapper {

	/**
	 * The value of the wrapper.
	 */
	private int value;
	
	
	/**
	 * Constructor, sets the given value to the wrapped value.
	 * @param value
	 */
	public IntegerWrapper(int value) 
	{
		this.value = value;
	}
	
	/**
	 * Default Constructor, initializing the wrapped value with 0.
	 */
	public IntegerWrapper() 
	{
		this.value = 0;
	}
	
	/**
	 * Increments the wrapped value by one.
	 */
	public void incrementValue() 
	{
		value++;
	}
	
	/**
	 * Returns the wrapped value.
	 * @return the wrapped value.
	 */
	public int getValue() 
	{
		return value;
	}
	
	
}
