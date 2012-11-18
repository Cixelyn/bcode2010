package kevin1;


import battlecode.common.*;

public abstract class BasePlayer implements Runnable{
	protected final RobotController rc;
	public static final Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NONE };
	public BasePlayer(RobotController r) {
		this.rc = r;		
	}


}
