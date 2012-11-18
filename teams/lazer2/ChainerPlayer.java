package lazer2;

import lazer2.goals.YieldGoal;
import battlecode.common.RobotController;

public class ChainerPlayer extends BasePlayer {

	public ChainerPlayer(RobotController rc) {
		super(rc);
		// TODO Auto-generated constructor stub
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
