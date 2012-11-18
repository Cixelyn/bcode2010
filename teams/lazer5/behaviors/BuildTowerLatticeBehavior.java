package lazer5.behaviors;

import lazer5.RobotPlayer;
import lazer5.communications.MsgType;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class BuildTowerLatticeBehavior extends Behavior {
	private final MapLocation myLoc;
	private final int xLoc;
	private final int yLoc;

	private MapLocation target1;	//north
	private MapLocation target2;	//east
	private MapLocation target3;	//south
	private MapLocation target4;	//west
	
	private int distance = 5;
	
	
	public BuildTowerLatticeBehavior(RobotPlayer player) {
		super(player);
		myLoc = player.myRC.getLocation();
		xLoc = myLoc.getX();
		yLoc = myLoc.getY();
		
	}

	@Override
	public boolean runActions() throws GameActionException {
//		if ((Clock.getRoundNum()+player.myRC.getRobot().getID())%20==0) {
			for(int i=distance; i>3; i--){
				target1 = new MapLocation(xLoc, yLoc-distance);
				target2 = new MapLocation(xLoc+distance, yLoc);
				target3 = new MapLocation(xLoc, yLoc+distance);
				target4 = new MapLocation(xLoc-distance, yLoc);
				
				
				if (canSpawnRobot(target1)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target1);
					player.myRC.setIndicatorString(2, target1.toString());
					return true;
				} else if (canSpawnRobot(target2)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target2);
					player.myRC.setIndicatorString(2, target2.toString());
					return true;
				} else if (canSpawnRobot(target3)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target3);
					player.myRC.setIndicatorString(2, target3.toString());
					return true;
				} else if (canSpawnRobot(target4)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target4);
					player.myRC.setIndicatorString(2, target4.toString());
					return true;
				}
				
				//if target locations are invalid, make sure they're not invalid because towers are there
				//we don't want to decrease radius if this is the case because then we'd be creating unnecessary
				//redundancy.
				if(locationHasTower(target1) || hasAdjacentTower(target1)
						|| locTooCloseToOtherTowers(target1))return false;
				else if(locationHasTower(target2) || hasAdjacentTower(target2)
						|| locTooCloseToOtherTowers(target2)) return false;
				else if(locationHasTower(target3) || hasAdjacentTower(target3)
						|| locTooCloseToOtherTowers(target3)) return false;
				else if(locationHasTower(target4) || hasAdjacentTower(target4)
						|| locTooCloseToOtherTowers(target4)) return false;
			}
//		}
		return false;
	}
	
	private boolean locationHasTower(MapLocation loc) throws GameActionException{
		Robot rob = player.myRC.senseGroundRobotAtLocation(loc);
		if(rob!=null){
			if(player.myRC.senseRobotInfo(rob).type == RobotType.COMM) return true;
		}
		return false;
	}
	
	private boolean canSpawnRobot(MapLocation target) throws GameActionException{
		if(player.myRC.senseGroundRobotAtLocation(target)==null && 
				player.myRC.senseTerrainTile(target).getType() == TerrainType.LAND){
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
		
		
		nearbyTiles[16] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.NORTH_WEST);
		nearbyTiles[17] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.NORTH);
		nearbyTiles[18] = center.add(Direction.NORTH_WEST).add(Direction.NORTH).add(Direction.NORTH);
		nearbyTiles[19] = center.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH);
		nearbyTiles[20] = center.add(Direction.NORTH_EAST).add(Direction.NORTH).add(Direction.NORTH);
		nearbyTiles[21] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.NORTH);
		nearbyTiles[22] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.NORTH_EAST);
		
		nearbyTiles[23] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.EAST);
		nearbyTiles[24] = center.add(Direction.NORTH_EAST).add(Direction.EAST).add(Direction.EAST);
		nearbyTiles[25] = center.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST);
		nearbyTiles[26] = center.add(Direction.SOUTH_EAST).add(Direction.EAST).add(Direction.EAST);
		nearbyTiles[27] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.EAST);
		nearbyTiles[28] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST);
		
		nearbyTiles[29] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.SOUTH);
		nearbyTiles[30] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH).add(Direction.SOUTH);
		nearbyTiles[31] = center.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH);
		nearbyTiles[32] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH).add(Direction.SOUTH);
		nearbyTiles[33] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.SOUTH);
		nearbyTiles[34] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST);
		
		nearbyTiles[35] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.WEST);
		nearbyTiles[36] = center.add(Direction.SOUTH_WEST).add(Direction.WEST).add(Direction.WEST);
		nearbyTiles[37] = center.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST);
		nearbyTiles[38] = center.add(Direction.NORTH_WEST).add(Direction.WEST).add(Direction.WEST);
		nearbyTiles[39] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.WEST);
		
		for(int i=0; i<40; i++){
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
