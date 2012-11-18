package lazerguns1.behaviors;

import lazerguns1.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
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
		MapLocation target = player.myRC.getLocation().add(player.myRC.getDirection());
		if (hasAdjacentTower(target)) return false;
		if(player.myRC.getFlux()>3000){
			if(tileOpen(target)){
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
	
	public boolean hasAdjacentTower(MapLocation loc) throws GameActionException{
		Direction dir;
		for(int i=0; i<8; i++){
			dir = Direction.values()[i];
			Robot rob = player.myRC.senseGroundRobotAtLocation(loc.add(dir));
			if ((rob != null) && (player.myRC.canSenseObject(rob))) {
				if (player.myRC.senseRobotInfo(rob).type == RobotType.COMM) {
					return true;
				}
			}
		}
		return false;
	}
}
