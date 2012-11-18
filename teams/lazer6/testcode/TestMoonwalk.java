package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.strategies.Strategy;
import battlecode.common.Direction;

public class TestMoonwalk extends Strategy {

	public TestMoonwalk(RobotPlayer player) {
		super(player);
		player.myProfiler.setScanMode(true, true, true, true);

	}

	@Override
	public boolean beginStrategy() {
		
		return true;
	}

	@Override
	public void runBehaviors() {
		int id = myProfiler.myArchonID;
		myRC.setIndicatorString(2, ""+id);
		
		if(id==3) {//point to 0, and move towards 5
			
			Direction faceDir = myRC.getLocation().directionTo(myProfiler.alliedArchons[0]);
			Direction moveDir = myRC.getLocation().directionTo(myProfiler.alliedArchons[5]);
			
			myAct.moveLikeMJ(faceDir, moveDir);
			
		}
		
		
		
		

		
	}

	@Override
	public void runInstincts() {

		
	}

}
