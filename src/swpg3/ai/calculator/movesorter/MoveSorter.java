package swpg3.ai.calculator.movesorter;

import java.util.HashSet;

import swpg3.ai.evaluator.Evaluator;
import swpg3.game.map.Map;
import swpg3.game.move.Move;

/**
 * Interface providing a MoveSort-algorithm(s) for the calculator
 * @author Ramil
 *
 */
public interface MoveSorter {
	
	/**
	 * Method called in the starting Max Player. Called once in the beginning.
	 * Moves should be sorted ascending. That means the 'worst' move is in the beginning and the 'best' at the end of the array. 
	 * @param eva - Evaluator to use
	 * @param possibleMovesOrderable - a HashSet of possible moves, which are "flagged" and sortable (e.g. NaturalSort)
	 * @param map - the currently calculated map
	 * @param playerNumber - the currently active playerNumber (toMove)
	 * @return sorted array of the possible Moves
	 */
	public Move[] initialMoveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber);
	
	/**
	 * Method called in the max-min-recursion. Called multiple times.
	 * Moves should be sorted ascending. That means the 'worst' move is in the beginning and the 'best' at the end of the array. 
	 * @param eva - Evaluator to use
	 * @param possibleMovesOrderable - a HashSet of possible moves, which are "flagged" and sortable (e.g. NaturalSort)
	 * @param map - the currently calculated map
	 * @param playerNumber - the currently active playerNumber (toMove)
	 * @return sorted array of the possible Moves
	 */
	public Move[] moveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber);

}
