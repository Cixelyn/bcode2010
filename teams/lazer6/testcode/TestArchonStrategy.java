package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.behaviors.ArchonRetreatBehavior;
import lazer6.behaviors.Behavior;
import lazer6.strategies.Strategy;

public class TestArchonStrategy extends Strategy {
	
	private Behavior Retreat;
	
	public TestArchonStrategy(RobotPlayer player) {
		super(player);
		Retreat = new ArchonRetreatBehavior(player);
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		Retreat.execute();
	}

	@Override
	public void runInstincts() {

	}

}
