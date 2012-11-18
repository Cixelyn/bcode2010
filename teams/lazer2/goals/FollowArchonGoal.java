package lazer2.goals;

import lazer2.BasePlayer;
import lazer2.filters.Filter;
import lazer2.filters.FilterFactory;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class FollowArchonGoal extends Goal {
	public Filter alliedArchons;
	
	public FollowArchonGoal(BasePlayer player) {
		super(player);
		initFilters();
	}
	public boolean takeControl() {
		if(player.myRC.getRoundsUntilMovementIdle()>0) return false;
		if (player.myRC.getEnergonLevel() >= 10) return false;
		return true;
	}
	public int execute() {
		try {
			// GHETTO METHOD...REPLACE W/ FILTER LATER
			MapLocation current = player.myRC.getLocation();
			MapLocation closest = myIntel.getNearestArchon();
			Direction archonDirection = current.directionTo(closest);
			if (current.directionTo(closest) == Direction.OMNI) {
				Direction currentDirection = player.myRC.getDirection();
				player.myRC.setDirection(currentDirection.opposite());
				return GOAL_SUCCESS;
			}
			player.myRC.setDirection(archonDirection);
			player.myRC.yield();
			if (player.myRC.canMove(archonDirection)) {
				player.myRC.moveForward();
			} else {
				player.myRC.yield();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GOAL_SUCCESS;
	}
	
	public void initFilters(){
		alliedArchons = FilterFactory.typeTeamFilter(player.myRC, player.myIntel, RobotType.ARCHON, player.myTeam); 
	}
	
}
