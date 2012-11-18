package lazerguns1.behaviors;

import lazerguns1.RobotPlayer;
import lazerguns1.filters.Filter;
import lazerguns1.filters.FilterFactory;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class MobTowerBehavior extends Behavior {
	
	Filter nearbyEnemies;
	Filter closestEnemy;
	
	public MobTowerBehavior(RobotPlayer player) {
		super(player);
		initFilters();
	}


	@Override
	public boolean runActions() throws GameActionException {
		Robot[] nearby = player.myIntel.getNearbyRobots();
		if (nearbyEnemies.weakest(nearby) != null) {
			RobotInfo target = player.myRC.senseRobotInfo(nearbyEnemies.weakest(nearby));
			MapLocation targetLoc = target.location;
			RobotType targetType = target.type;
			if (player.myRC.canAttackSquare(targetLoc)) {
				if (player.myRC.getRoundsUntilAttackIdle() == 0) {
					if (targetType == RobotType.ARCHON)
						player.myRC.attackAir(targetLoc);
					else
						player.myRC.attackGround(targetLoc);
				}
			}
		} else {
			if (closestEnemy.closest(nearby) != null) {
				RobotInfo newTarget = player.myRC.senseRobotInfo(closestEnemy.closest(nearby));
				MapLocation newLocation = newTarget.location;
				if (player.myRC.getLocation().distanceSquaredTo(newLocation) > 4) {
					if (player.myRC.getRoundsUntilMovementIdle() == 0) {
						Direction enemyDirection = player.myRC.getLocation().directionTo(newLocation);
						Direction myDirection = player.myRC.getDirection();
						if (myDirection != enemyDirection)
							player.myRC.setDirection(enemyDirection);
						else if (player.myRC.canMove(enemyDirection)) {
							player.myRC.moveForward();
						} else {
							player.myNavi.bugInDirection(enemyDirection);
						}
					}
				}
			}
		}
		return true;
	}
	
	public void initFilters() {
		nearbyEnemies = FilterFactory.enemiesInAttackRange(player.myRC, player.myIntel);
		closestEnemy = FilterFactory.enemiesInRange(player.myRC, player.myIntel, 9);
	}

}
