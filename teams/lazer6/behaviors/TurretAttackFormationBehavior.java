package lazer6.behaviors;

import lazer6.RobotPlayer;
import battlecode.common.MapLocation;


public class TurretAttackFormationBehavior extends Behavior {
	private MapLocation nearestArchon;
	private MapLocation myLoc;

	public TurretAttackFormationBehavior(RobotPlayer player) {
		super(player);
		player.myProfiler.switchSwarmMode(1);
	}

	@Override
	public boolean runActions() {
		if (myRC.getRoundsUntilAttackIdle() == 1) {
			player.myAct.moveBCK();
			return false;
		}
		
		nearestArchon = player.myProfiler.closestAlliedArchon;
		myLoc = myRC.getLocation();
		if (myLoc.distanceSquaredTo(nearestArchon) < 3 && myRC.getEnergonLevel() > 20) {
				player.myAct.moveFWD();
				//return false;
		} else {
			player.myAct.moveInDir(player.myNavi.bugTo(player.myProfiler.calculateSwarmUnitLocation(nearestArchon)));
//			player.myAct.moveInDir(player.myNavi.bugInDir(myProfiler.armyDirection()));
		}
		return true;
	}

}
