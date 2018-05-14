package swpg3.ai;

import swpg3.Map;

public interface Evaluator { //TODO: Make an interface - implement evaluatePosition - for different analysis

	double evaluatePosition(Map map, byte playerNumber);
}
	
	