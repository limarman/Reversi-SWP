package swpg3.ai.evaluator;

import swpg3.game.map.Map;

public interface Evaluator {

	double evaluatePosition(Map map, byte playerNumber);
}
	
	