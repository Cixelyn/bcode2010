package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.communications.MsgType;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class CommSuperJihadStrategy extends Strategy {
	MapLocation mobLoc = player.myRC.getLocation();
	MapLocation baseLoc = player.myRC.getLocation();
	MapLocation storedTower;
	private int commBroadcast = 0;
	private int COMM_BROADCAST_DELAY = 10;
	private int commTowerLoc = 50;
	private int COMM_LOCATION_DELAY = 50;
	int randTimer = 20;


	public CommSuperJihadStrategy(RobotPlayer player) {
		super(player);

	}

	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	public void runBehaviors() throws GameActionException {
		Robot[] nearbyRobots = player.myIntel.getNearbyRobots();
		RobotInfo rInfo;
		Robot closestEnemy = null;
		MapLocation closestEnemyLoc = null;

		for (int i = 0; i < nearbyRobots.length; i++) {
			rInfo = player.myRC.senseRobotInfo(nearbyRobots[i]);
			if (rInfo.team == player.myOpponent) {
				closestEnemy = nearbyRobots[i];
				closestEnemyLoc = rInfo.location;
			}
		}

		if (closestEnemy != null) {
			if (commBroadcast == COMM_BROADCAST_DELAY) {
				player.myRadio.sendSingleDestination(MsgType.MSG_DEFENDTOWER, closestEnemyLoc);
				commBroadcast = 0;
			}
			commBroadcast++;
		} else {
			commBroadcast = 10;
		}
		if (commTowerLoc == COMM_LOCATION_DELAY) {
			player.myRadio.sendSingleDestination(MsgType.MSG_BASECAMP, player.myRC.getLocation());
			commTowerLoc = 0;
		}
		commTowerLoc++;

	}


	public void runInstincts() throws GameActionException {

	}
}
