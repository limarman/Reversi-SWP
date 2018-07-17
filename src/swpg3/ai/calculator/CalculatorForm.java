package swpg3.ai.calculator;

import swpg3.game.move.Move;

/**
 * Class that stores information, which has been explored in the min-max recursion of the calculators.
 * Contents:
 * - bestMove
 * - maxBranchingFactor
 * - calculatedToEnd
 * - nodesReached
 * @author Ramil
 *
 */
public class CalculatorForm {
	
	/**
	 * The move which has been chosen to be the best.
	 */
	private Move bestMove;
	/**
	 * The maximal branching factor discovered during the search.
	 */
	private int maxBranchingFactor;
	/**
	 * boolean, whether the calcualtion reached the end of the gamephase.
	 */
	private boolean calculatedToEnd;
	/**
	 * the number of nodes (inner nodes and leaf nodes) reached during the search.
	 */
	private int nodesReached;
	
	/**
	 * Default constructor, initializing the attributes:
	 * bestMove - an "empty" instance of Move class.
	 * maxBranchingFactor = -1.
	 * calculatedToEnd = false.
	 * nodesReached = 0.
	 */
	public CalculatorForm() {
		bestMove = new Move();
		maxBranchingFactor = -1;
		calculatedToEnd = false;
		nodesReached = 0;
	}
	
	/**
	 * Getter for the best move.
	 * @return the reference to the best move.
	 */
	public Move getBestMove() 
	{
		return bestMove;
	}
	
	/**
	 * Getter for the maximal branching factor.
	 * @return the maximal branching factor.
	 */
	public int getMaxBranchingFactor() 
	{
		return maxBranchingFactor;
	}
	
	/**
	 * Getter for the boolean, whether the search has calculated to the end of the current gamephase.
	 * @return whether the search has reached the end of the current gamephase.
	 */
	public boolean hasCalculatedToEnd()
	{
		return calculatedToEnd;
	}
	
	/**
	 * Getter for the number of visited nodes.
	 * @return the number of visited nodes during the search. during the search.
	 */
	public int getReachedNodesCount()
	{
		return nodesReached;
	}
	
	/**
	 * Increments the number of reached nodes by one.
	 */
	public void incrementReachedNodes() 
	{
		nodesReached++;
	}
	
	/**
	 * Copys the move parameters into the the variable "bestMove". No pass by reference.
	 * @param move - to copy the attributes from.
	 */
	public void setBestMove(Move move) 
	{
		bestMove.copyFrom(move);
	}
	
	/**
	 * Setter for the visited nodes during the search.
	 * @param nodeCount - number of vistied nodes.
	 */
	public void setReachedNodesCount(int nodeCount) 
	{
		this.nodesReached = nodeCount;
	}
	
	/**
	 * Setter for the maximal branching factor found during the search.
	 * @param maxBranchingFactor - maximal branching factor
	 */
	public void setMaxBranchingFactor(int maxBranchingFactor) 
	{
		this.maxBranchingFactor = maxBranchingFactor;
	}
	
	/**
	 * Setter for whether the calculation has reached the end of the current gamephase.
	 * @param calculatedToEnd - whether the calculation has reached the end of current gamephase.
	 */
	public void setCalculatedToEnd(boolean calculatedToEnd) 
	{
		this.calculatedToEnd = calculatedToEnd;
	}
	
	/**
	 * Resets the attributes to the default values, just like the default constructor does.
	 */
	public void resetForm() 
	{
		bestMove = new Move();
		maxBranchingFactor = -1;
		calculatedToEnd = false;
		nodesReached = 0;
	}
}
