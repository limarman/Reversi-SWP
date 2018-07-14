package swpg3.game;

/**
 * Class representing a Bitmap.
 * @author Ramil
 *
 */
public class BitMap {

	/**
	 * The pseudo 2-dimensional array, representing the bitmap.
	 */
	private boolean[] map;
	
	/**
	 * The width of the map (height can be calculated with the length of the bitmap array)
	 */
	private int width;
	
	
	/**
	 * Constructor creating a bitmap with the given width and height.
	 * @param width - width of the bitmap
	 * @param height - height of the bitmap
	 */
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

	/**
	 * 
	 * @param position on the map
	 * @return boolean value in the bitmap at the position
	 */
	public boolean get(Vector2i position) {
		
		return get(position.x, position.y);
	}
	
	/**
	 * 
	 * @param position on the map
	 * @param value to set at the position
	 */
	public void set(Vector2i position, boolean val) 
	{
		set(position.x, position.y, val);
	}
	
}
