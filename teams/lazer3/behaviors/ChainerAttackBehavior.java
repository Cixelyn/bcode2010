package lazer3.behaviors;

import java.util.Iterator;

import lazer3.MsgType;
import lazer3.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class ChainerAttackBehavior extends Behavior {
	// I don't actually know the attack range of a chainer
	// If someone knows, add the SQUARED VALUE here
	final static int CHAIN_ATTACK_INFO_LIFE = 3;
	final static int CHAIN_ATTACK_RANGE_SQ = 100;
	
	public ChainerAttackBehavior(RobotPlayer player) {
		super(player);
	}
	
	
	// My knowledge of broadcasting is quite limited so if someone understands it 
	// well and can update this code so that it is constantly moving towards an
	// updated location, that would be swell
	// This function moves a chainer into min attack range of a unit
	public void minAttackRange(MapLocation enemyLoc) throws GameActionException {
		MapLocation myLoc = player.myRC.getLocation();
		int distanceTo = myLoc.distanceSquaredTo(enemyLoc);
		int distanceMoved = 0;
		Direction directionTo = myLoc.directionTo(enemyLoc);
		if (player.myRC.getDirection() != directionTo)
			player.myRC.setDirection(directionTo);
		while (distanceTo - distanceMoved > CHAIN_ATTACK_RANGE_SQ) {
			player.myRC.moveForward();
		}
	}
	
	// Check for chainer-specific communications from comm tower
	// Attack and/or move to attack
	public boolean runActions() throws GameActionException {
		MapLocation splashCenter = new MapLocation(0,0);
		Iterator<Message> chainMessage = player.myRadio.inbox.iterator();
		while (chainMessage.hasNext()) {
			Message m = chainMessage.next();
			if (m.ints[0] == MsgType.MSG_CHAINATTACK.ordinal()) {
				if (Clock.getRoundNum() - m.ints[3] < CHAIN_ATTACK_INFO_LIFE)
					splashCenter = m.locations[2];
			}
			chainMessage.remove();
		}
		if (player.myRC.canAttackSquare(splashCenter)) {
			if (player.myRC.senseAirRobotAtLocation(splashCenter) != null)
				player.myRC.attackAir(splashCenter);
			else
				player.myRC.attackGround(splashCenter);
		} else {
			minAttackRange(splashCenter);
		}
		return true;
	}
}
