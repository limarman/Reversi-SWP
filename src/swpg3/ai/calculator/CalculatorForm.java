package swpg3.ai.calculator;

import swpg3.game.move.Move;

/**
 * Class that stores information, which has been explored in the min-max recursion of the calculators.
 * Contents:
 * - bestMove
 * - maxBranchingFactor
 * - calculatedToEnd
 * @author Ramil
 *
 */
public class CalculatorForm {
	
	private Move bestMove;
	private int maxBranchingFactor;
	private boolean calculatedToEnd;
	
	public CalculatorForm() {
		bestMove = new Move();
		maxBranchingFactor = -1;
		calculatedToEnd = false;
	}
	
	public Move getBestMove() 
	{
		return bestMove;
	}
	
	public int getMaxBranchingFactor() 
	{
		return maxBranchingFactor;
	}
	
	public boolean hasCalculatedToEnd()
	{
		return calculatedToEnd;
	}
	
	/**
	 * Copys the move parameters into the the variable "bestMove". No pass by reference.
	 * @param m
	 */
	public void setBestMove(Move m) 
	{
		bestMove.copyFrom(m);
	}
	
	public void setMaxBranchingFactor(int maxBranchingFactor) 
	{
		this.maxBranchingFactor = maxBranchingFactor;
	}
	
	public void setCalculatedToEnd(boolean calculatedToEnd) 
	{
		this.calculatedToEnd = calculatedToEnd;
	}
	
	public void resetForm() 
	{
		bestMove = new Move();
		maxBranchingFactor = -1;
		calculatedToEnd = false;
	}
}
