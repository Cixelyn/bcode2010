package lazer5.strategies;

import java.util.LinkedList;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.WaitForChargingBehavior;
import lazer5.communications.Broadcaster;
import lazer5.communications.Encoder;
import lazer5.communications.MsgType;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SoldierAttackStrategy extends Strategy {
	private Behavior WaitForCharging;
	private Robot[] nearby;
	private RobotInfo opponent;
	private MapLocation attackSquare;
	private RobotType attackType;
	private int state = 0;
	private int priority = 0;

	public SoldierAttackStrategy(RobotPlayer player) {
		super(player);
		WaitForCharging = new WaitForChargingBehavior(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		if (WaitForCharging.execute()) {
			return true;
		}
		return false;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// UNTESTED
		// soldier attack states:
		// 0 - waiting for broadcast or enemy in range, swarm archons
		// 1 - have enemy location but can't attack it so move to it
		// 2 - attack
		player.myRC.setIndicatorString(1, Integer.toString(state));
		switch (state) {
		case (0):
			for (Message m:player.myRadio.inbox) {
				if (Encoder.decodeMsgType(m.ints[Broadcaster.idxData]) == MsgType.MSG_KILLNOW) {
					attackSquare = m.locations[Broadcaster.firstData];
					state = 1;
					break;
				}
			}
			nearby = player.myIntel.getNearbyRobots();
			if (nearby.length > 0) {
				for (Robot r:nearby) {
					opponent = player.myRC.senseRobotInfo(r);
					if (opponent.team == player.myOpponent) {
						// if yes, check if location is in attack range
						attackSquare = opponent.location;
						attackType = opponent.type;
						if (player.myRC.canAttackSquare(attackSquare)) {
							state = 2;
							break;
						}
						state = 1;
						break;				
					}
				}
			}
			
			
			player.myNavi.swarmTo(player.myIntel.getNearestArchon());
		break;
		case (1):
			// if we can't attack a square, it's out of range
			// navigate to it
			if (!player.myRC.canAttackSquare(attackSquare)) {
				player.myNavi.swarmTo(attackSquare);
			} else {
				state = 2;
			}
		break;
		case (2):
			// fuck shit up
			if (player.myRC.canAttackSquare(attackSquare)) {
				if (player.myRC.senseGroundRobotAtLocation(attackSquare) == null && player.myRC.senseAirRobotAtLocation(attackSquare) == null) {
					state = 0;
					break;
				}
				if (player.myRC.getRoundsUntilAttackIdle() == 0) {
					if (attackType == RobotType.ARCHON) {
						player.myRC.attackAir(attackSquare);
						System.out.println("attack successful");
					} else {
						player.myRC.attackGround(attackSquare);
						System.out.println("attack successful");
					}
				return;
				} else {
					break;
				}
			} else {
					state = 1;
					break;
			}
		}
	}


	private void swarmArchons() {
		
		
	}

	@Override
	public void runInstincts() throws GameActionException {

	}

}
