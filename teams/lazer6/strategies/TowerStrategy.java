package lazer6.strategies;

import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile.TerrainType;

public class TowerStrategy extends Strategy {
	public boolean isCenter = false;
	public boolean isNorthBranch = false;
	public boolean isSouthBranch = false;
	public boolean isEastBranch = false;
	public boolean isWestBranch = false;
	public boolean shouldBroadcast = true;
	public boolean towerAbove = false;
	public boolean towerBelow = false;
	public boolean towerRight = false;
	public boolean towerLeft = false;
	public boolean wallOnRight = false;
	public boolean wallOnLeft = false;
	public boolean wallAbove = false;
	public boolean wallBelow = false;
	

	private MapLocation myLoc;
	private int myX;
	private int myY;
	private int radius;
	
	
	private int broadcastCounter = 0;

	public TowerStrategy(RobotPlayer player) {
		super(player);
		myProfiler.setScanMode(true, false, false, false);
		
		myLoc = player.myRC.getLocation();
		myX = myLoc.getX();
		myY = myLoc.getY();
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		runBroadcast();
	}

	@Override
	public void runInstincts() {
		
	}
	
	/**
	 * Checks where in cross we are located, and then broadcasts relevant build location
	 */
	public void runBroadcast(){
		this.checkNearbyTowers();
		if (woutInRange()) {
			if (broadcastCounter%10 == 0) {
				this.broadcastLocation();
			}
			broadcastCounter++;
		} else {
			broadcastCounter = 0;
		}
	}
	
	/**
	 * This method sets isNorthBranch, isSouthBranch, isEastBranch, isWestBranch, and isCenter
	 * 
	 * Checks to see if tower has any neighboring towers north, south, east, and/or west to determine
	 * which part of the cross it is in.  Also determines whether tower should broadcast a location or not.
	 * (e.g. a tower should not broadcast a location if it is in the north branch but there is also a tower north of it, 
	 * otherwise we would be broadcasting redundant/unnecessary tower build locations).
	 */
	private void checkNearbyTowers(){
		shouldBroadcast = true;
		isNorthBranch = false;
		isSouthBranch = false;
		isEastBranch = false;
		isWestBranch = false;
		//these next 4 booleans are redundant with prev 4, but helps to think about it logic wise (isSouthBranch if somethingAbove)
		towerAbove = false;
		towerBelow = false;
		towerRight = false;
		towerLeft = false;
		
		wallOnRight = false;
		wallOnLeft = false;
		wallAbove = false;
		wallBelow = false;
		
		Robot robotNorth;
		Robot robotSouth;
		Robot robotEast;
		Robot robotWest;
		try {
			radius = Math.min(myRC.getRobotType().sensorRadius(), 5);
			MapLocation targetNorth, targetSouth, targetEast, targetWest;
			for (int i=radius; i>0; i--){
				targetNorth = new MapLocation(myX, myY-i);
				targetSouth = new MapLocation(myX, myY+i);
				targetEast = new MapLocation(myX+i, myY);
				targetWest = new MapLocation(myX-i, myY);
				robotNorth = myRC.senseGroundRobotAtLocation(targetNorth);
				robotSouth = myRC.senseGroundRobotAtLocation(targetSouth);
				robotEast = myRC.senseGroundRobotAtLocation(targetEast);
				robotWest = myRC.senseGroundRobotAtLocation(targetWest);
				
				if(myRC.senseTerrainTile(targetNorth).getType() == TerrainType.OFF_MAP){
					wallAbove = true;
				}else if(myRC.senseTerrainTile(targetSouth).getType() == TerrainType.OFF_MAP){
					wallBelow = true;
				}else if(myRC.senseTerrainTile(targetEast).getType() == TerrainType.OFF_MAP){
					wallOnRight = true;
				}else if(myRC.senseTerrainTile(targetWest).getType() == TerrainType.OFF_MAP){
					wallOnLeft = true;
				}
				
				if (!isSouthBranch) {
					if (robotNorth != null) {
						if (myRC.senseRobotInfo(robotNorth).type.ordinal() > 4) {
							towerAbove = true;
							isSouthBranch = true;
						}
					}
				}
				if (!isNorthBranch) {
					if (robotSouth != null) {
						if (myRC.senseRobotInfo(robotSouth).type.ordinal() > 4) {
							towerBelow = true;
							isNorthBranch = true;
						}
					}
				}
				if (!isWestBranch) {
					if (robotEast != null) {
						if (myRC.senseRobotInfo(robotEast).type.ordinal() > 4) {
							towerRight = true;
							isWestBranch = true;
						}
					}
				}
				if (!isEastBranch) {
					if (robotWest != null) {
						if (myRC.senseRobotInfo(robotWest).type.ordinal() > 4) {
							towerLeft = true;
							isEastBranch = true;
						}
					}
				}
			}
		} catch (GameActionException e) {
			System.out.println("Caught exception: checkNearbyTowers in tower Strategy");
			e.printStackTrace();
		}
		
		//if we don't see anything north, south, east or west of us, we think we center of the cross
		if(!isNorthBranch && !isSouthBranch && !isEastBranch && !isWestBranch){
			isCenter = true;
			isNorthBranch = false;
			isSouthBranch = false;
			isEastBranch = false;
			isWestBranch = false;
		}
		if(isCenter){
			if(towerAbove && towerBelow && towerRight && towerLeft){
				shouldBroadcast = false;
			}else{
				shouldBroadcast = true;
			}
		}else{
			if(isNorthBranch && towerAbove) shouldBroadcast = false;
			if(isSouthBranch && towerBelow) shouldBroadcast = false;
			if(isEastBranch && towerRight) shouldBroadcast = false;
			if(isWestBranch && towerLeft) shouldBroadcast = false;
		}
	}
	
