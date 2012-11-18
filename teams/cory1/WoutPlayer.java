package cory1;

import battlecode.common.*;

public class WoutPlayer extends BasePlayer {
	
	private Sense s;
	
	public WoutPlayer(RobotController r) {
		super(r);
		s = new Sense(r);
		
	}
	

	@Override
	public void run() {
		while(true) {
			
			try {

				
				//Yield
				while (rc.isMovementActive()) {
                    rc.yield();
                }
				
				
				
				//Attacking code goes here
				Robot[] nearbyAir = rc.senseNearbyAirRobots();
    			for (Robot r:nearbyAir) {
    				if (rc.senseRobotInfo(r).team == rc.getTeam().opponent()) {
    					MapLocation loc = rc.senseRobotInfo(r).location;
    					if (loc.isAdjacentTo(rc.getLocation())) {
    						rc.attackAir(loc);
    					}
    				}
    			}
				
				
				
				
				//Move towards nearest archon
				Direction d = rc.getLocation().directionTo(s.nearestArchon());
				
				if(d==rc.getDirection()) {
					rc.moveForward();
				} else {
					rc.setDirection(d);
				}
						
				
				rc.yield();
			}
			catch(GameActionException e) {
				//Exception here too lol
			}
			
		}

	}

}
