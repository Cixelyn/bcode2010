package lazer2;

import lazer2.goals.*;
import lazer2.instincts.*;
import battlecode.common.RobotController;

public class ArchonPlayer extends BasePlayer {

	public ArchonPlayer(RobotController rc) {
		super(rc);
	}
	
	public void initInstincts() { 
		myInstincts.addInstinct(new TransferInstinct(this));
	}
	
	public void initGoals() {
		myGoals.addGoal(new MoveAwayFromWalls(this));
		myGoals.addGoal(new YieldGoal(this));
		//myGoals.addGoal(new SpawnSoldierGoal(this));
		//myGoals.addGoal(new SpawnWoutGoal(this));
		//myGoals.addGoal(new WanderGoal(this));
	}
	

}
