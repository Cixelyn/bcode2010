package lazer2.goals;

import java.util.Random;

import lazer2.BasePlayer;
import lazer2.filters.Filter;
import lazer2.filters.Matcher;
import lazer2.filters.TeamMatcher;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotType;

public class WanderGoal extends Goal {
	Random rand;
	public WanderGoal(BasePlayer player) {
		super(player);
		rand = new Random(0);
		initFilters();
	}
	public boolean takeControl(){
		if(player.myRC.isMovementActive()) return false;
		if (player.myRC.getRobotType() == RobotType.SOLDIER && player.myRC.getEnergonLevel() < 10)return false;
		if(player.myRC.getRoundsUntilMovementIdle()>0) return false;

		Matcher matcherteam = new TeamMatcher(myIntel, player.myRC.getTeam().opponent());
		Filter isOpponent = new Filter(matcherteam, myRC);
		Robot[] nearby = myIntel.getNearbyRobots();
		Robot closest = isOpponent.closest(nearby);
		if (closest != null)return false;
		return true;
	}
	public int execute() {
		try {
			//Random movement
			int move = rand.nextInt(3);
			if(move == 0){
				int choice = rand.nextInt(8);
				if(choice==0){
					player.myRC.setDirection(Direction.NORTH);
					player.myRC.setIndicatorString(0, "Dir: N");
				}else if (choice==1) {
					player.myRC.setDirection(Direction.NORTH_EAST);
					player.myRC.setIndicatorString(0, "Dir: NE");
				}else if (choice==2) {
					player.myRC.setDirection(Direction.EAST);
					player.myRC.setIndicatorString(0, "Dir: E");
				}else if (choice==3) {
					player.myRC.setDirection(Direction.SOUTH_EAST);
					player.myRC.setIndicatorString(0, "Dir: SE");
				}else if (choice==4) {
					player.myRC.setDirection(Direction.SOUTH);
					player.myRC.setIndicatorString(0, "Dir: S");
				}else if (choice==5) {
					player.myRC.setDirection(Direction.SOUTH_WEST);
					player.myRC.setIndicatorString(0, "Dir: SW");
				}else if (choice==6) {
					player.myRC.setDirection(Direction.WEST);
					player.myRC.setIndicatorString(0, "Dir: W");
				}else if (choice==7) {
					player.myRC.setDirection(Direction.NORTH_WEST);
					player.myRC.setIndicatorString(0, "Dir: NW");
				}
			}
			else{
				if(player.myRC.canMove(player.myRC.getDirection())){
					player.myRC.setIndicatorString(1, Boolean.toString(player.myRC.hasActionSet()));
					player.myRC.moveForward();
					player.myRC.setIndicatorString(0, "MovF");
				}else {
					player.myRC.setDirection(player.myRC.getDirection().rotateRight());
				}
			}
			player.myRC.yield();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return GOAL_SUCCESS;	
	}
	
	public void initFilters(){
		
	}
}
