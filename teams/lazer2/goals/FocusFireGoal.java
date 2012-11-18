package lazer2.goals;

import lazer2.BasePlayer;
import lazer2.Intelligence;
import lazer2.filters.Filter;
import lazer2.filters.FilterFactory;
import lazer2.filters.Matcher;
import lazer2.filters.TeamMatcher;
import lazer2.Broadcaster;
import lazer2.MsgType;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class FocusFireGoal extends Goal {
	public FocusFireGoal(BasePlayer player) {
		super(player);
		
		initFilters();
	}

	Filter enemyArchons;
	
	public boolean takeControl() {
		// check for nearby enemies and allies
		Matcher matcherteam = new TeamMatcher(myIntel, player.myRC.getTeam().opponent());
		Filter isOpponent = new Filter(matcherteam, myRC);
		Filter isAlly = new Filter(matcherteam, myRC);
		Robot[] nearby = myIntel.getNearbyRobots();
		if ((Clock.getRoundNum()+player.myRC.getRobot().getID()) % 10 != 0)
			return false;
		if (isAlly.count(nearby) >= 1 && isOpponent.count(nearby) >= 1){
			return true;
		}else{
			return false;
		}	
	}
	public int execute() throws GameActionException {
		Robot[] nearby = myIntel.getNearbyRobots();
		MapLocation toAttack = player.myRC.senseRobotInfo(enemyArchons.weakest(nearby)).location;
		player.myRadio.sendSingleDestination(MsgType.MSG_ENEMYARCHON, toAttack);
		return GOAL_SUCCESS;
	}
	
	public void initFilters(){
		enemyArchons = FilterFactory.typeTeamFilter(player.myRC, player.myIntel, RobotType.ARCHON, player.myTeam.opponent());
	}
}
