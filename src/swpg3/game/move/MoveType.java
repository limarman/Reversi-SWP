package swpg3.game.move;

public enum MoveType {

	//Building phase
	BONUS_OVERRIDE(10),
	CHOICE(9),
	BONUS_BOMB(8),
	INVERSION(7),
	NORMAL_BUILDING(6),
	OVERRIDE_USE(1),
	SELF_OVERRIDE_USE(0),
	
	//Bombing Phase
	NORMAL_BOMBING(6),
	SELF_BOMB(0);
	
	public final int moveValue;
	
	MoveType(int moveValue)
	{
		this.moveValue = moveValue;
	}
}
