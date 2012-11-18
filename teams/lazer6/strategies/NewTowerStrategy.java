package lazer6.strategies;

import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile.TerrainType;

public class NewTowerStrategy extends Strategy {
	private boolean isCenter = false;
	private boolean isNorthBranch = false;
	private boolean isSouthBranch = false;
	private boolean isEastBranch = false;
	private boolean isWestBranch = false;
	private boolean shouldBroadcast = true;
	private boolean towerAbove = false;
	private boolean towerBelow = false;
	private boolean towerRight = false;
	private boolean towerLeft = false;
	private boolean wallEast = false;
	private boolean wallWest = false;
	private boolean wallNorth = false;
	private boolean wallSouth = false;
	private boolean wallNE = false;
	private boolean wallNW = false;
	private boolean wallSW = false;
	private boolean wallSE = false;
	private boolean towerNE = false;
	private boolean towerNW = false;
	private boolean towerSW = false;
	private boolean towerSE = false;
	

	private MapLocation targetNorth, targetSouth, targetEast, targetWest;
	private MapLocation targetNE, targetNW, targetSE, targetSW;
	

	private MapLocation myLoc;
	private int radius;
	
	private int broadcastCounter = 0;
	

	public NewTowerStrategy(RobotPlayer player) {
		super(player);
		myProfiler.setScanMode(true, false, false, false);
		
		myLoc = player.myRC.getLocation();
		int myX = myLoc.getX();
		int myY = myLoc.getY();

		radius = Math.min(myRC.getRobotType().sensorRadius(), 5);
		targetNorth = new MapLocation(myX, myY-radius);
		targetSouth = new MapLocation(myX, myY+radius);
		targetEast = new MapLocation(myX+radius, myY);
		targetWest = new MapLocation(myX-radius, myY);
		if (myRC.getRobotType().ordinal() == 5) {		//comm tower - sensor radius 11
			targetNE = new MapLocation(myX + 3, myY - 4);
			targetNW = new MapLocation(myX - 3, myY - 4);
			targetSE = new MapLocation(myX + 3, myY + 4);
			targetSW = new MapLocation(myX - 3, myY + 4);
		}else if(myRC.getRobotType().ordinal() == 6){	//teleporter - sensor radius 3
			targetNE = new MapLocation(myX + 2, myY - 2);
			targetNW = new MapLocation(myX - 2, myY - 2);
			targetSE = new MapLocation(myX + 2, myY + 2);
			targetSW = new MapLocation(myX - 2, myY + 2);
		}else{											//aura tower - sensor radius 4
			targetNE = new MapLocation(myX + 2, myY - 3);
			targetNW = new MapLocation(myX - 2, myY - 3);
			targetSE = new MapLocation(myX + 2, myY + 3);
			targetSW = new MapLocation(myX - 2, myY + 3);
		}
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		RobotInfo enemy = player.myProfiler.closestEnemyInfo;
		if (enemy!=null) {
			MapLocation enemyLoc = enemy.location;
			if (enemyLoc != null) {
				if (player.myRC.getLocation().distanceSquaredTo(enemyLoc) < 17) {
					if (Clock.getRoundNum() % 7 == 0) {
						player.myRadio.sendSingleDestination(
								MsgType.MSG_ENGAGINGENEMY, enemyLoc);
					}
				}
			}
		}
		runBroadcast();
	}
	
	@Override
	public void runInstincts(){
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
		
		wallEast = false;
		wallWest = false;
		wallNorth = false;
		wallSouth = false;
		
		wallNE = false;
		wallNW = false;
		wallSW = false;
		wallSE = false;
		
		towerNE = false;
		towerNW = false;
		towerSW = false;
		towerSE = false;
		
		Robot robotNorth, robotSouth, robotEast, robotWest;
		Robot robotNE, robotNW, robotSE, robotSW;
		try {
				robotNorth = myRC.senseGroundRobotAtLocation(targetNorth);
				robotSouth = myRC.senseGroundRobotAtLocation(targetSouth);
				robotEast = myRC.senseGroundRobotAtLocation(targetEast);
				robotWest = myRC.senseGroundRobotAtLocation(targetWest);
				//check diagonals
				robotNE = myRC.senseGroundRobotAtLocation(targetNE);
				robotSE = myRC.senseGroundRobotAtLocation(targetSE);
				robotNW = myRC.senseGroundRobotAtLocation(targetNW);
				robotSW = myRC.senseGroundRobotAtLocation(targetSW);
				
				//if there is a wall in given direction
				if(myRC.senseTerrainTile(targetNorth).getType().ordinal() > 0){
					wallNorth = true;
				}
				if(myRC.senseTerrainTile(targetSouth).getType().ordinal() > 0){
					wallSouth = true;
				}
				if(myRC.senseTerrainTile(targetEast).getType().ordinal() > 0){
					wallEast = true;
				}
				if(myRC.senseTerrainTile(targetWest).getType().ordinal() > 0){
					wallWest = true;
				}
				if(myRC.senseTerrainTile(targetNW).getType().ordinal() > 0){
					wallNW = true;
				}
				if(myRC.senseTerrainTile(targetNE).getType().ordinal() > 0){
					wallNE = true;
				}
				if(myRC.senseTerrainTile(targetSW).getType().ordinal() > 0){
					wallSW = true;
				}
				if(myRC.senseTerrainTile(targetSE).getType().ordinal() > 0){
					wallSE = true;
				}
				
				//check if there are towers north, south, east or west of self to determine branch of cross
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
				
				//check to see if there are towers on diagonals from self
				if (robotNW != null) {
					if (myRC.senseRobotInfo(robotNW).type.ordinal() > 4) {
						towerNW = true;
					}
				}
				if (robotNE != null) {
					if (myRC.senseRobotInfo(robotNE).type.ordinal() > 4) {
						towerNE = true;
					}
				}
				if (robotSW != null) {
					if (myRC.senseRobotInfo(robotSW).type.ordinal() > 4) {
						towerSW = true;
					}
				}
				if (robotSE != null) {
					if (myRC.senseRobotInfo(robotSE).type.ordinal() > 4) {
						towerSE = true;
					}
				}
				
		} catch (GameActionException e) {
			System.out.println("Caught exception: checkNearbyTowers in tower Strategy");
			e.printStackTrace();
		}
		
		//if we don't see anything north, south, east or west of us, we think we center of the cross
		if(!isNorthBranch && !isSouthBranch && !isEastBranch && !isWestBranch 
				&& !towerNW && !towerNE && !towerSW && !towerSE){
			isCenter = true;
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
			if(towerNE && towerSW) shouldBroadcast = false;
			if(towerNW && towerSE) shouldBroadcast = false;
		}
	}
	
