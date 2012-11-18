package lazer2;

import battlecode.common.RobotController;
import battlecode.common.RobotType;


/**
 * Main Entrypoint for the Battlecode VM
 * @author omg lazer pew pew
 *
 */
public class RobotPlayer implements Runnable{
	
	private final RobotController rc;

	public RobotPlayer(RobotController rc) {
		this.rc = rc;
	}
	
	public void run() {
		RobotType type = this.rc.getRobotType();
		BasePlayer player;
		
		if(type == RobotType.ARCHON) {
			player = new ArchonPlayer(this.rc);
		}else if(type == RobotType.WOUT) {
			player = new WoutPlayer(this.rc);
		}else if(type == RobotType.SOLDIER) {
			player = new SoldierPlayer(this.rc);
		}else if(type == RobotType.AURA) {
			player = new AuraPlayer(this.rc);
		}else if(type == RobotType.COMM) {
			player = new CommPlayer(this.rc);
		}else if(type == RobotType.TELEPORTER) {
			player = new TeleporterPlayer(this.rc);
		}else if(type == RobotType.CHAINER) {
			player = new ChainerPlayer(this.rc);
		}else if(type == RobotType.TURRET) {
			player = new TurretPlayer(this.rc);
		}else {
			player = null;
		}
			
		player.run();
	}
	

}
