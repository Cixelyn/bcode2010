package lazer4.behaviors;

import lazer4.RobotPlayer;
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
		if (hasAdjacentTower(target) || locTooCloseToOtherTowers(target)) return false;
		if(player.myRC.getFlux()>3000){
			if(tileOpen(target)){
				player.myRC.spawn(RobotType.COMM);
				return true;
			}else {//otherwise, find an available that we can spawn on. and rotate towards it				
				Direction newDir = player.myRC.getDirection();
				
				for(int i=0; i<7; i++) {
					newDir = newDir.rotateLeft();
					target = player.myRC.getLocation().add(newDir);
					if (player.myRC.senseGroundRobotAtLocation(target) == null &&  //if we can spawn the soldier.
							player.myRC.senseTerrainTile(target).getType() == TerrainType.LAND){
						player.myRC.setDirection(newDir);
						return false;
					}
				}
			
			return false;
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
	
	public boolean locTooCloseToOtherTowers(MapLocation center) throws GameActionException{
		if(hasAdjacentTower(center)) return true;
		MapLocation[] nearbyTiles = new MapLocation[40];
		nearbyTiles[0] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST);
		nearbyTiles[1] = center.add(Direction.NORTH).add(Direction.NORTH_WEST);
		nearbyTiles[2] = center.add(Direction.NORTH).add(Direction.NORTH);
		nearbyTiles[3] = center.add(Direction.NORTH).add(Direction.NORTH_EAST);
		nearbyTiles[4] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST);
		nearbyTiles[5] = center.add(Direction.NORTH_EAST).add(Direction.EAST);
		nearbyTiles[6] = center.add(Direction.EAST).add(Direction.EAST);
		nearbyTiles[7] = center.add(Direction.EAST).add(Direction.SOUTH_EAST);
		nearbyTiles[8] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST);
		nearbyTiles[9] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH);
		nearbyTiles[10] = center.add(Direction.SOUTH).add(Direction.SOUTH);
		nearbyTiles[11] = center.add(Direction.SOUTH).add(Direction.SOUTH_WEST);
		nearbyTiles[12] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST);
		nearbyTiles[13] = center.add(Direction.SOUTH_WEST).add(Direction.WEST);
		nearbyTiles[14] = center.add(Direction.WEST).add(Direction.WEST);
		nearbyTiles[15] = center.add(Direction.WEST).add(Direction.NORTH_WEST);
		
		
//		nearbyTiles[16] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.NORTH_WEST);
//		nearbyTiles[17] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.NORTH);
//		nearbyTiles[18] = center.add(Direction.NORTH_WEST).add(Direction.NORTH).add(Direction.NORTH);
//		nearbyTiles[19] = center.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH);
//		nearbyTiles[20] = center.add(Direction.NORTH_EAST).add(Direction.NORTH).add(Direction.NORTH);
//		nearbyTiles[21] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.NORTH);
//		nearbyTiles[22] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.NORTH_EAST);
//		
//		nearbyTiles[23] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.EAST);
//		nearbyTiles[24] = center.add(Direction.NORTH_EAST).add(Direction.EAST).add(Direction.EAST);
//		nearbyTiles[25] = center.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST);
//		nearbyTiles[26] = center.add(Direction.SOUTH_EAST).add(Direction.EAST).add(Direction.EAST);
//		nearbyTiles[27] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.EAST);
//		nearbyTiles[28] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST);
//		
//		nearbyTiles[29] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.SOUTH);
//		nearbyTiles[30] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH).add(Direction.SOUTH);
//		nearbyTiles[31] = center.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH);
//		nearbyTiles[32] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH).add(Direction.SOUTH);
//		nearbyTiles[33] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.SOUTH);
//		nearbyTiles[34] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST);
//		
//		nearbyTiles[35] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.WEST);
//		nearbyTiles[36] = center.add(Direction.SOUTH_WEST).add(Direction.WEST).add(Direction.WEST);
//		nearbyTiles[37] = center.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST);
//		nearbyTiles[38] = center.add(Direction.NORTH_WEST).add(Direction.WEST).add(Direction.WEST);
//		nearbyTiles[39] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.WEST);
		
		for(int i=0; i<16; i++){
			Robot rob = player.myRC.senseGroundRobotAtLocation(nearbyTiles[i]);
			if ((rob != null) && (player.myRC.canSenseObject(rob))) {
				if (player.myRC.senseRobotInfo(rob).type == RobotType.COMM) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
}
