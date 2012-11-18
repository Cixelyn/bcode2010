package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.BuildTowerLatticeBehavior;
import lazer5.communications.MsgType;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class CommBroadcastAvailableStrategy extends Strategy {
	
	private final Behavior buildTowerLatticeBehavior = new BuildTowerLatticeBehavior(player);
	
	MapLocation mobLoc = player.myRC.getLocation();
	MapLocation baseLoc = player.myRC.getLocation();
	MapLocation storedTower;
	private int waited = 0;
	private int HELP_DELAY = 10;

	public CommBroadcastAvailableStrategy(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		player.myRadio.sendSingleDestination(MsgType.MSG_BASECAMP, player.myRC.getLocation());
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		int currTime = (Clock.getRoundNum() + player.myRC.getRobot().getID());
		if (currTime % 20 == 0) {
			buildTowerLatticeBehavior.execute();
		} else if ((currTime + 10) % 20 == 0) {
			Robot[] nearbyRobots = player.myIntel.getNearbyRobots();
			RobotInfo rInfo = null;
			Robot closestEnemy = null;
			MapLocation closestEnemyLoc = null;

			for (int i = 0; i < nearbyRobots.length; i++) {
				rInfo = player.myRC.senseRobotInfo(nearbyRobots[i]);
				if (rInfo.team == player.myOpponent) {
					closestEnemy = nearbyRobots[i];
					closestEnemyLoc = rInfo.location;
					break;
				}
			}

			if (closestEnemy != null) {
				if (waited == HELP_DELAY) {
					player.myRadio.sendSingleDestination(MsgType.MSG_DEFENDTOWER, closestEnemyLoc);
					waited = 0;
				}
				waited++;
			} else {
				waited = 10;
			}
		}
	}

	@Override
	public void runInstincts() throws GameActionException {

	}

}
