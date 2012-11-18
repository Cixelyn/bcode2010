package stephen;

import battlecode.common.RobotController;

public abstract class BasePlayer implements Runnable{
	public final RobotController rc;
	public final Energy energy;
	
	
	
	public BasePlayer(RobotController r){
		this.rc = r;
		this.energy = new Energy(rc);
	}
	
}
