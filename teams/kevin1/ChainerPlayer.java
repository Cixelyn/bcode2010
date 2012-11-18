package kevin1;


import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotLevel;

import java.util.Random;

public class ChainerPlayer extends BasePlayer {
	Random rand;
	public ChainerPlayer(RobotController r) {
		super(r);
		rand = new Random(0);
	}

	@Override
	public void run() {
		while (true){
			try{
				while (rc.hasActionSet()) {
                    System.out.println("yielding due to action set");
					rc.yield();
                }
                //retreat to archon if energon low
                if (rc.getEnergonLevel() <= 20){
    				
            		MapLocation near[] = rc.senseAlliedArchons();
            		
            		float mindist = 1000;
            		MapLocation closest = null;
            		for(MapLocation l:near) {
            			float calc = rc.getLocation().distanceSquaredTo(l);
            			
            			if(calc < mindist) {
            				mindist = calc;
            				closest = l;
            			}
            		}
            		
                	Direction d = rc.getLocation().directionTo(closest);
    				
    				if(d==rc.getDirection() && rc.canMove(d) && !rc.isMovementActive()) {
    					System.out.println("moving towards archon");
    					rc.moveForward();
    				} else {
    					if (d!=Direction.OMNI && !rc.isMovementActive()) rc.setDirection(d);
    				}
                } else {
				
					
					if (!rc.isAttackActive()) fire();//fire on anything in range
	                
					//Transfer Energon to Allies
					Robot[] nearbyAllies = rc.senseNearbyGroundRobots();
					
					for (Robot r:nearbyAllies) {
	    				if (rc.senseRobotInfo(r).energonLevel < 20 && rc.senseRobotInfo(r).team != rc.getTeam().opponent() && rc.getEnergonLevel()>30) {
	    					MapLocation loc = rc.senseRobotInfo(r).location;
	    					if (loc.isAdjacentTo(rc.getLocation())) {
	    						rc.transferUnitEnergon(5, loc, RobotLevel.ON_GROUND);
	    						
	    					}
	    				}
	    			}
					
					//move randomly if nothing was fired upon
	                if (!rc.hasActionSet() && !rc.isMovementActive()){
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
	    						if (rc.canMove(rc.getDirection().opposite())) rc.moveBackward();
	    					}
	    				}
	                }
                }
                rc.yield();

	        	}
	        
	        
	        catch (Exception e){
	        	System.out.println("caught exception:");
	            e.printStackTrace();
	        }
		}
	}
	/**
	 * Senses for enemy units and attacks the first one it sees and can attack. (air first then ground)
	 */
	public void fire(){
		try {
			Robot[] nearbyAir = rc.senseNearbyAirRobots();
			for (Robot r:nearbyAir) {
				if (rc.senseRobotInfo(r).team == rc.getTeam().opponent()) {
					MapLocation loc = rc.senseRobotInfo(r).location;
					if (rc.canAttackSquare(rc.senseRobotInfo(r).location)){
						System.out.println("attacking air");
						rc.attackAir(loc);
						rc.yield();
						return;
					}
				}
			}

			Robot[] nearbyGround = rc.senseNearbyGroundRobots();
			for (Robot r:nearbyGround) {
				if (rc.senseRobotInfo(r).team == rc.getTeam().opponent()) {
					MapLocation loc = rc.senseRobotInfo(r).location;
					if (rc.canAttackSquare(rc.senseRobotInfo(r).location)){
						System.out.println("attacking ground");
						rc.attackGround(loc);
						rc.yield();
						return;
					}
				}
			}
		} catch(Exception e){
        	System.out.println("caught exception:");
            e.printStackTrace();
		}
	
	}

}
