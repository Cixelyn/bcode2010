package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.SpawnChainerBehavior;
import lazer5.behaviors.SpawnChargeBehavior;
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

public class ArchonChainerStrategy extends Strategy {
	private Instinct transfer;

	private Robot[] nearbyRobots;
	private RobotInfo nearbyInfo;
	private Robot nearbyRobot;
	private MapLocation atkLoc = player.myRC.getLocation();
	private MapLocation baseLoc = player.myRC.getLocation();

	private int i = 0;
	private int lastSeen = 0;
	private int TIME_LEASH = 100;
	private int DISTANCE_LEASH = 225;
	int randTimer = 20;

	//private Behavior spawnSoldierBehavior;
	private Behavior spawnChainerBehavior;
	private Behavior spawnChargeBehavior;

	boolean chargingChainer = false;

	public ArchonChainerStrategy(RobotPlayer player) {
		super(player);
		transfer = new RevertedTransferInstinct(player);

		//spawnSoldierBehavior = new SpawnChainerBehavior(player);
		spawnChainerBehavior = new SpawnChainerBehavior(player);
		spawnChargeBehavior = new SpawnChargeBehavior(player);
	}

	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	public void runBehaviors() throws GameActionException {
		player.myRadio.sendRobotList(player.myIntel.getNearbyRobots());
		nearbyRobots = null;
		nearbyRobot = null;
		int currID = player.myIntel.getArchonID();
		if (currID == 0 || currID == 1) {
			player.changeStrategy(new ArchonBuilderStrategy(player));
		}
	

		if (player.myRC.getEnergonLevel() > 50 && chargingChainer == false) {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				chargingChainer = spawnChainerBehavior.execute();
			}
		} else if (chargingChainer) {
			chargingChainer = !spawnChargeBehavior.execute();
		} else {
			nearbyRobots = player.myIntel.getNearbyRobots();
			for (i = 0; i < nearbyRobots.length; i++) {
				nearbyInfo = player.myRC.senseRobotInfo(nearbyRobots[i]);
				if (nearbyInfo.team == player.myOpponent) {
					nearbyRobot = nearbyRobots[i];
					break;
				}
			}
			
			if (nearbyRobot != null) {
				player.myRadio.sendSingleDestination(MsgType.MSG_KILLNOW, player.myRC.senseRobotInfo(nearbyRobot).location);
			} else if (Clock.getRoundNum() - lastSeen > TIME_LEASH) {
				player.myNavi.archonBugTo(baseLoc);
				return;
			}
			
			for (i = 0; i < player.myRadio.inbox.size(); i++) {
				Message m = player.myRadio.inbox.get(i);
				MsgType type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
				if (type == MsgType.MSG_KILLNOW) {
					lastSeen = Clock.getRoundNum();
					atkLoc = m.locations[2];
					break;
				} else if (type == MsgType.MSG_DEFENDTOWER) {
					lastSeen = Clock.getRoundNum();
					atkLoc = m.locations[2];
					break;
				} else if (type == MsgType.MSG_BASECAMP) {
					baseLoc = m.locations[2];
					break;
				}
			}
		}
		
		if (player.myRC.getLocation().distanceSquaredTo(atkLoc) < 16 && chargingChainer == true) {
			archonMoveRandom();
		} else {
			player.myNavi.archonBugTo(atkLoc);
		}
		
	}
	
	private void archonMoveRandom() throws GameActionException {
		Direction myDir = player.myRC.getDirection();
		if (randTimer == 20) {
			Direction changeTo = player.myUtils.randDir();
			if (myDir != changeTo && player.myRC.getRoundsUntilMovementIdle() == 0 & player.myRC.hasActionSet() == false) {
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
		transfer.execute();
	}
}
