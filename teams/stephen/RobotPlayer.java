package stephen;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer implements Runnable{
	public final BasePlayer player;
	
	public RobotPlayer(RobotController rc){
		if(rc.getRobotType()==RobotType.ARCHON) this.player = new ArchonPlayer(rc);
		else{
			this.player = new WoutPlayer(rc);
		}
		
	}
	
	public void run(){
		this.player.run();
	}
}
