package swpg3.ai;

import swpg3.game.BitMap;
import swpg3.game.Vector2i;
import swpg3.game.map.Map;
import swpg3.game.map.MapManager;
import swpg3.game.map.MapWalker;
import swpg3.game.map.Tile;

/**
 * A Singleton-class which provides functionality to "analyze" the starting map. Concretely this means that some
 * static properties of the map are found out and saved in the AI class.
 * These are:
 * - number of solid squares
 * - location of solid squares
 * - number of reachable squares
 * (- location of reachable squares)
 * @author Ramil
 *
 */
public class Analyser {
	
	/**
	 * A private instance of the Analyser class.
	 */
	private static Analyser instance = null;
	
	/**
	 * A private constructor, so that getInstance() has to be called to get an instance of Analyser.
	 */
	private Analyser() {}
	
	/**
	 * Method which returns the singleton instance of Analyser. Creating one if not done yet.
	 * @return
	 */
	public static Analyser getInstance() 
	{
		if(instance == null) 
		{
			instance = new Analyser();
		}
		
		return instance;
	}
	
	/**
	 * The "main" method of this class. Iterates over the map and finds out static properties of the map as:
	 * - number of solid stones
	 * - location of solid stones
	 * - number of reachable squares
	 * (- location of reachable squares)
	 */ 
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
						AI.solidSquares.set(pos.x, pos.y, true);
						numberOfSolidSquares++;
					}
				}
			}
		}
			
		AI.PLAYABLE_SQUARES = playableSquares;
		AI.numberOfSolidSquares = numberOfSolidSquares;
	}
}
