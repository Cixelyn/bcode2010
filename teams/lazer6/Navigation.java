package lazer6;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Navigation {
	private final RobotPlayer player;
	private final RobotController myRC;


	
	
	public Navigation(RobotPlayer player) {
		this.player = player;
		myRC = player.myRC;
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////BUGNAV/////////////////////////////////////////////////////////
	private boolean isTracing;
	private boolean tracingRight;
	private int roundsTracing = 0;
	
	/**
	 * Extremely basic bugnav.  This should be temporary until we get another algorithm up and running
	 * <br>
	 * It doesn't handle concaves very well, but should do well on simple maps.  Will be replaced later w/
	 * an M-line system
	 * 
	 * 
	 * @param destLoc Location that you want to head to
	 * @return Direction you need to head in to get to your destination
	 */
	public Direction bugTo(MapLocation destLoc) {
		
		MapLocation currLoc = myRC.getLocation();
		//Direction currDir = myRC.getDirection();
		Direction currDir = player.myAct.lastHeading;  //This allows us to do michael jackson awesome shit
		Direction destDir = currLoc.directionTo(destLoc);
		
//		player.myRC.setIndicatorString(1, "Dest: "+destDir);
//		player.myRC.setIndicatorString(2, ""+isTracing);
		
		
		if(currLoc.equals(destLoc)) {
			isTracing=false;
			return Direction.OMNI;
		}
		

		
		if(isTracing) {
			
			//if we can move, go in that direction, stop tracing
			if(currDir==destDir || (roundsTracing > 20 && myRC.canMove(destDir))) { 
				isTracing = false;
				return destDir;
			}

			else { //we need to trace
				
				roundsTracing++;
				Boolean rotateLeft = false;
				Boolean isBlocked = false;
				Direction traceDir = currDir;
				
				//These statements can be replaced with binary manipulations
				if(tracingRight) {  //rotate as far left as possible. If not, rotate outwards.
					if(myRC.canMove(currDir)) {
						rotateLeft = true;
						isBlocked = false;
					} else {
						rotateLeft = false;
						isBlocked = true;
					}					
				}
				
				else { //we're tracing left.  Rotate as far right, then rotate left.
					if(myRC.canMove(currDir)) {
						rotateLeft = false;
						isBlocked = false;
					} else {
						rotateLeft = true;
						isBlocked = true;
					}
				}
				
				Direction oldDir=traceDir;
				
				

				for(int i=0; i<8; i++) {

					oldDir = traceDir;

					if(rotateLeft) traceDir = traceDir.rotateLeft();
					else traceDir = traceDir.rotateRight();
					
					if(isBlocked){ //We want to rotate to the first available space
						if(myRC.canMove(traceDir)) 
							return traceDir;
					} 
								
					else { //We want to rotate until we reach the wall again
						
						if(traceDir==destDir && myRC.canMove(destDir)) { //but break early if we can get on target
							return traceDir;
						}				
						if(!myRC.canMove(traceDir)) 
							return oldDir;
					}
				}
			
				//We are at the destination
				return Direction.OMNI;
			}
		} else { //not tracing
			
			if(player.myRC.canMove(destDir)) {
				return destDir;
			} 
					
			else {//we hit a wall, need to trace
				
				isTracing = true;
				
				//Figure out whether left or right is better.
				Direction leftDir=currDir;
				Direction rightDir=currDir;
				
				
				//Left Check
				for(int i=0; i<8; i++) {
					leftDir = leftDir.rotateLeft();	
					if(player.myRC.canMove(leftDir)) {
						break;
					}
				}
				for(int i=0; i<8; i++) {
					rightDir = rightDir.rotateRight();	
					if(player.myRC.canMove(rightDir)) {
						break;
					}
				}
				
				
				//Check which distance is shorter.
				MapLocation leftLoc = currLoc.add(leftDir).add(leftDir);
				MapLocation rightLoc = currLoc.add(rightDir).add(rightDir);
				roundsTracing = 0;

				if(destLoc.distanceSquaredTo(leftLoc)<destLoc.distanceSquaredTo(rightLoc)) {
					tracingRight = false;
					//System.out.println("Tracing Left");
					return leftDir;
				} else {
					tracingRight = true;
					//System.out.println("Tracing Right");
					return rightDir;
				}				
			}		
		}
	}
	
	public Direction bugInDir(Direction destDir){
		return bugTo(myRC.getLocation().add(destDir).add(destDir).add(destDir).add(destDir).add(destDir));
	}
	
	public MapLocation archonFormation() {
		/*	Attempted Formation
		 * 
		 *	  ^  ^  ^  ^  ^
		 * 	  2  4  5  3  1
		 * 		    ^
		 *          0
		 * 
		 * -------Pseudocode------
		 * 
		 * if(far from group)
		 * 		set directionvector to lead
		 * 
		 * if(cansense lead)
		 * 		set directionvector to offset
		 * 
		 * calculate vector to head in and go
		 * 
		 * 
		 */
		
		int leadArchonNum = player.myProfiler.alliedArchons.length;
		
		MapLocation leadArchonLoc = player.myProfiler.alliedArchons[leadArchonNum-1];
		MapLocation myLoc = myRC.getLocation();
		int distToLeadArchon = myLoc.distanceSquaredTo(leadArchonLoc);
		
		MapLocation destLoc=myLoc;
		
		
		myRC.setIndicatorString(1, ""+player.myProfiler.myArchonID);

		if(distToLeadArchon>36) {  ///////////////////HEAD TO LEAD ARCHON////////////	
			return leadArchonLoc;
		} else { //head in formation	
			
			RobotInfo leadArchon=null;

			try {	
				leadArchon = myRC.senseRobotInfo(myRC.senseAirRobotAtLocation(leadArchonLoc));
			} catch (GameActionException e) {
				e.printStackTrace();
			}

			Direction leadArchonDir = leadArchon.directionFacing;
			Direction formDir;
			
			int myArchonId = player.myProfiler.myArchonID;
			
			
			if(myArchonId == --leadArchonNum) {			//5
				//I am lead archon lol
				return leadArchonLoc;
			}else if (myArchonId == --leadArchonNum) {	//4
				formDir = leadArchonDir.rotateLeft().rotateLeft();
				destLoc = leadArchonLoc.add(formDir).add(formDir);
				
			}else if (myArchonId == --leadArchonNum) {	//3
				formDir = leadArchonDir.rotateRight().rotateRight();
				destLoc = leadArchonLoc.add(formDir).add(formDir);
			
			}else if (myArchonId == --leadArchonNum) {  //2
				formDir = leadArchonDir.rotateLeft().rotateLeft();
				destLoc = leadArchonLoc.add(formDir).add(formDir).add(formDir);
				
			}else if (myArchonId == --leadArchonNum) {  //1
				formDir = leadArchonDir.rotateRight().rotateRight();
				destLoc = leadArchonLoc.add(formDir).add(formDir).add(formDir);
				
			}else if (myArchonId == --leadArchonNum) {	//0
				formDir = leadArchonDir.opposite();
				destLoc = leadArchonLoc.add(formDir).add(formDir);	
			}
			
			return destLoc;
		}	
	}
	
	public boolean moveToLocation(MapLocation loc) {
		Direction dir = player.myNavi.bugTo(loc);
		return player.myAct.moveInDir(dir);	
	}
	
	
	
	public Direction mobArchonVector() {
		
		MapLocation myLoc = player.myRC.getLocation();
		MapLocation archonCoM = player.myProfiler.archonCoM;
		
		if(player.myRC.getLocation().distanceSquaredTo(archonCoM)>49) { 	//if we are far away
			return myLoc.directionTo(archonCoM);							//go to archon center of mass
		} else	{															//otherwise
			return player.myProfiler.archonDifferential;					//go in the archon differential
		}

		
	}
	
	
	
	
	
	
	
	


}