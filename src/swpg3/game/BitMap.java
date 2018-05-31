package swpg3.game;

/**
 * Class representing a Bitmap
 * @author Ramil
 *
 */
public class BitMap {

	private boolean[] map;
	private int height;
	
	
	public BitMap(int width, int height) 
	{
		this.height = height;
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
		return map[x + y * height];
	}
	
	/**
	 * Sets the value val at the position (x.y)
	 * @param x
	 * @param y
	 * @param val
	 */
	public void set(int x, int y, boolean val) 
	{
		map[x + y * height] = val;
	}
	
}
