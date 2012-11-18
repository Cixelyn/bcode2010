package lazer3.behaviors;

import lazer3.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class MoveFWDBehavior extends Behavior {

	public MoveFWDBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() throws GameActionException {
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			Direction myDir = player.myRC.getDirection();
			if (player.myRC.canMove(myDir)) {
				player.myRC.moveForward();
				return true;
			}
		}
		return false;
	}

}