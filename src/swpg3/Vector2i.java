/**
 * 
 */
package swpg3;

/**
 * Small helper-class to represent a position or direction in form of a Vector
 * of 2 integer Values
 * 
 * @author eric
 * 
 */
public class Vector2i {
	public int x;
	public int y;

	/**
	 * Initialise as (0,0)
	 */
	public Vector2i()
	{
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Initialise as (x,y)
	 */
	public Vector2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Addition of another Vector b. this = this + b
	 * 
	 * @param b
	 *            Other Vector to be added
	 */
	public void add(Vector2i b)
	{
		this.x += b.x;
		this.y += b.y;
	}

	/**
	 * Compare this to another Vector
	 * 
	 * @param b
	 *            Other Vector to be added
	 * @return true, if vectors are equal
	 */
	public boolean equals(Vector2i b)
	{
		return ((this.x == b.x) && (this.y == b.y));
	}

	/**
	 * @return true, if x and y are 0; false, otherwise
	 */
	public boolean isZero()
	{
		return this.x == 0 && this.y == 0;
	}
}
