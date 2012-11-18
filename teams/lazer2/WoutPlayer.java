package lazer2;

import lazer2.goals.BuildTowerGoal;
import lazer2.goals.FollowArchonGoal;
import lazer2.goals.YieldGoal;
import lazer2.instincts.TransferFluxInstinct;
import lazer2.instincts.TransferInstinct;
import battlecode.common.RobotController;

public class WoutPlayer extends BasePlayer {

	public WoutPlayer(RobotController rc) {
		super(rc);
	}
	
	public void initInstincts() { 
		myInstincts.addInstinct(new TransferInstinct(this));
		myInstincts.addInstinct(new TransferFluxInstinct(this));
	}
	public void initGoals() {
		myGoals.addGoal(new YieldGoal(this));
		myGoals.addGoal(new BuildTowerGoal(this));
		myGoals.addGoal(new FollowArchonGoal(this));
	}

}
