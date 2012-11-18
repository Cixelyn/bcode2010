package cory1;

import cory1.ArchonPlayer;
import cory1.BasePlayer;
import cory1.WoutPlayer;
import battlecode.common.*;

public class RobotPlayer implements Runnable {
	
	private final BasePlayer player;

	public RobotPlayer(RobotController rc)
	{
		if(rc.getRobotType()==RobotType.ARCHON) this.player = new ArchonPlayer(rc);
		else{
			this.player = new WoutPlayer(rc);
		}
		
	}
	
	public void run(){
		this.player.run();
	}
}