	/**
	 * Determines what build location to broadcast
	 */
	private void broadcastLocation(){
//		System.out.println("isCenter: " + this.isCenter + ", isNorthBranch: " + this.isNorthBranch + ", isSouthBranch: " + this.isSouthBranch + ", isEastBranch: " + this.isEastBranch
//				+ ", isWestBranch: " + this.isWestBranch + ", shouldBroadcast: " + this.shouldBroadcast);
		
		if(this.shouldBroadcast){
			if(this.isCenter){
				broadcastAvailable();
			}else if(this.isNorthBranch){
				if(!wallAbove){	//if we are not up against a wall
					broadcastNorth();
				}else{
					broadcastWest();
				}
			}else if(this.isEastBranch){
				if(!wallOnRight){
					broadcastEast();
				}else{
					broadcastNorth();
				}
			}else if(this.isSouthBranch){
				if(!wallBelow){
					broadcastSouth();
				}else{
					broadcastEast();
				}
			}else if(this.isWestBranch){
				if(!wallOnLeft){
					broadcastWest();
				}else{
					broadcastSouth();
				}
			}
		}
	}

	/**
	 * Checks the 5 spaces to the north of tower (starting from farthest tile) and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 */
	private void broadcastNorth(){
		radius = Math.min(myRC.getRobotType().sensorRadius(), 5);
		MapLocation target;
		for (int i=radius; i>=0; i--){
			target = new MapLocation(myX, myY-i);
			if(canBuildTower(target)){
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target);
				myRC.setIndicatorString(1, myLoc +" broadcasting "+target);
				return;
			}
		}
	}
	
	/**
	 * Checks the 5 spaces to the south of tower (starting from farthest tile) and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 */
	private void broadcastSouth(){
		radius = Math.min(myRC.getRobotType().sensorRadius(), 5);
		MapLocation target;
		for (int i=radius; i>0; i--){
			target = new MapLocation(myX, myY+i);
			if(canBuildTower(target)){
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target);
				myRC.setIndicatorString(1,  myLoc +" broadcasting "+target);
				return;
			}
		}
	}

	/**
	 * Checks the 5 spaces to the east of tower (starting from farthest tile) and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 */
	private void broadcastEast(){
		radius = Math.min(myRC.getRobotType().sensorRadius(), 5);
		MapLocation target;
		for (int i=radius; i>0; i--){
			target = new MapLocation(myX+i, myY);
			if(canBuildTower(target)){
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target);
				myRC.setIndicatorString(1,  myLoc +" broadcasting "+target);
				return;
			}
		}
	}

	/**
	 * Checks the 5 spaces to the west of tower (starting from farthest tile) and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 */
	private void broadcastWest(){
		radius = Math.min(myRC.getRobotType().sensorRadius(), 5);
		MapLocation target;
		for (int i=radius; i>0; i--){
			target = new MapLocation(myX-i, myY);
			if(canBuildTower(target)){
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, target);
				myRC.setIndicatorString(1,  myLoc +" broadcasting "+target);
				return;
			}
		}
	}

	/**
	 * Broadcasts an available direction (north, south, east, or west) if no tower is in that direction
	 */
	private void broadcastAvailable(){
		if(!towerAbove) broadcastNorth();
		else if(!towerRight) broadcastEast();
		else if(!towerBelow) broadcastSouth();
		else if(!towerLeft) broadcastWest();
	}
	
	/**
	 * Checks if given MapLocation is a land tile and is not occupied by another tower.
	 * @param MapLocation loc
	 * @return
	 */
	private boolean canBuildTower(MapLocation loc){
		try {
			Robot rob = myRC.senseGroundRobotAtLocation(loc);
			if(myRC.senseTerrainTile(loc).getType() == TerrainType.LAND){
				if(rob==null){
					return true;
				}else{
					if(myRC.senseRobotInfo(rob).type.ordinal() < 6){
						return true;
					}
				}
			}
		} catch (GameActionException e) {
			System.out.println("caught game action exception: tower strategy");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if there is a wout within sensing range
	 * @return
	 */
	private boolean woutInRange(){
		try {
			Robot[] nearby = player.myProfiler.nearbyGroundRobots;
			RobotInfo nInfo;
			for(int i=0; i<nearby.length; i++){
				nInfo = myRC.senseRobotInfo(nearby[i]);
				if(nInfo.type.ordinal() == 1){
					if(nInfo.team == player.myTeam){
						return true;
					}
				}
			}
		} catch (GameActionException e) {
			System.out.println("caught exception in tower strategy: woutInRange");
			e.printStackTrace();
		}
		return false;
	}
}
