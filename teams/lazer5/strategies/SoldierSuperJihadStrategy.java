package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.WaitForChargingBehavior;
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

public class SoldierSuperJihadStrategy extends Strategy {
	private int i;
	private int state = 0;
	private int lastSeen = 0;
	private int TIME_LEASH = 100;
	
	private Robot[] nearbyRobots;

	private Robot nearbyRobot;

	private RobotInfo nearbyInfo;

	private RobotType atkType;


	private MapLocation archonCoM;
	private MapLocation comeHither;
	private MapLocation toAtk;
	private MapLocation baseLoc;

	private Instinct transfer;
	
	private Behavior chargeSoldier;


	public SoldierSuperJihadStrategy(RobotPlayer player) {
		super(player);
		transfer = new RevertedTransferInstinct(player);
		chargeSoldier = new WaitForChargingBehavior(player);
	}

	public boolean beginStrategy() throws GameActionException {
		
		return chargeSoldier.execute();
	}

	public void runBehaviors() throws GameActionException {
		nearbyRobots = null;
		nearbyRobot = null;
		baseLoc = player.myIntel.getNearestAttackArchon();
		
		
		nearbyRobots = player.myIntel.getNearbyRobots();
		for (i = 0; i < nearbyRobots.length; i++) {
			nearbyInfo = player.myRC.senseRobotInfo(nearbyRobots[i]);
			if (nearbyInfo.team == player.myOpponent) {
				nearbyRobot = nearbyRobots[i];
				break;
			}
		}
		
		if (nearbyRobot != null) {
			lastSeen = Clock.getRoundNum();
			nearbyInfo = player.myRC.senseRobotInfo(nearbyRobot);
			toAtk = nearbyInfo.location;
			if (player.myRC.canAttackSquare(toAtk)) {
				if (player.myRC.getRoundsUntilAttackIdle() == 0) {
					if (nearbyInfo.type == RobotType.ARCHON) {
						player.myRC.attackAir(toAtk);
					} else {
						player.myRC.attackGround(toAtk);
					}
				}
			} else {
				player.myNavi.swarmTo(toAtk);
			}
		} else {
			for (i = 0; i < player.myRadio.inbox.size(); i++) {
				Message m = player.myRadio.inbox.get(i);
				MsgType type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
				if (type == MsgType.MSG_ENEMYHERE) {
					lastSeen = Clock.getRoundNum();
					toAtk = m.locations[2];
					break;
				}
				else if (type == MsgType.MSG_KILLNOW) {
					lastSeen = Clock.getRoundNum();
					toAtk = m.locations[2];
					break;
				} else if (type == MsgType.MSG_DEFENDTOWER) {
					lastSeen = Clock.getRoundNum();
					toAtk = m.locations[2];
					break;
				} else if (type == MsgType.MSG_BASECAMP) {
					baseLoc = m.locations[2];
					break;
				}
			}
			
			if (Clock.getRoundNum() - lastSeen > TIME_LEASH) {
			//	System.out.println("stuck here");
				goToLocation(baseLoc);
				return;
			}
			player.myNavi.swarmTo(toAtk);
			return;
		}
	}

	private void goToLocation(MapLocation attackLoc) throws GameActionException {
		if (!(player.myRC.getLocation().equals(attackLoc))) {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				Direction attackDirection = player.myRC.getLocation().directionTo(attackLoc);
				Direction myDirection = player.myRC.getDirection();
				if (myDirection != attackDirection)
					player.myRC.setDirection(attackDirection);
				else if (player.myRC.canMove(attackDirection))
					player.myRC.moveForward();
				else
					player.myNavi.bugInDirection(attackDirection);
			}
		}

	}
	
	
	public void runInstincts() throws GameActionException {
		transfer.execute();
	}
}
