package lazer6.behaviors;

import lazer6.RobotPlayer;
import battlecode.common.MapLocation;

public class WoutBuildReturnBehavior extends Behavior {

	public WoutBuildReturnBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() {
		MapLocation myLoc = myRC.getLocation();
		MapLocation targetArchon = myProfiler.alliedArchons[0];
		if (myLoc.distanceSquaredTo(targetArchon) <= 1) {
			return true;
		} else {
			myAct.moveInDir(myNavi.bugTo(targetArchon));
		}
		return false;
	}

}
