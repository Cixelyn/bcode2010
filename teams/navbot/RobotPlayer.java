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
				if (!(this.myRC.isMovementActive() || this.myRC.isAttackActive() || this.myRC.hasActionSet())){
					myNavi.bugInDirection(myRC.getLocation().directionTo(myRC.senseAlliedArchons()[0]));
				} else myRC.yield();

			} catch(GameActionException e) {
				e.printStackTrace();
			}

		}
	}
}
