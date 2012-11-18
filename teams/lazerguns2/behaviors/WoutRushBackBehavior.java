package lazerguns2.behaviors;

import lazerguns2.*;
import battlecode.common.*;

public class WoutRushBackBehavior extends Behavior{
	
	private MapLocation targetArchon;
	
	public WoutRushBackBehavior(RobotPlayer player) {
		super(player);
	}
	public void setTargetArchon(MapLocation loc) {
		this.targetArchon = loc;
	}
	public boolean runActions() throws GameActionException {
		//while (player.myRC.getLocation().distanceSquaredTo(player.myIntel.getNearestArchon()) > 0) {
			//if (!(player.myRC.isMovementActive())) {
				player.myNavi.bugTo(targetArchon);
			//}
		//}
			
		return true;
	}
}
