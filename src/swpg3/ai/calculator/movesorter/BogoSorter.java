package swpg3.ai.calculator.movesorter;

import java.util.HashSet;

import swpg3.ai.evaluator.Evaluator;
import swpg3.game.map.Map;
import swpg3.game.move.Move;

/**
 * MoveSorter which does not perform any sorting - or better phrased: sorting the moves randomly.
 * In the spirit of Bogo-Sort.
 * @author Ramil
 *
 */
public class BogoSorter implements MoveSorter{

	@Override
	public Move[] initialMoveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber) {
		return possibleMovesOrderable.toArray(new Move[0]);
	}

	@Override
	public Move[] moveSort(Evaluator eva, HashSet<Move> possibleMovesOrderable, Map map, byte playerNumber) {
		return possibleMovesOrderable.toArray(new Move[0]);
	}

}
