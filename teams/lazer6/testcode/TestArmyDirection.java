package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.strategies.Strategy;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;


/**
 * This class test the archon moving 
 * @author Cory
 *
 */
public class TestArmyDirection extends Strategy{

	public TestArmyDirection(RobotPlayer player) {
		super(player);
	}

	public boolean beginStrategy() {
		myProfiler.setScanMode(false,false,false,true);  //Only scan archons
		return true;
	}
	
	
	public void runBehaviors() {
		
		
		myRC.setIndicatorString(2, ""+myProfiler.archonDifferential);
		myAct.moveInDir(myProfiler.mobDirection());
		
	}
	

	public void runInstincts() {
	}
	
	
	
	
	
	
	
	
	

}
