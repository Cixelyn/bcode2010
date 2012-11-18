package navbot;



import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Navigation {
	RobotController rc;
	boolean isTracing = false;
	MapLocation traceStart = null;

	public Navigation(RobotController r) {
		rc = r;

	}
	public void bugTo(Direction goalDir) throws GameActionException {
		if (goalDir != Direction.NONE && goalDir != null) {
			// if you aren't tracing, move in the direction of the target
			if (!isTracing) {
				if (rc.getDirection() != goalDir)
					rc.setDirection(goalDir);
				else if (rc.canMove(goalDir))
					rc.moveForward();
				else{
					isTracing = true;
					traceStart = rc.getLocation();
				}
			} else {
				if (rc.canMove(goalDir) && calcDirection(traceStart) != goalDir) {
					isTracing = false;
					rc.setDirection(goalDir);
				} else if (rc.canMove(rc.getDirection()))
					rc.moveForward();
				else
					rc.setDirection(rc.getDirection().rotateRight());
			}
		}
	}
	/*
	 * Judges the direction one should travel to reach the goal. Compares
	 * current position with position of the target to calculate.
	 */
	private Direction calcDirection(MapLocation goal) {
		int currX = rc.getLocation().getX();
		int currY = rc.getLocation().getY();

		if (currX < goal.getX()) {
			if (currY < goal.getY())
				return Direction.SOUTH_EAST;
			else if (currY > goal.getY())
				return Direction.NORTH_EAST;
			else
				return Direction.EAST;
		} else if (currX > goal.getX()) {
			if (currY < goal.getY())
				return Direction.SOUTH_WEST;
			else if (currY > goal.getY())
				return Direction.NORTH_WEST;
			else
				return Direction.WEST;
		} else if (currY < goal.getY())
			return Direction.SOUTH;
		else if (currY > goal.getY())
			return Direction.NORTH;
		else
			return Direction.NONE;

	}
}