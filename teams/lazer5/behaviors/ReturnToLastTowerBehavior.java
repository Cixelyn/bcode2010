package lazer5.behaviors;

import lazer5.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class ReturnToLastTowerBehavior extends Behavior {
	
	private MapLocation dest;
	

	public ReturnToLastTowerBehavior(RobotPlayer player) {
		super(player);
	}
	
	public ReturnToLastTowerBehavior(RobotPlayer player, MapLocation dest){
		super(player);
		this.dest = dest;
		
	}

	@Override
	public boolean runActions() throws GameActionException {
		if(player.myRC.getLocation().distanceSquaredTo(dest) >= 16){
			player.myNavi.bugTo(dest);
			return false;
		}
		else return true;
	}

}
