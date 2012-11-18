package lazerguns1.behaviors;

import lazerguns1.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class SpawnSoldierBehavior extends Behavior {

	public SpawnSoldierBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean runActions() throws GameActionException {
		Direction myDirection = player.myRC.getDirection();
		MapLocation spawnLoc = player.myRC.getLocation().add(myDirection);
		if (player.myRC.senseGroundRobotAtLocation(spawnLoc) == null &&
				player.myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND){
			player.myRC.spawn(RobotType.SOLDIER);
			return true;
		}
		return false;
	}

}
