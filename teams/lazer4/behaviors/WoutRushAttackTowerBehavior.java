package lazer4.behaviors;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import lazer4.RobotPlayer;

public class WoutRushAttackTowerBehavior extends Behavior{
	
	private MapLocation enemyTowerLoc;
	
	public void setEnemyTowerLoc(MapLocation loc) {
		this.enemyTowerLoc = loc;
	}
	
	public WoutRushAttackTowerBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean runActions() throws GameActionException {
		// TODO Auto-generated method stub
		if (player.myRC.canAttackSquare(enemyTowerLoc) && (player.myRC.getRoundsUntilAttackIdle()==0)) {
			player.myRC.attackGround(enemyTowerLoc);
			
			//return true if it killed the enemy tower
			if (player.myRC.senseRobotInfo(player.myRC.senseGroundRobotAtLocation(enemyTowerLoc)).energonLevel == 0.0) return true;
		}
		
		return false;
	}

}
