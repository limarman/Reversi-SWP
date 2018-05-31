package swpg3.game;

import java.util.HashSet;

public class VectorSetWrapper {

	private HashSet<Vector2i> vectorSet;
	
	public VectorSetWrapper() 
	{
		vectorSet = new HashSet<>();
	}
	
	public void add (Vector2i v) 
	{
		vectorSet.add(v);
	}
	
	public int getSize() 
	{
		return vectorSet.size();
	}
	
}
