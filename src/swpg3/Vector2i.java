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
	public int	x;
	public int	y;

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
	 * Scale Vector by factor
	 * 
	 * @param factor
	 *            factor to be scaled by
	 */
	public void scale(int factor)
	{
		this.x *= factor;
		this.y *= factor;
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

	/**
	 * Creates a new Vector equal to the sum of a and b
	 * @param a doesn't get changed by function
	 * @param b doesn't get changed by function
	 * @return new Vector = a+b
	 */
	public static Vector2i sum(Vector2i a, Vector2i b)
	{
		Vector2i ret = new Vector2i(a.x, a.y);
		ret.add(b);
		return ret;
	}

	/**
	 * Creates a new Vector equal to the scaled Vetor a
	 * @param a doesn't get changed by function
	 * @param factor to be scaled by
	 * @return a new vector
	 */
	public static Vector2i scaled(Vector2i a, int factor)
	{
		Vector2i ret = new Vector2i(a.x, a.y);
		ret.scale(factor);
		return ret;
	}
}
