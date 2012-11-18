package lazerguns1.behaviors;

import java.util.Iterator;

import lazerguns1.MsgType;
import lazerguns1.RobotPlayer;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class GoToTowerBehavior extends Behavior{
	
	MapLocation enemyLoc = new MapLocation(0,0);

	public GoToTowerBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() throws GameActionException {
		Iterator<Message> helpTower = player.myRadio.inbox.iterator();
		while (helpTower.hasNext()) {
			Message towerMsg = helpTower.next();
			if (towerMsg.ints[0] == MsgType.MSG_DEFENDTOWER.ordinal()) {
				enemyLoc = towerMsg.locations[2];
				helpTower.remove();
			}
		}
		if (player.myRC.getLocation().distanceSquaredTo(enemyLoc) > 4) {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				Direction enemyDirection = player.myRC.getLocation().directionTo(enemyLoc);
				Direction myDirection = player.myRC.getDirection();
				if (myDirection != enemyDirection)
					player.myRC.setDirection(enemyDirection);
				else if (player.myRC.canMove(enemyDirection)) {
					player.myRC.moveForward();
				} else {
					player.myNavi.bugInDirection(enemyDirection);
				}
			} else {
				return false;
			}
		} else {
			return true;
		}
		return false;
	}

}
