package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.strategies.Strategy;
import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class TestNav extends Strategy {

	public TestNav(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() {
		player.myProfiler.switchSwarmMode(1);
		return true;
	}

	@Override
	public void runBehaviors() {
		
		//Robots will move towards nearest archon
		MapLocation arcLoc = player.myRC.senseAlliedArchons()[0];
		//Direction toTurn = player.myNavi.bugTo(player.myProfiler.calculateSwarmUnitLocation((arcLoc)));
		Direction toTurn = player.myNavi.bugTo(arcLoc);
		//player.myRC.setIndicatorString(2, "Swarm: "+toTurn);
			
		
		player.myAct.backUpInDir(toTurn);
	}

	@Override
	public void runInstincts() {
	}
	

}
