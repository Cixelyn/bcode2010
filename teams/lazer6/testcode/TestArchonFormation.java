package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.strategies.Strategy;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

public class TestArchonFormation extends Strategy{

	public TestArchonFormation(RobotPlayer player) {
		super(player);
	}

	public boolean beginStrategy() {
		myProfiler.setScanMode(false,false,false,true);  //Only scan archons
		return true;
	}

	public void runBehaviors() {
		myNavi.archonFormation();		
	}
	

	public void runInstincts() {
	}
	
	
	
	
	
	
	
	
	

}
