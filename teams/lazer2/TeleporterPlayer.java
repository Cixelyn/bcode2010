package lazer2;

import lazer2.goals.YieldGoal;
import battlecode.common.RobotController;

public class TeleporterPlayer extends BasePlayer {
	public TeleporterPlayer(RobotController rc){
		super(rc);
	}

	@Override
	protected void initGoals() {
		// TODO Auto-generated method stub
		myGoals.addGoal(new YieldGoal(this));

	}

	@Override
	protected void initInstincts() {
		// TODO Auto-generated method stub

	}

}
