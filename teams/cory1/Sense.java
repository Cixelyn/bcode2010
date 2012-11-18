package cory1;

import battlecode.common.*;


public class Sense {
	
	public final RobotController rc;
	
	public Sense(RobotController r) {
		this.rc = r;
	}
	
	public MapLocation lowestHealthWout() throws GameActionException{
			
		Robot near[] = rc.senseNearbyGroundRobots();
		
		for(Robot r:near) {		
			if(rc.senseRobotInfo(r).energonLevel<5) {
				return rc.senseRobotInfo(r).location;
				
			} else {
				return null;
			}
		}
		
		return null;
	}
	
	public MapLocation nearestArchon() throws GameActionException{
		
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
		
		return closest;
	}
}