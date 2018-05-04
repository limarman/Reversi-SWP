package swpg3.ai;

import swpg3.GamePhase;
import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.Player;

public class AI {
	
	//##################################################
	// Parameters for evaluation-function
	//##################################################
	
	//##################################################
	// Static map-properties
	//##################################################
	int PLAYABLE_SQUARES;
	
	//##################################################
	// Evaluation Function
	//##################################################
	
	public double evaluatePosition(Map m, int playerNumber)
	{
		if(MapManager.getInstance().getGamePhase() == GamePhase.BUILDING_PHASE)
		{
			int freeSquares = 0;
			int occupiedSquares = 0;
			int freePossibleMoves = 0;
			
			//iterating over the map, analyzing
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
			{
				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
				{
					if(m.getTileAt(w,h).isOccupied())
					{
						occupiedSquares++;
						if(m.getTileAt(w, h).getStatus() == Player.mapPlayerNumberToTileStatus(playerNumber))
						{
							//TODO: search/count for possible free moves!
						}
					}
					else if(m.getTileAt(w, h).isEmpty())
					{
						freeSquares++;
					}//otherwise it was a hole
				}
			}
			
			//Look at the positional factors
		}
		else //Bombing Phase
		{
			
		}
		
		return 42;
	}
	
	
	//##################################################
	// Method for Returning the Best Move
	//##################################################
	
	public Move getBestMove()
	{
		Move move = null;
		
		return move;
	}
	
	//##################################################
	// Function for initial map-analysis
	//##################################################
	public void analyseMap()
	{
		
	}
	
	private double evaluateMobility()
	{
		return 42;
	}
	
	private double evaluateStoneCount()
	{
		return 42;
	}
	
	private double evaluateOverrideCount()
	{
		return 42;
	}
	
	private double evaluatePositionalFactors()
	{
		return 42;
	}
}

