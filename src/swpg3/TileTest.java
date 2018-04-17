/**
 * 
 */
package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author eric
 *
 */
class TileTest {

	@Test
	void testTile()
	{
		Tile toTest = new Tile();

		assertTrue(toTest.getStatus() == TileStatus.HOLE, "New Tile not a hole");
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				assertFalse(toTest.hasTransitionTo(new Vector2i(x, y)), "New Tile has a Transition");

		toTest = new Tile(TileStatus.CHOICE);

		assertTrue(toTest.getStatus() == TileStatus.CHOICE, "New Tile not a CHOICE");
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				assertFalse(toTest.hasTransitionTo(new Vector2i(x, y)), "New Tile has a Transition");
	}

	@Test
	void testAddTransition()
	{
		Tile toTest = new Tile();
		Tile target = new Tile(TileStatus.BONUS);

		Vector2i dir1 = new Vector2i(1,0);
		
		// Test adding Transition
		toTest.addTransition(new Transition(target, dir1), dir1);
		assertTrue(toTest.hasTransitionTo(dir1), "Transition not found");
		
		// Test retrieving transition
		Transition trans = toTest.getTransitionTo(dir1);
		assertTrue(trans != null && trans.getEndpoint() == target, "Not correct Transition");

		try
		{
			toTest.addTransition(new Transition(target, new Vector2i(0, 1)), dir1);
			fail("Added to Transitions in same Direction");
		} catch (IllegalArgumentException e) {
			// this needs to happen
		}
		finally {}
		
		try
		{
			toTest.addTransition(new Transition(target, new Vector2i(1,1)), new Vector2i(0,0));
			fail("Transition in no Direction allowed");
		}  catch (IllegalArgumentException e) {
			// this needs to happen
		}
		finally {}

	}

}
