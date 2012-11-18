package kevin1;

import kevin1.ArchonPlayer;
import kevin1.WoutPlayer;
import kevin1.SoldierPlayer;
import kevin1.ChainerPlayer;
import battlecode.common.*;

public class RobotPlayer implements Runnable {

    private final BasePlayer player;
    public RobotPlayer(RobotController rc)
	{
		if(rc.getRobotType()==RobotType.ARCHON) this.player = new ArchonPlayer(rc);
		else if (rc.getRobotType()==RobotType.SOLDIER) this.player = new SoldierPlayer(rc);
		else if (rc.getRobotType()==RobotType.CHAINER) this.player = new ChainerPlayer(rc);
		else {
			this.player = new WoutPlayer(rc);
		}
		
	}
	
	public void run(){
		this.player.run();
	}
}
