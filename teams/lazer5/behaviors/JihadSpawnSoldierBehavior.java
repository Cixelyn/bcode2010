package lazer5.behaviors;

import lazer5.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class JihadSpawnSoldierBehavior extends Behavior {
	int numSoldiers = 0;

	public JihadSpawnSoldierBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() throws GameActionException {
		Direction myDirection = player.myRC.getDirection();
		MapLocation spawnLoc = player.myRC.getLocation().add(myDirection);
		if (player.myRC.senseGroundRobotAtLocation(spawnLoc) == null &&  //if we can spawn the soldier.
				player.myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND && numSoldiers < 4){
			player.myRC.spawn(RobotType.SOLDIER);
			numSoldiers++;
			return true;
		} else {//otherwise, find an available that we can spawn on. and rotate towards it				
				Direction newDir = player.myRC.getDirection();
				
				for(int i=0; i<7; i++) {
					newDir = newDir.rotateLeft();
					spawnLoc = player.myRC.getLocation().add(newDir);
					if (player.myRC.senseGroundRobotAtLocation(spawnLoc) == null &&  //if we can spawn the soldier.
							player.myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND && player.myRC.getRoundsUntilMovementIdle() == 0){
						player.myRC.setDirection(newDir);
						return false;
					}
				}
			
			return false;
		}
		

	}

}
