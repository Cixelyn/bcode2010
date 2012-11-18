package lazerguns2.behaviors;


import lazerguns2.*;
import battlecode.common.*;


public class WoutRushToTowerBehavior extends Behavior{
	
	private MapLocation towerLoc;
	
	public void setTowerLoc(MapLocation loc) {
		this.towerLoc = loc;
	}
	
	public WoutRushToTowerBehavior(RobotPlayer player) {
		super(player);
	}
	public boolean runActions() throws GameActionException {
		player.myNavi.bugTo(towerLoc);
		return true;
	}
}
