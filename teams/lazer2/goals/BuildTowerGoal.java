package lazer2.goals;

import java.util.ArrayList;

import lazer2.BasePlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class BuildTowerGoal extends Goal {
	public BuildTowerGoal(BasePlayer player) {
		super(player);
		initFilters();
	}
	
	
	public boolean takeControl(){
		if(myRC.getFlux() >= 3000) return true;
		return false;
	}

	public int execute() {
		MapLocation myLoc = player.myRC.getLocation();
		Direction dir = player.myRC.getDirection();
		MapLocation dest = myLoc.add(dir);
		try {
			if((player.myRC.senseGroundRobotAtLocation(dest) == null) && player.myRC.senseTerrainTile(dest).getType()==TerrainType.LAND)
				if(player.myRC.getFlux()>=3000)
					player.myRC.spawn(RobotType.COMM);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return GOAL_SUCCESS;
	}
	public void initFilters(){
		
	}

}
