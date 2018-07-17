package swpg3.ai.calculator;

import swpg3.ai.evaluator.Evaluator;

public interface Calculator {
	
	/**
	 * The main Method for every move-calculation strategy. Calculates the position value and fills out the CalculatorForm to
	 * pass the best move and other information asked in the form for.
	 * @param eval - used Evaluator
	 * @param playerNumber - number of player to move
	 * @param form - form to fill out by the calculator during the process of calculating
	 * @param conditions - conditions which are set for the Calculator to follow
	 * @return the evaluation in view of player with playerNumber
	 */
	double calculateBestMove(Evaluator eval, byte playerNumber, CalculatorForm form, CalculatorConditions conditions);
	
}