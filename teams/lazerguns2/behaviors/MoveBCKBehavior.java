package lazerguns2.behaviors;

import lazerguns2.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class MoveBCKBehavior extends Behavior {

	public MoveBCKBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() throws GameActionException {
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			Direction myDir = player.myRC.getDirection();
			if (player.myRC.canMove(myDir.opposite())) {
				player.myRC.moveBackward();
			}
		}
		return true;
	}

}
