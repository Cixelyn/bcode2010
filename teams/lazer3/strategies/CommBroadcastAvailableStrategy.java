package lazer3.strategies;

import lazer3.RobotPlayer;
import lazer3.behaviors.Behavior;
import lazer3.behaviors.BuildTowerLatticeBehavior;
import battlecode.common.GameActionException;

public class CommBroadcastAvailableStrategy extends Strategy {
	private Behavior bcast;

	public CommBroadcastAvailableStrategy(RobotPlayer player) {
		super(player);
		bcast = new BuildTowerLatticeBehavior(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		bcast.execute();

	}

	@Override
	public void runInstincts() throws GameActionException {

	}

}
