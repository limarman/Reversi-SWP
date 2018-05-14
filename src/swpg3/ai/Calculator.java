package swpg3.ai;

import swpg3.Map;

public class Calculator {
	
	private static Calculator instance = null;
	
	private Calculator() {}
	
	public static Calculator getInstance() 
	{
		if(instance == null) 
		{
			instance = new Calculator();
		}
		
		return instance;
	}
	
	public double minimax(Evaluator eval, byte playerNumber, int depth, Map map) 
	{
		return 42;
	}
	
	private double minPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map) 
	{
		return 42;
	}
	
	private double maxPlayer(Evaluator eval, byte maxPlayerNumber, byte currentPlayerNumber, int depth, Map map) 
	{
		//reached maximal depth
		if(depth == 0) 
		{
			return eval.evaluatePosition(map, maxPlayerNumber);
		}
		
		return 42;

	}

}
