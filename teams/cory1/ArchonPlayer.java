package cory1;

import battlecode.common.*;

import java.util.Random;

public class ArchonPlayer extends BasePlayer {

	Random rand;
	
	public ArchonPlayer(RobotController r) {
		super(r);
		rand = new Random(0);
	}
	
	
	@Override
	public void run() {
		while(true) {
			
			try {
				
				while (rc.isMovementActive()) {
                    rc.yield();
                }
				
							
					
				int choice = rand.nextInt(4);
				if(choice==0){
					rc.setDirection(rc.getDirection().rotateLeft());
					rc.setIndicatorString(0, "RotL");
				}else if (choice==1) {
					rc.setDirection(rc.getDirection().rotateRight());
					rc.setIndicatorString(0, "RotR");
				}else if (choice==2) {
					if(rc.canMove(rc.getDirection())){
						rc.setIndicatorString(1, Boolean.toString(rc.hasActionSet()));
						rc.moveForward();
						rc.setIndicatorString(0, "MovF");
					}else {
						rc.moveBackward();
					}
				}else if(choice==3) {
					if(rc.getEnergonLevel()>60.0) {
						rc.spawn(RobotType.WOUT);
						rc.setIndicatorString(0, "Spawn");
					}
				}
				
				rc.yield();
				
				//Transfer Energon to Allies
				Robot[] nearbyAllies = rc.senseNearbyGroundRobots();
				
				for (Robot r:nearbyAllies) {
    				if (rc.senseRobotInfo(r).type == RobotType.WOUT && rc.senseRobotInfo(r).energonLevel < 20) {
    					MapLocation loc = rc.senseRobotInfo(r).location;
    					if (loc.isAdjacentTo(rc.getLocation())) {
    						rc.transferUnitEnergon(10, loc, RobotLevel.ON_GROUND);
    						
    					}
    				}
    			}
                
				rc.yield();

				
			}
			catch(Exception e) {
				//Throw Exception
			}
			
		}
		
		
	}

}
