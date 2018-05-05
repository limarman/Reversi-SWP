package swpg3.ai;

import swpg3.GamePhase;
import swpg3.Map;
import swpg3.MapManager;
import swpg3.Move;
import swpg3.Player;
import swpg3.TileStatus;
import swpg3.main.Phteven;

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
			//array to count the amount of stones from each player, where player1's stones are saved in stonecount[0] and so forth
			int [] stonecount = new int[MapManager.getInstance().getNumberOfPlayers()-1];
			
			//iterating over map counting stones from each player
			for(int w = 0; w<MapManager.getInstance().getWidth(); w++)
			{
				for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
				{
					if(m.getTileAt(w, h).getStatus() != TileStatus.HOLE)
					{
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_1)
						{
							stonecount [0]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_2)
						{
							stonecount[1]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_3)
						{
							stonecount[2]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_4)
						{
							stonecount[3]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_5)
						{
							stonecount[4]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_6)
						{
							stonecount[5]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_7)
						{
							stonecount[6]++;
						}
						if(m.getTileAt(w, h).getStatus() == TileStatus.PLAYER_8)
						{
							stonecount[7]++;
						}
					}
				}	
			}
			
			//finding the index of the player with the most stones, second most and of the player
			int max = 0;
			int secmax = 0;
			int player = Phteven.getPlayerNumber() - 1;
			for (int i = 0; i < stonecount.length; i++)
			{
			     if (stonecount[i] >= max)
			     {
			    	secmax = max;
			    	max = i;
			     }else if(stonecount[i] > secmax)
			     {
			    	secmax = i;
			     }
			}
			
			if(player == max && !(stonecount[max] == stonecount[secmax])) 
			{
				return stonecount[max] - stonecount[secmax];
			}else if(stonecount[player] == max && stonecount[max] == stonecount[secmax])	//when player shares first place with other players we return -1
			{
				return -1;
			}else 
			{
				//count the number of stones needed for first place
				int sumtoFirst = 0;
				for(int n = 0; n < stonecount.length; n++)
				{
					if(stonecount[player] < stonecount[n])
					{
						sumtoFirst += stonecount[n];
					}
				}
				return -sumtoFirst;
			}
			
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
	
	private double evaluateBombingPhase() {
		return PLAYABLE_SQUARES;
		
	}
}

