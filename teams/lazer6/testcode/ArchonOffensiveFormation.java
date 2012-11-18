package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.instincts.Instinct;
import lazer6.strategies.Strategy;

public class ArchonOffensiveFormation extends Strategy {
	
	private Instinct Transfer;

	public ArchonOffensiveFormation(RobotPlayer player) {
		super(player);
		myProfiler.setScanMode(true, true, false, true);
	}

	@Override
	public boolean beginStrategy() {
		return false;
	}

	@Override
	public void runBehaviors() {
		
		//Assume that we've already engaged in battle.
		
	
		
		
		
		
	}

	@Override
	public void runInstincts() {
		Transfer.execute();		
	}

}
