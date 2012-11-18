package lazer3.strategies;

import battlecode.common.GameActionException;
import lazer3.RobotPlayer;

public class DebugPrintStrategy extends Strategy {
	String debugString;
	
	public DebugPrintStrategy(RobotPlayer player, String s) {
		super(player);
		debugString = s;
		
	}

	public DebugPrintStrategy(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		System.out.println(debugString);
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		//Do nothing
	}

	@Override
	public void runInstincts() throws GameActionException {
		//Do nothing
	}
	

}
