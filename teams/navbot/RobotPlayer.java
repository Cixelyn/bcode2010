package navbot;

import battlecode.common.*;
import navbot.Navigation;

public class RobotPlayer implements Runnable {
	public final RobotController myRC;
	public final Navigation myNavi;
	public RobotPlayer(RobotController rc){
		this.myRC = rc;
		myNavi = new Navigation(myRC);
	}
	public void run() {
		while (true) {
			try{
				if (myRC.getRobotType()==RobotType.WOUT) {
					if (!(this.myRC.isMovementActive() || this.myRC.isAttackActive() || this.myRC.hasActionSet())){
					myNavi.bugTo(myRC.getLocation().directionTo(myRC.senseAlliedArchons()[0]));
					} else myRC.yield();
				}
				

			} catch(GameActionException e) {
				e.printStackTrace();
			}

		}
	}
}
