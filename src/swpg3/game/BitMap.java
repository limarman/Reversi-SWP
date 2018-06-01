package swpg3.game;

/**
 * Class representing a Bitmap
 * @author Ramil
 *
 */
public class BitMap {

	private boolean[] map;
	private int width;
	
	
	public BitMap(int width, int height) 
	{
		this.width = width;
		map = new boolean[width*height];
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return bool value in at the given coordinates
	 */
	public boolean get(int x, int y) 
	{
		return map[x + y * width];
	}
	
	/**
	 * Sets the value val at the position (x.y)
	 * @param x
	 * @param y
	 * @param val
	 */
	public void set(int x, int y, boolean val) 
	{
		map[x + y * width] = val;
	}
	
}
