package swpg3.ai.calculator;

import swpg3.ai.evaluator.Evaluator;

public interface Calculator {
	
	/**
	 * The main Method for every calculate strategy. Calculates the value and puts the move decision in the reference bestMove.
	 * @param eval - used Evaluator
	 * @param playerNumber - number of player to move
	 * @param depth - wished depth to calculate
	 * @param calcDeadLine - Deadline till when the move has to be calculated
	 * @param form - form to fill out by the calculator during the process of calculating
	 * @return the evaluation
	 */
	double calculateBestMove(Evaluator eval, byte playerNumber, int depth, long calcDeadLine, CalculatorForm form);
	
}