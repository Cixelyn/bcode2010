package lazer6.strategies;

import lazer6.RobotPlayer;

public class CommStrategy extends TowerStrategy {

	public CommStrategy(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		this.runBroadcast();

	}

	@Override
	public void runInstincts() {

	}

}
