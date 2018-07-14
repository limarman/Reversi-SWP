package swpg3.ai.evaluator;

import swpg3.game.map.Map;

/**
 * An interface which is providing a evaluation function, to evaluate a position from the perspective of one player.
 * @author Ramil
 *
 */
public interface Evaluator {

	/**
	 * Method evaluating the current position the map is displaying
	 * from the point of view of the player with the given playerNumber
	 * @param map - the map, which position should be evaluated.
	 * @param playerNumber - the playerNumber of the player, from which point of view the position should be evaluated.
	 * @return An evaluation of the position.
	 */
	double evaluatePosition(Map map, byte playerNumber);
}
	
	