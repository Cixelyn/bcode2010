package lazer5.strategies;

import java.util.Iterator;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.JihadSpawnChargeBehavior;
import lazer5.behaviors.JihadSpawnSoldierBehavior;
import lazer5.communications.Broadcaster;
import lazer5.communications.Encoder;
import lazer5.communications.MsgType;
import lazer5.instincts.Instinct;
import lazer5.instincts.RevertedTransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SuperJihadStrategy extends Strategy {
	MapLocation mobLoc = player.myRC.getLocation();
	MapLocation baseLoc = player.myRC.getLocation();
	MapLocation storedTower;
	private int IGNORE_LEASH = 0;
	private int ROUNDS_UNTIL_RETREAT = 1000;
	private int enemyLastSeen = 0;
	private int commBroadcast = 0;
	private int COMM_BROADCAST_DELAY = 10;
	private int commTowerLoc = 50;
	private int COMM_LOCATION_DELAY = 50;
	private int ALERT_RADIUS_SQUARED = 64;
	private Instinct transfer;
	int randTimer = 20;


	private Behavior spawnSoldierBehavior;
	private Behavior spawnChargeBehavior;

	boolean chargingSoldier = false;

	public SuperJihadStrategy(RobotPlayer player) {
		super(player);
		transfer = new RevertedTransferInstinct(player);

		spawnSoldierBehavior = new JihadSpawnSoldierBehavior(player);
		spawnChargeBehavior = new JihadSpawnChargeBehavior(player);
	}

	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	public void runBehaviors() throws GameActionException {
		player.myRC.setIndicatorString(2, baseLoc.toString());
		Robot[] nearbyRobots = player.myIntel.getNearbyRobots();
		RobotInfo rInfo;
		Robot closestEnemy = null;
		MapLocation closestEnemyLoc = null;
		switch (player.myRC.getRobotType()) {
		case ARCHON:
			if(player.myRC.getEnergonLevel()>50 && chargingSoldier == false) {
				chargingSoldier = spawnSoldierBehavior.execute();
			}else if (chargingSoldier) {
				chargingSoldier = !spawnChargeBehavior.execute();
			} else{
				nearbyRobots = player.myIntel.getNearbyRobots();
				for (int i = 0; i < nearbyRobots.length; i++) {
					rInfo = player.myRC.senseRobotInfo(nearbyRobots[i]);
					if (rInfo.team == player.myOpponent) {
						closestEnemy = nearbyRobots[i];
						closestEnemyLoc = rInfo.location;
					}
				}
				if (closestEnemy != null) 
					player.myRadio.sendSingleDestination(MsgType.MSG_KILLNOW, closestEnemyLoc);
				else if (Clock.getRoundNum() - enemyLastSeen > ROUNDS_UNTIL_RETREAT && IGNORE_LEASH == 0) {
					goToLocation(baseLoc);
					break;
				}
				Iterator<Message> archonIt = player.myRadio.inbox.iterator();
				while(archonIt.hasNext()) {
					Message m = archonIt.next();
					MsgType type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
					if (type == MsgType.MSG_ENEMYTOWER) {
						IGNORE_LEASH = 1;
						mobLoc = m.locations[2];
						archonIt.remove();
						break;
					} else if (type == MsgType.MSG_KILLNOW) {
						enemyLastSeen = Clock.getRoundNum();
						mobLoc = m.locations[2];
						archonIt.remove();
						break;
					} else if (type == MsgType.MSG_DEFENDTOWER) {
						enemyLastSeen = Clock.getRoundNum();
						mobLoc = m.locations[2];
						archonIt.remove();
						break;
					} else if (type == MsgType.MSG_BASECAMP) {
						baseLoc = m.locations[2];
						archonIt.remove();
						break;
					}
				}
			}
			if (player.myRC.getLocation().distanceSquaredTo(mobLoc) < 16 && chargingSoldier == true)
				archonMoveRandom();
			else
				goToLocation(mobLoc);
			
			int currID = player.myIntel.getArchonID();
			if(currID==0 || currID==1) {
				player.changeStrategy(new ArchonBuilderStrategy(player));
			}
			
			
			break;
			
			
		
		}
	}

	private void goToLocation(MapLocation attackLoc) throws GameActionException {
		if (!(player.myRC.getLocation().equals(attackLoc))) {
			player.myNavi.bugTo(attackLoc);
		}

	}

	private void archonMoveRandom() throws GameActionException {
		Direction myDir = player.myRC.getDirection();
		if (randTimer == 20) {
			Direction changeTo = player.myUtils.randDir();
			if (myDir != changeTo && player.myRC.getRoundsUntilMovementIdle() == 0) {
				player.myRC.setDirection(changeTo);
				randTimer = 0;
				randTimer++;
			} else {
				randTimer = 20;
			}
		} else {
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.canMove(myDir) && player.myRC.hasActionSet() == false) {
				System.out.println("moving in direction");
				player.myRC.moveForward();
				randTimer++;
			} else
				randTimer = 20;
		}

		player.myRC.setIndicatorString(1, Integer.toString(randTimer));
	}

public void runInstincts() throws GameActionException {
	if (player.myRC.getRobotType() == RobotType.COMM) {
		
	} else {
	transfer.execute();
	}

}
}