	/**
	 * Determines what build location to broadcast. Checks directions in front, 45 ccw, 45 cw, 90 ccw, 90 cw
	 * (e.g. North, NW, NE, W, E)
	 */
	private void broadcastLocation(){
//		System.out.println("isCenter: " + this.isCenter + ", isNorthBranch: " + this.isNorthBranch + ", isSouthBranch: " + this.isSouthBranch + ", isEastBranch: " + this.isEastBranch
//				+ ", isWestBranch: " + this.isWestBranch + ", shouldBroadcast: " + this.shouldBroadcast);
		
		if(this.shouldBroadcast){
			if(this.isCenter){
				broadcastAvailable();
			}else if(this.isNorthBranch){
				broadcastNorth();
			}else if(this.isEastBranch){
				broadcastEast();
			}else if(this.isSouthBranch){
				broadcastSouth();
			}else if(this.isWestBranch){
				broadcastWest();
			}
		}
	}

	/**
	 * Checks the 5 spaces to the north of tower and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 * If there is a wall or void north, then broadcast northwest location
	 */
	private void broadcastNorth(){
		if(wallNorth){
			broadcastNW();
			return;
		}else if(canBuildTower(targetNorth)){
				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetNorth);
//				myRC.setIndicatorString(1, myLoc +" broadcasting "+targetNorth);
				return;
		}
//		if(canBuildTower(targetNorth)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetNorth);
//			myRC.setIndicatorString(1, myLoc +" broadcasting "+targetNorth);
//			return;
//		}
	}
	
	/**
	 * Checks the 5 spaces to the south of tower and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 * If there is a wall or void south, then broadcast southeast location
	 */
	private void broadcastSouth(){
		if(wallSouth){
			broadcastSE();
			return;
		}else if(canBuildTower(targetSouth)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetSouth);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetSouth);
			return;
		}
//		if(canBuildTower(targetSouth)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetSouth);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetSouth);
//			return;
//		}
	}

	/**
	 * Checks the 5 spaces to the east of tower  and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 * If there is a wall or void east, then broadcast Northeast location
	 */
	private void broadcastEast(){
		if(wallEast){
			broadcastNE();
			return;
		}else if(canBuildTower(targetEast)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetEast);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetEast);
			return;
		}
//		if(canBuildTower(targetEast)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetEast);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetEast);
//			return;
//		}
	}

	/**
	 * Checks the 5 spaces to the west of tower and checks whether or not another
	 * tower can be built there (if land and not occupied by a tower) and broadcasts that location.
	 * If there is a wall or void West, then broadcast southwest location
	 */
	private void broadcastWest(){
		if(wallWest){
			broadcastSW();
			return;
		}else if(canBuildTower(targetWest)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetWest);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetWest);
			return;
		}
//		if(canBuildTower(targetWest)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetWest);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetWest);
//			return;
//		}
	}

	/**
	 * Checks to see if tile 4 to the right and 3 up is free, if so, broadcast that location
	 * If there is a wall or void northeast, then broadcast north location
	 */
	private void broadcastNE(){
		if(wallNE){
			broadcastNorth();
			return;
		}else if(canBuildTower(targetNE)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetNE);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetNE);
			return;
		}
//		if(canBuildTower(targetNE)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetNE);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetNE);
//			return;
//		}
	}
	
	/**
	 * Checks to see if tile 4 to the right and 3 down is free, if so, broadcast that location
	 * If there is a wall or void southeast, then broadcast east location
	 */
	private void broadcastSE(){
		if(wallSE){
			broadcastEast();
			return;
		}else if(canBuildTower(targetSE)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetSE);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetSE);
			return;
		}
//		if(canBuildTower(targetSE)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetSE);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetSE);
//			return;
//		}
	}
	
	/**
	 * Checks to see if tile 4 to the Left and 3 up is free, if so, broadcast that location
	 * If there is a wall or void Northwest, then broadcast west location
	 */
	private void broadcastNW(){
		if(wallNW){
			broadcastWest();
			return;
		}else if(canBuildTower(targetNW)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetNW);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetNW);
			return;
		}
//		if(canBuildTower(targetNW)){
//				player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetNW);
//				myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetNW);
//				return;
//		}
	}
	
	/**
	 * Checks to see if tile 4 to the left and 3 down is free, if so, broadcast that location.
	 * If there is a wall or void southwest, then broadcast south location
	 */
	private void broadcastSW(){
		if(wallSW){
			broadcastSouth();
			return;
		}else if(canBuildTower(targetSW)){
			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetSW);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetSW);
			return;
		}
//		if(canBuildTower(targetSW)){
//			player.myRadio.sendSingleDestination(MsgType.MSG_BUILDTOWERHERE, targetSW);
//			myRC.setIndicatorString(1,  myLoc +" broadcasting "+targetSW);
//			return;
//		}
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
