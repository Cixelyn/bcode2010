package lazer3.behaviors;

import lazer3.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class BuildCommBehavior extends Behavior {

	public BuildCommBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean runActions() throws GameActionException {
		// TODO Auto-generated method stub
		if(player.myRC.getFlux()>3000){
			if(tileOpen(player.myRC.getLocation().add(player.myRC.getDirection()))){
				player.myRC.spawn(RobotType.COMM);
				return true;
			}
		}
		return false;
	}

	/**
	 * returns true if we can spawn something in the ground in front of myRobot
	 * 
	 * @param loc - location of robot
	 * @param dir - direction of robot
	 * @return true if no robot in front of myRobot and tile in front is a land
	 * @throws GameActionException
	 */
	public boolean tileOpen(MapLocation dest) throws GameActionException{
		//if no ground robot in front and tile in front is a land, return true
		if(player.myRC.senseGroundRobotAtLocation(dest)==null &&
				player.myRC.senseTerrainTile(dest).getType()==TerrainType.LAND){
			return true;
		}
		return false;
	}
}
