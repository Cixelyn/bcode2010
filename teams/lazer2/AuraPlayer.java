package lazer2;

import java.util.Random;

import lazer2.goals.YieldGoal;
import battlecode.common.AuraType;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class AuraPlayer extends BasePlayer {
	public AuraPlayer(RobotController rc){
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
