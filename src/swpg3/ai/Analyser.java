package swpg3.ai;

import java.util.Stack;

import swpg3.game.BitMap;
import swpg3.game.Vector2i;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.map.MapWalker;
import swpg3.game.map.Tile;

public class Analyser {
	
	private static Analyser instance = null;
	
	private Analyser() {}
	
	public static Analyser getInstance() 
	{
		if(instance == null) 
		{
			instance = new Analyser();
		}
		
		return instance;
	}
	
	//##################################################
	// Function for initial map-analysis
	//##################################################
	public void analyseMap()
	{
		AI.solidSquares = new BitMap(MapManager.getInstance().getWidth(), MapManager.getInstance().getHeight());
		int playableSquares = 0;
		int numberOfSolidSquares = 0;
		Map map = MapManager.getInstance().getCurrentMap();
		for(int w = 0; w < MapManager.getInstance().getWidth(); w++)
		{
			for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
			{
				Vector2i pos = new Vector2i(w, h);
				Tile t = map.getTileAt(w, h);
				
				
				if (!t.isHole())
				{
					// is it a solid square?
					// looking whether all 4 directions are blocked
					boolean directionsBlocked[] = new boolean[4];
					MapWalker tester = new MapWalker(map);
					tester.setPosition(pos);
					for(int i = 0; i<8; i++)
					{
						tester.setDirection(Vector2i.mapDirToVector(i));
						
						//direction is blocked!
						if(!tester.canStep())
						{
							directionsBlocked[i % 4] = true;
						}
					}
					
					//all directions are blocked -> solidSquare
					if(directionsBlocked[0] && directionsBlocked[1] && directionsBlocked[2] && directionsBlocked[3])
					{
						AI.solidSquares.set(pos.x, pos.y, true);
					}
				}

				// reachable components
				if (t.isOccupied() && !AI.reachableSquares.get(w, h))
				{
					boolean meetsCondition = false;
					for (int i = 0; i < 8 && !meetsCondition; i++)
					{
						MapWalker mw = new MapWalker(map, new Vector2i(w, h), Vector2i.mapDirToVector(i));
						if (mw.canStep())
						{
							mw.step();
							Tile curTile = map.getTileAt(mw.getPosition());
							if (curTile.isOccupied() && curTile.getStatus() != t.getStatus() && mw.canStep())
							{
								mw.step();
								if (map.getTileAt(mw.getPosition()).isEmpty())
								{
									meetsCondition = true;
									break;
								}
							}
						}
					}
					if (meetsCondition)
					{
						depthSearch(map, w, h);
					}
				}

			}
		}
		
		//iterate once again over the reachable squares bitmap and count the reachable squares
		//as well as the reachable solid stones
		for(int w = 0; w < MapManager.getInstance().getWidth(); w++) 
		{
			for(int h = 0; h < MapManager.getInstance().getHeight(); h++) 
			{
				if(AI.reachableSquares.get(w,h)) 
				{
					playableSquares++;
					if(AI.solidSquares.get(w, h)) 
					{
						numberOfSolidSquares++;
					}
				}
			}
		}

		AI.PLAYABLE_SQUARES = playableSquares;
		AI.numberOfSolidSquares = numberOfSolidSquares;
	}
}
