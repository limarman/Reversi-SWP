package swpg3.ai;

import java.util.HashSet;

import swpg3.Map;
import swpg3.MapManager;
import swpg3.MapWalker;
import swpg3.Tile;
import swpg3.Vector2i;

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
		AI.solidSquares = new HashSet<>();
		int playableSquares = 0;
		Map map = MapManager.getInstance().getCurrentMap();
		for(int w = 0; w < MapManager.getInstance().getWidth(); w++)
		{
			for(int h = 0; h < MapManager.getInstance().getHeight(); h++)
			{
				Vector2i pos = new Vector2i(w,h);
				Tile t = map.getTileAt(w,h);
				if(!t.isHole())
				{
					playableSquares++;
					
					//is it a solid square?
					//looking whether all 4 directions are blocked
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
						AI.solidSquares.add(pos.clone());
					}
				}
			}
		}
			
		AI.PLAYABLE_SQUARES = playableSquares;
	}
}
