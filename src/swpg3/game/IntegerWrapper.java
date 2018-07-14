package swpg3.game;

/**
 * Wrapper Class for Integers. Used for pass by reference.
 * @author Ramil
 *
 */
public class IntegerWrapper {

	private int value;
	
	
	public IntegerWrapper(int value) 
	{
		this.value = value;
	}
	
	public IntegerWrapper() 
	{
		this.value = 0;
	}
	
	public void incrementValue() 
	{
		value++;
	}
	
	public int getValue() 
	{
		return value;
	}
	
	
}
