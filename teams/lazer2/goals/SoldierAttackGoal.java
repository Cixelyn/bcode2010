package lazer2.goals;

import lazer2.BasePlayer;
import lazer2.Intelligence;
import lazer2.filters.Filter;
import lazer2.filters.Matcher;
import lazer2.filters.TeamMatcher;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SoldierAttackGoal extends Goal {
	public SoldierAttackGoal(BasePlayer player) {
		super(player);
		initFilters();
	}

	public boolean takeControl() {
		if(player.myRC.getRoundsUntilAttackIdle()>0)return false;
		Matcher matcherteam = new TeamMatcher(myIntel, player.myRC.getTeam().opponent());
		Filter isOpponent = new Filter(matcherteam, myRC);
		Robot[] nearby = myIntel.getNearbyRobots();
		Robot closest = isOpponent.closest(nearby);
		if (closest == null)return false;
		return true;
	}
	public int execute() {
		Matcher matcherteam = new TeamMatcher(myIntel, player.myRC.getTeam().opponent());
		Filter isOpponent = new Filter(matcherteam, myRC);
		Robot[] nearby = myIntel.getNearbyRobots();
		Robot closest = isOpponent.closest(nearby);
		try {
			if (closest != null) {
				RobotInfo rInfo = player.myRC.senseRobotInfo(closest);
				MapLocation enemyLoc = rInfo.location;
				if (rInfo.type == RobotType.ARCHON) {
					if (player.myRC.canAttackSquare(enemyLoc)) {
						player.myRC.attackAir(enemyLoc);
						player.myRC.yield();
					} else {
						if(player.myRC.getRoundsUntilMovementIdle()==0){
							Direction enemyDirection = player.myRC.getLocation().directionTo(enemyLoc);
							player.myRC.setDirection(enemyDirection);
							player.myRC.yield();
							if(player.myRC.canMove(enemyDirection)){
								player.myRC.moveForward();
								player.myRC.yield();
							}
						}
					}
				}
				else
					if (player.myRC.canAttackSquare(enemyLoc)) {
						player.myRC.attackGround(enemyLoc);
						player.myRC.yield();
					} else {
						if(player.myRC.getRoundsUntilMovementIdle()==0){
							Direction enemyDirection = player.myRC.getLocation().directionTo(enemyLoc);
							player.myRC.setDirection(enemyDirection);
							player.myRC.yield();
							if(player.myRC.canMove(enemyDirection)){
								player.myRC.moveForward();
								player.myRC.yield();
							}
						}
					}
					
			} else {
				return GOAL_SUCCESS;
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return GOAL_SUCCESS;
	}
	
	public void initFilters(){
		
	}
}
