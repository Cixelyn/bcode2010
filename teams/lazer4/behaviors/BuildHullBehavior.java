package lazer4.behaviors;

import java.util.ArrayList;

import lazer4.RobotPlayer;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class BuildHullBehavior extends Behavior {
	private final RobotController myRC;
	private final ArrayList<MapLocation> towers = new ArrayList<MapLocation>(0);
	private final ArrayList<MapLocation> closeTowers = new ArrayList<MapLocation>(0);
	
	private final int MAX_SQ_DIST = 25;
	private final int MIN_SQ_DIST = 4;
	
	public BuildHullBehavior(RobotPlayer player) {
		super(player);
		myRC = player.myRC;
	}

	@Override
	public boolean runActions() throws GameActionException {
		MapLocation myLoc = myRC.getLocation();
		Direction myDir = myRC.getDirection();
		double myFlux = myRC.getFlux();
		
		MapLocation targetLoc = myLoc.add(myDir);
		
		if(myFlux>3000){
			//if no towers yet, build comm tower immediately
			if(towers.size()==0){
				if(tileOpen(targetLoc)){
					myRC.spawn(RobotType.COMM);
					towers.add(targetLoc);
					return true;
				}
			}
			//if there is exactly 1 tower built, build within 5 units of other tower, but
			//not closer than 3 units to tower
			else if(towers.size()==1){
				MapLocation towerLoc = towers.get(0);
				if(tileOpen(targetLoc)){
					int dist = towerLoc.distanceSquaredTo(targetLoc);
					if(dist <= MAX_SQ_DIST && dist>MIN_SQ_DIST){
						myRC.spawn(RobotType.COMM);
						towers.add(targetLoc);
						return true;
					}
				}
			}
			//if there are exactly 2 towers built, build another tower when there are exactly 2 towers
			//within 5 units, but no closer than 3 units to either of them.
			else if(towers.size()==2){
				if (tileOpen(targetLoc)) {
					int dist1 = towers.get(0).distanceSquaredTo(targetLoc);
					int dist2 = towers.get(1).distanceSquaredTo(targetLoc);
					if (dist1 <= MAX_SQ_DIST && dist2 <= MAX_SQ_DIST) {
						if (dist1 > MIN_SQ_DIST && dist2 > MIN_SQ_DIST) {
							myRC.spawn(RobotType.COMM);
							towers.add(targetLoc);
							return true;
						}
					}
				}
			}
			//if 3 or more towers, build within 5 units of exactly 2 other towers (make a triforce)
			else{
				closeTowers.clear();
				//if within 5 units of another tower, add towerLocation to closeTowers array
				for(MapLocation t: towers){
					int dist = t.distanceSquaredTo(targetLoc);
					if(dist<=MAX_SQ_DIST) closeTowers.add(t);
				}
				//if there are exactly 2 towers that are within 5 units, spawn a tower
				if(closeTowers.size()==2){
					myRC.spawn(RobotType.COMM);
					towers.add(targetLoc);
					return true;
				}else{
					//move towards direction that we can most likely spawn another tower
					closeTowers.clear();
					return false;
				}
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
		if(myRC.senseGroundRobotAtLocation(dest)==null &&
				myRC.senseTerrainTile(dest).getType()==TerrainType.LAND){
			return true;
		}
		return false;
	}

}
