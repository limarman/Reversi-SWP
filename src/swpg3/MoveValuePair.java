package swpg3;

public class MoveValuePair {

	private Move move;
	private double value;
	
	public MoveValuePair(Move move, double value) 
	{
		this.move = move;
		this.value = value;
	}

	/**
	 * @return the move
	 */
	public Move getMove() {
		return move;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	
	
}
