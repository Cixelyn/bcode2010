package lazer4.strategies;

import lazer4.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class SwarmTest extends Strategy {	
		
	public SwarmTest(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		player.myNavi.SwarmTo(new MapLocation(99999,0));
	}
	
	public void runInstincts() throws GameActionException {
		//Do nothing
	}
	

}
