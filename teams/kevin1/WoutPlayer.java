package kevin1;
import battlecode.common.*;

import java.util.Random;
public class WoutPlayer extends BasePlayer {
	Random rand;
	public WoutPlayer(RobotController rc) {
	    super(rc);
	    rand = new Random(0);
	  }
	public void run(){
		while (true){
			try{
				while (rc.hasActionSet()) {
                    System.out.println("yielding due to action set");
					rc.yield();
                }
                
				//retreat to archon if energon low
				if (rc.getEnergonLevel() <= 15){
    				
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
    					System.out.println("moving toward archon");
    					rc.moveForward();
    				} else {
    					if (d!=Direction.OMNI && !rc.isMovementActive()) rc.setDirection(d);
    				}
                } else {
                	if (!rc.isAttackActive()) fire();//fire on anything in range
                	
                	//Transfer Energon to Allies
    				Robot[] nearbyAllies = rc.senseNearbyGroundRobots();
    				
    				for (Robot r:nearbyAllies) {
        				if (rc.senseRobotInfo(r).energonLevel < 20 && rc.senseRobotInfo(r).team != rc.getTeam().opponent() && rc.getEnergonLevel()>25) {
        					MapLocation loc = rc.senseRobotInfo(r).location;
        					if (loc.isAdjacentTo(rc.getLocation())) {
        						rc.transferUnitEnergon(3, loc, RobotLevel.ON_GROUND);
        						
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
	    				}/*if (rc.canMove(rc.getDirection())) {
	                		System.out.println("about to move");
	                		rc.moveForward();
	                	} else {
	                		rc.setDirection(rc.getDirection().rotateRight());
	                	}*/
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
	 * Searches for enemy units in the 8 squares adjacent to the wout and attacks the first one it sees. (air first then ground)
	 */
	public void fire(){
		try {
			for (int i = 7; i >= 0; --i) {
				Robot r = rc.senseAirRobotAtLocation(rc.getLocation().add(directions[i]));
				if ((r != null) &&(rc.senseRobotInfo(r).team != rc.getTeam())) {
					System.out.println("attacking air");
					rc.attackAir(rc.getLocation().add(directions[i]));
					rc.yield();
					return;
				}
			}

	      for (int i = 7; i >= 0; --i) {
	          Robot r = rc.senseGroundRobotAtLocation(rc.getLocation().add(directions[i]));
	          if ((r != null) && (rc.senseRobotInfo(r).team != rc.getTeam())) {
	        	  System.out.println("attacking ground");
	        	  rc.attackGround(rc.getLocation().add(directions[i]));
	        	  rc.yield();
	        	  return;
	          }
	        }
		} catch(Exception e){
        	System.out.println("caught exception:");
            e.printStackTrace();
		}
	}
}
