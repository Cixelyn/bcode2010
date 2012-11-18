package lazer2.goals;

import java.util.Set;

import lazer2.BasePlayer;
import lazer2.filters.Filter;
import lazer2.filters.FilterFactory;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class SpawnSoldierGoal extends Goal {
	Filter nearbySoldiers;
	public SpawnSoldierGoal(BasePlayer player) {
		super(player);
		initFilters();
	}
	
	boolean wallpriority;
	
	public boolean takeControl() {
		Direction myDirection = player.myRC.getDirection();
		MapLocation spawnLoc = player.myRC.getLocation().add(myDirection);
		
		if (player.myRC.getRobotType() != RobotType.ARCHON || player.myRC.getEnergonLevel() < 40) return false;
		Set<Robot> nearby = nearbySoldiers.filter(myIntel.getNearbyGroundRobots());
		if(nearby.size()>1) return false;
		try {
			if (player.myRC.senseGroundRobotAtLocation(spawnLoc) == null &&
					player.myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND)return true;
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	public int execute() {
		try {
			Direction myDirection = player.myRC.getDirection();
			MapLocation spawnLoc = player.myRC.getLocation().add(myDirection);
			if (player.myRC.senseGroundRobotAtLocation(spawnLoc) == null &&
					player.myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND){
				player.myRC.spawn(RobotType.SOLDIER);
				player.myRC.yield();
				player.myRC.transferUnitEnergon(GameConstants.ENERGON_RESERVE_SIZE, spawnLoc, RobotLevel.ON_GROUND);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GOAL_SUCCESS;
	}
	
	public void initFilters(){
		nearbySoldiers = FilterFactory.typeTeamRangeFilter(myRC, myIntel, RobotType.SOLDIER, 
				player.myTeam,RobotType.ARCHON.sensorRadius());
	}
}
