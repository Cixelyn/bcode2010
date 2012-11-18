package cory1;

import battlecode.common.*;

public abstract class BasePlayer implements Runnable{
	protected final RobotController rc;

	
	//Constructor
	public BasePlayer(RobotController r) {
		this.rc = r;		
	}
	
	

}
