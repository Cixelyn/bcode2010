package lazer6.strategies;

import lazer6.RobotPlayer;

public class DefaultStrategy extends Strategy {

	public DefaultStrategy(RobotPlayer player) {
		super(player);
	}

	public boolean beginStrategy() {
		return true;
	}

	public void runBehaviors() {
		//Nothing
	}
	
	public void runInstincts() {
		//Nothing		
	}

}
