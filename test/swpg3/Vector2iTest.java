package swpg3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import swpg3.Vector2i;

class Vector2iTest {

	@Test
	void testVector2iIntInt()
	{
		Vector2i v = new Vector2i(1, 2);
		assertEquals(1, v.x, "X is off");
		assertEquals(2, v.y, "Y is off");
	}

	@Test
	void testAdd()
	{
		Vector2i v = new Vector2i();
		Vector2i v2 = new Vector2i(1, 1);
		Vector2i v3 = new Vector2i(-3, -4);

		v.add(v2);
		assertEquals(1, v.x, "Addition failed (x)");
		assertEquals(1, v.y, "Addition failed (y)");

		v.add(v3);
		assertEquals(-2, v.x, "Addition failed (x)");
		assertEquals(-3, v.y, "Addition failed (y)");
	}

	@Test
	void testEquals()
	{
		Vector2i v = new Vector2i(1, 1);
		Vector2i v2 = new Vector2i(1, 1);
		Vector2i v3 = new Vector2i(-3, -4);

		assertTrue(v.equals(v2), "Failed comparing equal Vectors");
		assertFalse(v.equals(v3), "Failed comparing not equal vectors");

	}

	@Test
	void testIsZero()
	{
		Vector2i v = new Vector2i();
		Vector2i v2 = new Vector2i(1, 1);

		assertTrue(v.isZero(), "Zero Vector failed");
		assertFalse(v2.isZero(), "Not zero Vector failed");

	}

}
