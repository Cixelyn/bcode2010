package lazerguns1.strategies;

import java.util.Iterator;

import lazerguns1.MsgType;
import lazerguns1.RobotPlayer;
import lazerguns1.behaviors.Behavior;
import lazerguns1.behaviors.SpawnChargeBehavior;
import lazerguns1.behaviors.SpawnSoldierBehavior;
import lazerguns1.filters.FilterFactory;
import lazerguns1.instincts.Instinct;
import lazerguns1.instincts.TransferInstinct;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class defenseMob extends Strategy {
	MapLocation mobLoc = player.myRC.getLocation();
	MapLocation baseLoc = player.myRC.getLocation();
	MapLocation storedTower;
	private int ROUNDS_UNTIL_RETREAT = 30;
	private int enemyLastSeen = 0;
	private int commBroadcast = 0;
	private int COMM_BROADCAST_DELAY = 10;
	private int commTowerLoc = 50;
	private int COMM_LOCATION_DELAY = 50;
	private int ALERT_RADIUS_SQUARED = 64;
	private Instinct transfer;
	
	
	private Behavior spawnSoldierBehavior;
	private Behavior spawnChargeBehavior;
	
	boolean chargingSoldier = false;

	public defenseMob(RobotPlayer player) {
		super(player);
		transfer = new TransferInstinct(player);
		
		spawnSoldierBehavior = new SpawnSoldierBehavior(player);
		spawnChargeBehavior = new SpawnChargeBehavior(player);
	}

	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	public void runBehaviors() throws GameActionException {
		player.myRC.setIndicatorString(2, baseLoc.toString());
		switch (player.myRC.getRobotType()) {
		case COMM:
			Robot closestEnemy = FilterFactory.enemiesInRange(player.myRC, player.myIntel, ALERT_RADIUS_SQUARED).closest(player.myIntel.getNearbyRobots());
			if (closestEnemy != null) {
				MapLocation closestEnemyLoc = player.myRC.senseRobotInfo(closestEnemy).location;
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
			break;
		case ARCHON:
			if(player.myRC.getEnergonLevel()>50 && chargingSoldier == false) {
				chargingSoldier = spawnSoldierBehavior.execute();
			}else if (chargingSoldier) {
				chargingSoldier = !spawnChargeBehavior.execute();
			} else{
				Robot archonEnemy = FilterFactory.enemiesInRange(player.myRC, player.myIntel, 49).closest(player.myIntel.getNearbyRobots());
				if (archonEnemy != null) 
					player.myRadio.sendSingleDestination(MsgType.MSG_KILLNOW, player.myRC.senseRobotInfo(archonEnemy).location);
				else if (Clock.getRoundNum() - enemyLastSeen > ROUNDS_UNTIL_RETREAT) {
					goToLocation(baseLoc);
					break;
				}
				Iterator<Message> archonIt = player.myRadio.inbox.iterator();
				while(archonIt.hasNext()) {
					Message m = archonIt.next();
					if (m.ints[0] == MsgType.MSG_KILLNOW.ordinal()) {
						enemyLastSeen = Clock.getRoundNum();
						mobLoc = m.locations[2];
						archonIt.remove();
						break;
					} else if (m.ints[0] == MsgType.MSG_DEFENDTOWER.ordinal()) {
						enemyLastSeen = Clock.getRoundNum();
						mobLoc = m.locations[2];
						archonIt.remove();
						break;
					} else if (m.ints[0] == MsgType.MSG_BASECAMP.ordinal()) {
						baseLoc = m.locations[2];
						archonIt.remove();
						break;
					}
				}
			}
			goToLocation(mobLoc);
			break;
		case SOLDIER:
			Robot nearbyEnemy = FilterFactory.enemiesInRange(player.myRC, player.myIntel, 15).closest(player.myIntel.getNearbyRobots());
			if (nearbyEnemy != null) {
				enemyLastSeen = Clock.getRoundNum();
				RobotInfo rInfo = player.myRC.senseRobotInfo(nearbyEnemy);
				mobLoc = rInfo.location;
				if (player.myRC.canAttackSquare(mobLoc)) {
					if (player.myRC.getRoundsUntilAttackIdle() == 0) {
						if (rInfo.type == RobotType.ARCHON)
							player.myRC.attackAir(mobLoc);
						else
							player.myRC.attackGround(mobLoc);
					}
				} else
					goToLocation(mobLoc);
			} else {
				Iterator<Message> soldierIt = player.myRadio.inbox.iterator();
				while(soldierIt.hasNext()) {
					Message m = soldierIt.next();
					if (m.ints[0] == MsgType.MSG_KILLNOW.ordinal()) {
						enemyLastSeen = Clock.getRoundNum();
						mobLoc = m.locations[2];
						soldierIt.remove();
						break;
					} else if (m.ints[0] == MsgType.MSG_DEFENDTOWER.ordinal()) {
						enemyLastSeen = Clock.getRoundNum();
						mobLoc = m.locations[2];
						soldierIt.remove();
						break;
					} else if (m.ints[0] == MsgType.MSG_BASECAMP.ordinal()) {
						baseLoc = m.locations[2];
						soldierIt.remove();
						break;
					}
				
				}
				if (Clock.getRoundNum() - enemyLastSeen > ROUNDS_UNTIL_RETREAT) {
					goToLocation(baseLoc);
					break;
				}
				goToLocation(mobLoc);
			}
			break;
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
