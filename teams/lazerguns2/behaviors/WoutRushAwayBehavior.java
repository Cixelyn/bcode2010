package lazerguns2.behaviors;

import lazerguns2.*;
import lazerguns2.filters.*;
import battlecode.common.*;

public class WoutRushAwayBehavior extends Behavior{
	
	private Direction rushDir;

	/*public WoutRushAwayBehavior(RobotPlayer player, Direction dir) {
		super(player);
		this.rushDir = dir;
	}*/
		
	public void setRushDir(Direction dir) {
		this.rushDir = dir;
	}
	
	public WoutRushAwayBehavior(RobotPlayer player) {
		super(player);

	}
	public boolean runActions() throws GameActionException {
		//while ((availableEnergon >= (tilesTraveled * RobotType.WOUT.energonUpkeep()+5)) && !(enemySighted)) {
			//if (!(player.myRC.isMovementActive())) {
				player.myNavi.bugInDirection(rushDir);
				//tilesTraveled++;
				//distance = player.myRC.getLocation().distanceSquaredTo(origLoc);
				//if (!(enemyRobots.filter(player.myIntel.getNearbyRobots())).isEmpty()) {
					//enemySighted = true;
				//}
			//}		
		//}
		return true;
	}

}
