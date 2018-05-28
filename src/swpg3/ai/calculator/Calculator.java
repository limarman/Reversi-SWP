package swpg3.ai.calculator;

import swpg3.ai.evaluator.Evaluator;
import swpg3.game.move.Move;

public interface Calculator {
	
	/**
	 * The main Method for every calculate strategy. Calculates the value and puts the move decision in the reference bestMove.
	 * @param eval - used Evaluator
	 * @param playerNumber - number of player to move
	 * @param depth - wished depth to calculate
	 * @param bestMove - reference to fill out with the best move
	 * @return the evaluation
	 */
	double calculateBestMove(Evaluator eval, byte playerNumber, int depth, Move bestMove);
	
}