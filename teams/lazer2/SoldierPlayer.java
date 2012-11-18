package lazer2;

import battlecode.common.RobotController;
import lazer2.goals.*;
import lazer2.instincts.*;

public class SoldierPlayer extends BasePlayer {
	
	public SoldierPlayer(RobotController rc) {
		super(rc);
	}

	protected void initGoals() {
		myGoals.addGoal(new YieldGoal(this));
		myGoals.addGoal(new FocusFireGoal(this));
		myGoals.addGoal(new AttackGoal(this));
		myGoals.addGoal(new FollowArchonGoal(this));
		myGoals.addGoal(new WanderGoal(this));
	}

	
	protected void initInstincts() {
		myInstincts.addInstinct(new TransferInstinct(this));
	}
	

}
