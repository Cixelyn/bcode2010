package lazer3.behaviors;

import java.util.Iterator;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import lazer3.MsgType;
import lazer3.RobotPlayer;


//copied from GoToTowerBehavior, so variables are copied and make no sense
public class GoToBroadcastedLocation extends Behavior{
	
	private MapLocation destinationLoc = new MapLocation(0,0);
	private MsgType msgtype;

	public GoToBroadcastedLocation(RobotPlayer player) {
		super(player);
	}
	
	public GoToBroadcastedLocation(RobotPlayer player, MsgType msgtype) {
		super(player);
		this.msgtype = msgtype;
		
		
	}

	@Override
	public boolean runActions() throws GameActionException {
		Iterator<Message> helpTower = player.myRadio.inbox.iterator();
		while (helpTower.hasNext()) {
			Message towerMsg = helpTower.next();
			if (towerMsg.ints[0] == msgtype.ordinal()) {
				destinationLoc = towerMsg.locations[2];
				helpTower.remove();
			}
		}
		if(destinationLoc.equals(new MapLocation(0,0))) return false;
		if (player.myRC.getLocation().distanceSquaredTo(destinationLoc) > 4) {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				Direction enemyDirection = player.myRC.getLocation().directionTo(destinationLoc);
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
