package swpg3.ai.calculator;

import swpg3.ai.evaluator.Evaluator;

public interface Calculator {
	
	/**
	 * The main Method for every calculate strategy. Calculates the value and puts the move decision in the reference bestMove.
	 * @param eval - used Evaluator
	 * @param playerNumber - number of player to move
	 * @param form - form to fill out by the calculator during the process of calculating
	 * @param conditions - conditions which are set for the Calculator to follow
	 * @return the evaluation in view of player with playerNumber
	 */
	double calculateBestMove(Evaluator eval, byte playerNumber, CalculatorForm form, CalculatorConditions conditions);
	
}