package sajith1;

import battlecode.common.*;

public class RobotPlayer implements Runnable {

    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        //System.out.println("STARTING");
    	while(true) {
            try {
            	int x = 0;
            	Robot[] nearbyAllies = myRC.senseNearbyGroundRobots();
            	for (Robot r:nearbyAllies) {
            		if (myRC.senseRobotInfo(r).type == RobotType.SOLDIER) {
            			x++;
            		}
            	}
            	if (myRC.getRobotType() == RobotType.ARCHON && myRC.getEnergonLevel() > 35) {
            		myRC.spawn(RobotType.SOLDIER);
            	} else {
            		if (myRC.getRobotType() == RobotType.ARCHON){
            			for (Robot r:nearbyAllies) {
            				if (myRC.senseRobotInfo(r).type == RobotType.SOLDIER && myRC.senseRobotInfo(r).energonLevel < 20) {
            					MapLocation loc = myRC.senseRobotInfo(r).location;
            					if (loc.isAdjacentTo(myRC.getLocation())) {
            						myRC.transferUnitEnergon(5.0,loc, RobotLevel.ON_GROUND);
            						
            					}
            				}
            			}
            			myRC.yield();
            		}
            	}
            	if (myRC.getRobotType() == RobotType.SOLDIER){
            		if (myRC.canMove(myRC.getDirection())) {
            			myRC.moveForward();
            		} else {
            			Robot[] nearbyAir = myRC.senseNearbyAirRobots();
            			for (Robot r:nearbyAir) {
            				if (myRC.senseRobotInfo(r).team == myRC.getTeam().opponent()) {
            					MapLocation loc = myRC.senseRobotInfo(r).location;
            					if (loc.isAdjacentTo(myRC.getLocation())) {
            						myRC.attackAir(loc);
            					}
            				}
            			}
            		}
        			myRC.yield();
            	}
            } catch (Exception e) {
                System.out.println("caught exception:");
            }
    	}
    }
}
