package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.strategies.Strategy;
import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;

public class TestSighting extends Strategy{

	public TestSighting(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() {
		myProfiler.setScanMode(true, true, true, true);
		return true;
	}

	@Override
	public void runBehaviors() {
		int size = player.myProfiler.nearbyGroundRobots.length;
		RobotInfo[] rdata = new RobotInfo[size+3];
		
		for(int i=0; i<size; i++) {
			try {
				rdata[i] = myRC.senseRobotInfo(myProfiler.nearbyGroundRobots[i]);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		
		
		myRadio.sendRobotList(rdata);
		
		myRC.setIndicatorString(2, player.myDB.toString());
		
		
		
		
		
	}

	@Override
	public void runInstincts() {		
	}

}
