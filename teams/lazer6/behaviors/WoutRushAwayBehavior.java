package lazer6.behaviors;

import lazer6.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class WoutRushAwayBehavior extends Behavior {

	private Direction rushDir;
	private MapLocation myLoc;
	
	public WoutRushAwayBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions(){
		myLoc = myRC.getLocation();
		rushDir = fluxDirection();
		//FIX LATER
		if (player.myAct.moveInDir(player.myNavi.bugTo(myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir)))) return true;
		return false;
	}

	/**
	 * finds the direction that has squares with the highest flux
	 * @param none
	 * @author Kevin Li
	 * @return Direction of highest flux
	 */
	public Direction fluxDirection() {
		int highestFlux = 0;
		Direction dir = Direction.NONE;
		try {
			for (int i = 0; i < 8; i++) {
				if (player.myProfiler.closestEnemyInfo != null) {
					if (Direction.values()[i] != myLoc.directionTo(player.myProfiler.closestEnemyInfo.location)) {
						int flux = 0;
						if (i%2==0) {
							flux = myRC.senseFluxAtLocation(myLoc.add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]));
						} else {
							flux = myRC.senseFluxAtLocation(myLoc.add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]));
						}



						if (flux > highestFlux) {
							highestFlux = flux;
							dir = Direction.values()[i];
						}
					}
				} else {
					int flux = 0;
					if (i%2==0) {
						flux = myRC.senseFluxAtLocation(myLoc.add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]));
					} else {
						flux = myRC.senseFluxAtLocation(myLoc.add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]));
					}



					if (flux > highestFlux) {
						highestFlux = flux;
						dir = Direction.values()[i];
					}
				}
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: finding fluxDirection");
			e.printStackTrace();
		}
		if (dir != Direction.NONE) {
			return dir;
		} else {
			return player.myUtils.randDir();
		}
	}
}
