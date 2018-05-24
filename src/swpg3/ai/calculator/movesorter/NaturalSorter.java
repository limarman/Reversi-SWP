package swpg3.ai.calculator.movesorter;

import java.util.Arrays;
import java.util.HashSet;

import swpg3.ai.evaluator.Evaluator;
import swpg3.game.map.Map;
import swpg3.game.move.Move;

/**
 * Using the MoveType to sort the moves in "natural" order - most likely (hopefully) better moves first.
 * @author Ramil
 *
 */
public class NaturalSorter implements MoveSorter{

	@Override
	public Move[] initialMoveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber) {
		
		Move[] possibleMovesOrdered = possibleMovesOrderable.toArray(new Move[0]); 
		Arrays.sort(possibleMovesOrdered);
		return possibleMovesOrdered;
	}

	@Override
	public Move[] moveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber) {
		
		Move[] possibleMovesOrdered = possibleMovesOrderable.toArray(new Move[0]); 
		Arrays.sort(possibleMovesOrdered);
		return possibleMovesOrdered;
	}

}
