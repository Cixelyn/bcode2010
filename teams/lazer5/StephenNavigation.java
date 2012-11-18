package lazer5;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile.TerrainType;

public class StephenNavigation {
	protected final RobotPlayer player;
	protected final RobotController myRC;
	protected final Intelligence myIntel;
	
	private boolean buggingWallOnLeft = false;
	private boolean buggingWallOnRight = false;
	private int buggingCounter = 0;
	private final int MAX_ROUNDS_TO_BUG = 35;
	
	public final static double scaleCohesion = 4.0;
	public final static double scaleSeparation = 8.0;
	public final static double scaleAlignment = 1.0;
	public final static double scaleDestinationVector = 10.0;
	public final static int separateDistance = 16;
	
	public StephenNavigation(RobotPlayer rPlayer) {
		player = rPlayer;
		myRC = player.myRC;
		myIntel = player.myIntel;

	}
	
	/**
	 * Tries to move in given direction (forward or backward)
	 * @param dir - direction to move
	 */
	public void moveInDirection(Direction dir) {
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			if (dir.ordinal() > 7)
				return;
			Direction currentDir = player.myRC.getDirection();
			if (!(myRC.canMove(dir)))
				return;
			try {
				if (currentDir.equals(dir)) {
					myRC.moveForward();
					return;
				} else if (currentDir.equals(dir.opposite())) {
					myRC.moveBackward();
					return;
				} else {
					myRC.setDirection(dir);
					return;
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * same as moves in direction, but does extra check to see if it's moving over land
	 * @param dir
	 */
	public void archonMoveInDirection(Direction dir) {
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			if (dir.ordinal() > 7)
				return;
			Direction currentDir = player.myRC.getDirection();
			if (!(canMoveOverGround(dir)))
				return;
			try {
				if (currentDir.equals(dir)) {
					myRC.moveForward();
					return;
				} else if (currentDir.equals(dir.opposite())) {
					myRC.moveBackward();
					return;
				} else {
					myRC.setDirection(dir);
					return;
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Uses bugging to get to desired location
	 * 
	 * @param dest
	 * @throws GameActionException
	 */
	public void bugTo(MapLocation dest) throws GameActionException {
//		myRC.setIndicatorString(0, buggingWallOnLeft ? "true" : "false");
//		myRC.setIndicatorString(1, buggingWallOnRight ? "true" : "false");
//		System.out.println("buggingWallOnLeft =" + buggingWallOnLeft + ", buggingWallOnRight = " + buggingWallOnRight);
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			if(buggingCounter>=25){
				buggingWallOnLeft = !buggingWallOnLeft;
				buggingWallOnRight = !buggingWallOnRight;
				buggingCounter = 0;
				myRC.setDirection(myRC.getDirection().opposite());
				return;
			}
			Direction directionTo = myRC.getLocation().directionTo(dest);
			if (buggingWallOnLeft) {
				buggingCounter++;
				//if we can move towards goal, and that isn't moving backwards, go towards goal, we are no longer bugging
				if(myRC.canMove(directionTo) && isForwardDirection(myRC.getDirection(), directionTo)){
					buggingWallOnLeft = false;
					buggingCounter = 0;
					myRC.setDirection(directionTo);
					return;
				}
				else if(myRC.canMove(myRC.getDirection().rotateLeft())){
					myRC.setDirection(myRC.getDirection().rotateLeft());
					return;
				}
				else { //ok, keep bugging
					if (myRC.canMove(myRC.getDirection())) {
						myRC.moveForward();
						return;
					} else { //hit a wall
						myRC.setDirection(myRC.getDirection().rotateRight());
						return;
					}
				}

			} else if(buggingWallOnRight){
				buggingCounter++;
				if(myRC.canMove(directionTo) && isForwardDirection(myRC.getDirection(), directionTo)){
					buggingWallOnRight = false;
					buggingCounter = 0;
					myRC.setDirection(directionTo);
					return;
				}
				else if(myRC.canMove(myRC.getDirection().rotateRight())){
					myRC.setDirection(myRC.getDirection().rotateRight());
					return;
				}
				else { //ok, keep bugging
					if (myRC.canMove(myRC.getDirection())) {
						myRC.moveForward();
						return;
					} else { //hit wall
						myRC.setDirection(myRC.getDirection().rotateLeft());
						return;
					}
				}
				
			} else {//not bugging
				if (player.myRC.canMove(directionTo)) {
					moveInDirection(directionTo);
					return;
				} else if(myRC.canMove(myRC.getDirection())){
					myRC.moveForward();
					return;
				}
				else {//hit a wall
					myRC.setDirection(determineBugDir(dest, myRC.getLocation(), myRC.getDirection()));
					return;
				}
			}
		}
	}

	/**
	 * just like bugTo, but uses canMoveOverGround so that archons don't travel over 
	 * voids even though they can.
	 * @param dest
	 * @throws GameActionException
	 */
	public void archonBugTo(MapLocation dest) throws GameActionException {
		if (player.myRC.getRoundsUntilMovementIdle()==0) {
			if(buggingCounter>=25){
				buggingWallOnLeft = !buggingWallOnLeft;
				buggingWallOnRight = !buggingWallOnRight;
				buggingCounter = 0;
				myRC.setDirection(myRC.getDirection().opposite());
				return;
			}
			Direction directionTo = myRC.getLocation().directionTo(dest);
			if (buggingWallOnLeft) {
				buggingCounter++;
				//if we can move towards goal, and that isn't moving backwards, go towards goal, we are no longer bugging
				if(canMoveOverGround(directionTo) && isForwardDirection(myRC.getDirection(), directionTo)){
					buggingWallOnLeft = false;
					buggingCounter = 0;
					myRC.setDirection(directionTo);
					return;
				}
				else if(canMoveOverGround(myRC.getDirection().rotateLeft())){
					myRC.setDirection(myRC.getDirection().rotateLeft());
					return;
				}
				else { //ok, keep bugging
					if (canMoveOverGround(myRC.getDirection())) {
						myRC.moveForward();
						return;
					} else { //hit a wall
						myRC.setDirection(myRC.getDirection().rotateRight());
						return;
					}
				}

			} else if(buggingWallOnRight){
				buggingCounter++;
				if(canMoveOverGround(directionTo) && isForwardDirection(myRC.getDirection(), directionTo)){
					buggingWallOnRight = false;
					buggingCounter = 0;
					myRC.setDirection(directionTo);
					return;
				}
				else if(canMoveOverGround(myRC.getDirection().rotateRight())){
					myRC.setDirection(myRC.getDirection().rotateRight());
					return;
				}
				else { //ok, keep bugging
					if (canMoveOverGround(myRC.getDirection())) {
						myRC.moveForward();
						return;
					} else { //hit wall
						myRC.setDirection(myRC.getDirection().rotateLeft());
						return;
					}
				}
				
			} else {//not bugging
				if (canMoveOverGround(directionTo)) {
					archonMoveInDirection(directionTo);
					return;
				} else if(canMoveOverGround(myRC.getDirection())){
					myRC.moveForward();
					return;
				}
				else {//hit a wall
					myRC.setDirection(determineBugDir(dest, myRC.getLocation(), myRC.getDirection()));
					return;
				}
			}
		}
	}
	
	//	public Direction getClosestForwardDirection(MapLocation dest, MapLocation self, Direction dir){
	//	//relative tiles directions (diagLeft, diagRight, right, left);
	//	Direction[] directions = new Direction[2];
	//	switch(dir){
	//	case NORTH:
	//		directions[0] = Direction.NORTH_WEST;
	//		directions[1] = Direction.NORTH_EAST;
	//		break;
	//	case EAST:
	//		directions[0] = Direction.NORTH_EAST;
	//		directions[1] = Direction.SOUTH_EAST;
	//		break;
	//	case WEST:
	//		directions[0] =Direction.SOUTH_WEST;
	//		directions[1] = Direction.NORTH_WEST;
	//		break;
	//	case SOUTH:
	//		directions[0] = Direction.SOUTH_EAST;
	//		directions[1] = Direction.SOUTH_WEST;
	//		break;
	//	default: 
	//		directions[0] = dir;
	//		directions[1] = dir;
	//		break;
	//	}
	//	int minSqDist = 5000;
	//	//could later determine faster order in which to check tiles most efficiently
	//	Direction best = dir;
	//	for(int i=0; i<2; i++){
	//		int checkingDist = self.add(directions[i]).distanceSquaredTo(dest);
	//		if( checkingDist < minSqDist){
	//			best = directions[0];
	//			minSqDist = checkingDist;
	//		}
	//	}
	//	return best;
	//}
	
	
		
	/**
	 * Calls bugTo(destination) by calculating a destination based on the direction.
	 * NorthWest -> (0,0), SouthEast -> (99999, 99999)
	 * @param dir - Direction to bug in
	 * @throws GameActionException
	 */
	public void bugInDirection(Direction dir) throws GameActionException {
		int x = myRC.getLocation().getX();
		int y = myRC.getLocation().getY();
		MapLocation dest;
		switch(dir){
		case NORTH:
			dest = new MapLocation(x, y-99999);
			break;
		case SOUTH:
			dest = new MapLocation(x, y+99999);
			break;
		case EAST:
			dest = new MapLocation(x+99999, y);
			break;
		case WEST:
			dest = new MapLocation(x-99999, y);
			break;
		case NORTH_EAST:
			dest = new MapLocation(x+99999, y-99999);
			break;
		case NORTH_WEST:
			dest = new MapLocation(x-99999, y-99999);
			break;
		case SOUTH_EAST:
			dest = new MapLocation(x+99999, y+99999);
			break;
		case SOUTH_WEST:
			dest = new MapLocation(x-99999, y+99999);
			break;
		default: dest = myRC.getLocation();
		}
		
		bugTo(dest);
	}

	/**
	 * Calculates a destination based on the given direction, calls archonBugTo
	 * @param dir
	 * @throws GameActionException
	 */
	public void archonBugInDirection(Direction dir) throws GameActionException {
		int x = myRC.getLocation().getX();
		int y = myRC.getLocation().getY();
		MapLocation dest;
		switch(dir){
		case NORTH:
			dest = new MapLocation(x, y-99999);
			break;
		case SOUTH:
			dest = new MapLocation(x, y+99999);
			break;
		case EAST:
			dest = new MapLocation(x+99999, y);
			break;
		case WEST:
			dest = new MapLocation(x-99999, y);
			break;
		case NORTH_EAST:
			dest = new MapLocation(x+99999, y-99999);
			break;
		case NORTH_WEST:
			dest = new MapLocation(x-99999, y-99999);
			break;
		case SOUTH_EAST:
			dest = new MapLocation(x+99999, y+99999);
			break;
		case SOUTH_WEST:
			dest = new MapLocation(x-99999, y+99999);
			break;
		default: dest = myRC.getLocation();
		}
		
		archonBugTo(dest);
	}
	

	/**
	 * Called when robot hits a wall while not in bugging state, used to determine whether or not to rotate right
	 * or left.  Calculates by determining whether tile on left or right is closer
	 * 
	 * @param dest - target destination
	 * @param self - robot's location
	 * @param dir - robot's current direction
	 * @return right or left (relative to robot's current direction)
	 */
	public Direction determineBugDir(MapLocation dest, MapLocation self, Direction dir){
		Direction right = dir.rotateRight();
		Direction left = dir.rotateLeft();
		//don't have to care whether the tiles to the left or right are actually traversible because we just have to rotate
		//then set bugging to true, bug code will take care of the rest (will rotate again until can move forward)
		if(myRC.canMove(dir)){ //should not even be called because this method only called when we hit an obstacle
			return dir;
		}else{ //cannot move forward
			int goalX = dest.getX();
			int goalY = dest.getY();
			int rightX = self.add(right).getX();
			int rightY = self.add(right).getY();
			int leftX = self.add(left).getX();
			int leftY = self.add(left).getY();
			double rightDX = Math.abs(goalX - rightX);
			double rightDY = Math.abs(goalY - rightY);
			double leftDX = Math.abs(goalX - leftX);
			double leftDY = Math.abs(goalY - leftY);
			
			//if target is right in front of robot, make arbitrary decision
			if (rightDX==leftDX && rightDY==leftDY){
				buggingWallOnLeft = true;
				buggingWallOnRight = false;
				buggingCounter = 0;
				return right;
			}
			else if(rightDX==leftDX){
				if(rightDY<leftDY){
					buggingWallOnLeft = true;
					buggingWallOnRight = false;
					buggingCounter = 0;
					return right;
				}else{
					buggingWallOnLeft = false;
					buggingWallOnRight = true;
					buggingCounter = 0;
					return left;
				}
			}else if(rightDY==leftDY){
				if(rightDX<leftDX){
					buggingWallOnLeft = true;
					buggingWallOnRight = false;
					buggingCounter = 0;
					return right;
				}else{
					buggingWallOnLeft = false;
					buggingWallOnRight = true;
					buggingCounter = 0;
					return left;
				}
			}
			
			//shouldn't get here
			return left;

		}
	}
	
	/**
	 * Determines whether target direction is forward, forward-left, or  forward-right of current direction
	 * 
	 * @param myDir - current Direction
	 * @param target - desired direction
	 * @return boolean
	 */
	public boolean isForwardDirection(Direction myDir, Direction target){
		switch(myDir){
		case NORTH:
			if(target==Direction.NORTH || target==Direction.NORTH_WEST || target==Direction.NORTH_EAST){
				return true;
			}
			break;
		case SOUTH:
			if(target==Direction.SOUTH || target==Direction.SOUTH_EAST || target==Direction.SOUTH_WEST){
				return true;
			}
			break;
		case EAST:
			if(target==Direction.EAST || target==Direction.SOUTH_EAST || target==Direction.NORTH_EAST){
				return true;
			}
			break;
		case WEST:
			if(target==Direction.WEST || target==Direction.NORTH_WEST || target==Direction.SOUTH_WEST){
				return true;
			}
			break;
		}
		return false;
	}
	
	
	public void swarmTo(MapLocation dest) throws GameActionException {
		V2d vCohesion = new V2d(0,0);
		V2d vSeparation = new V2d(0,0);
		V2d vAlignment = new V2d(0,0);
		V2d vAvoidance = new V2d(0,0);
		
		
		V2d selfV = new V2d(player.myIntel.myLocation);
		MapLocation selfL = player.myIntel.myLocation;
		
		V2d vDestination = new V2d(dest).sub(selfV).norm().scale(scaleDestinationVector);
		
			
		/////////////////////////COHESION AND ALIGNMENT AND SEPARATION///////////////////////////////
		

		V2d coSum = new V2d(0,0); //Cohehsion Sum
		V2d alSum = new V2d(0,0); //Alignment Sum
		V2d seSum = new V2d(0,0); //Seperation Sum
		
		Robot[] robots = player.myIntel.getNearbyRobots();
		if(robots.length>0) {
			for(Robot r:robots) {
				RobotInfo currR = player.myRC.senseRobotInfo(r);

				if(currR!=null) {
					if(currR.team==player.myTeam) { //IF ITS ON OUR TEAMMMMMMM!!!!!!!!!!!!!
						RobotInfo curr = player.myRC.senseRobotInfo(r);
						
						coSum = coSum.add(new V2d(curr.location)); //cohesion
						alSum = alSum.add(new V2d(curr.directionFacing)); //alignment
						
						if(player.myIntel.myLocation.distanceSquaredTo(currR.location)<separateDistance) {
							seSum.add(new V2d(curr.location.directionTo(selfL)));
						}
			
					}
				}
			}
			
			V2d cM = coSum.scale(1.0/robots.length); //Center of Mass
			
			vCohesion = cM.sub(selfV).scale(scaleCohesion);
			vAlignment = alSum.scale(scaleAlignment);
			vSeparation = seSum.scale(scaleSeparation);	
		} 		
		
		
		//////////////////////COMPUTATION AND UPDATE///////////////////
		V2d vTotal = vCohesion.add(vSeparation.add(vAlignment.add(vAvoidance))).add(vDestination);
		
		//player.myRC.setIndicatorString(2, "Des: " + vDestination.toString());
		//player.myRC.setIndicatorString(1, "Coh: " + vCohesion.toString());
		//player.myRC.setIndicatorString(1, "Aln: " + vAlignment.toString());
		//player.myRC.setIndicatorString(2, "Sep: " + vSeparation.toString());
		
		MapLocation toGo = vTotal.add(selfV).toLoc();
		Direction toTurn = selfL.directionTo(toGo);
		
		bugInDirection(toTurn);
	}

	
	public void swarmToArchonCoM(int low, int hi) throws GameActionException {
		
		V2d selfV = new V2d(player.myIntel.myLocation);
		MapLocation selfL = player.myIntel.myLocation;
		
		V2d sum = new V2d(0,0);	//sum is the direction we need to head in
		MapLocation[] aList = player.myIntel.getArchonList();
				
		for(int i=low; i<=hi; i++) {
			sum = sum.add(new V2d(aList[i]));
		}
		
		sum = sum.scale(1.0/(hi-low+1));
		
		Direction toTurn = selfL.directionTo(sum.toLoc());

		bugInDirection(toTurn);
		
	}

	
	
	
	
	
	
	
	
	/**
	 * Like the RobotController function canMove(direction) but also checks to see if tile in that direction
	 * is also a land.
	 * 
	 * @param dir
	 * @return 
	 */
	public boolean canMoveOverGround(Direction dir){
		if(myRC.senseTerrainTile(myRC.getLocation().add(dir)).getType() != TerrainType.LAND) return false;
		if(myRC.canMove(dir)) return true;
		return false;
	}
	
	
	public void followNearestArchon() throws GameActionException{
		MapLocation nearestArchon = player.myIntel.getNearestArchon();
		bugTo(nearestArchon);
	}
	
}