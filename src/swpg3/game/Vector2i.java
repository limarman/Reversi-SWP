/**
 * 
 */
package swpg3.game;

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
	
	public static Vector2i UP() {
		return new Vector2i(0, -1);
	}

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
	 * @param x
	 * @param y
	 */
	public Vector2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Addition of another Vector b like this = this + b
	 * This Method modifies this Object
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
	 * This Method modifies this Object
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
	 * @return true, if vectors are equal; false, otherwise
	 */
	public boolean equals(Vector2i b)
	{
		if(b == null)
		{
			return false;
		}
		return ((this.x == b.x) && (this.y == b.y));
	}

	/**
	 * Checks if both entries of the Vector are zero
	 * 
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
	 * Creates a new Vector equal to the scaled Vector a
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
	
	/**
	 * creates an identical copy of Vector, for reference problems
	 * @return an identical copy of Vector
	 */
	@Override
	public Vector2i clone()
	{
		return new Vector2i(this.x, this.y);
	}

	/**
	 * Maps a direction in the form specified by the assignments to a Vector
	 * Input can be an int in interval [0,7]
	 * Output will be a Vector in the direction:
	 * i.e. Direction 0 (up) will be mapped to (0,-1)
	 * 
	 * @param dir
	 * @return
	 */
	public static Vector2i mapDirToVector(int dir)
	{
		switch (dir)
		{
			case 0:
				return new Vector2i(0, -1);
			case 1:
				return new Vector2i(1, -1);
			case 2:
				return new Vector2i(1, 0);
			case 3:
				return new Vector2i(1, 1);
			case 4:
				return new Vector2i(0, 1);
			case 5:
				return new Vector2i(-1, 1);
			case 6:
				return new Vector2i(-1, 0);
			case 7:
				return new Vector2i(-1, -1);
			default:
				return new Vector2i(0, 0);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2i other = (Vector2i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}

}	


