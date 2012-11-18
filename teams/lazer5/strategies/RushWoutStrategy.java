package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.BuildCommBehavior;
import lazer5.behaviors.GoToBuildLocation;
import lazer5.behaviors.WaitForChargingBehavior;
import lazer5.behaviors.WoutRushReturnFluxBehavior;
import lazer5.behaviors.WoutRushTowerTransferBehavior;
import lazer5.communications.MsgType;
import lazer5.instincts.Instinct;
import lazer5.instincts.RevertedTransferInstinct;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class RushWoutStrategy extends Strategy {
	private int state = 0;
	private Behavior TowerTransfer, WaitForCharging, BuildTower, ReturnFlux, GoToTowerLoc; 
	private Instinct Transfer;
	
	private MapLocation targetArchon;
	
	public final double TOWER_FLUX_PERCENTAGE = 0.33;
	public final double WOUT_TOLERANCE_FACTOR = 3;
	public final int TOWER_INWARD_SEARCH_DISTANCE = 3;
	
	public Direction rushDir = player.myRC.getDirection();
	public MapLocation origLoc = player.myRC.getLocation();
	
	public MapLocation myLoc = player.myRC.getLocation();
	public double remainingEnergon = player.myRC.getEnergonLevel();
	public int tilesTraveled = 0;
	public boolean enemySighted = false;
	public MapLocation enemyLoc;
	public Robot enemy;
	public RobotInfo enemyInfo;
	public boolean towerSighted = false;
	public MapLocation towerLoc;
	public Robot tower;
	public RobotInfo towerInfo;
	public int distance = 0;
	public boolean fluxTransferred = false;
	public boolean towerFluxTransferred = false;
	public boolean enemyTowerSighted = false;
	public boolean enemyTowerKilled = false;
	public MapLocation enemyTowerLoc;
	public double enemyPoints = 0;
	public MapLocation hullTowerLoc;
	public MapLocation closestAlliedTowerLoc;
	public boolean firstTowerBuilt = false;
	public MapLocation previousTower = null;
	
	
	
	
	public RushWoutStrategy(RobotPlayer player) {
		super(player);
		ReturnFlux = new WoutRushReturnFluxBehavior(player);
		TowerTransfer = new WoutRushTowerTransferBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);
		BuildTower = new BuildCommBehavior(player);
		GoToTowerLoc = new GoToBuildLocation(player,MsgType.MSG_BUILDTOWERHERE);
		Transfer = new RevertedTransferInstinct(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		return WaitForCharging.execute();
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// TODO Auto-generated method stub
		
		/*
		 * states
		 * 0 = moving away from archon in random direction
		 * 1 = going to an allied tower
		 * 2 = refill allied tower
		 * 3 = return to archon
		 * 4 = dump extraneous flux to archon
		 * 5 =  move to build tower location
		 * 6 = build tower
		 */
		
		remainingEnergon = player.myRC.getEnergonLevel();//re check energon level and refresh location
		myLoc = player.myRC.getLocation();
		
		
		MapLocation[] AlliedArchons = player.myRC.senseAlliedArchons(); //locate builder archons
		if (AlliedArchons.length >= 2) {
			if (myLoc.distanceSquaredTo(AlliedArchons[0]) <= myLoc.distanceSquaredTo(AlliedArchons[1])) {
				targetArchon = AlliedArchons[0];
			} else {
				targetArchon = AlliedArchons[1];
			}
		} else {
			targetArchon = AlliedArchons[0];
		}
		
		if (!((state==3)||(state==4))) {
			robotSearch();
		}
/*		if (player.myRC.getFlux()>3000 && (towerSighted || !(firstTowerBuilt)) && (state != 6)) {
			if (state != 5) {
				hullTowerLoc = closestAlliedTowerLoc;
				state = 5;
			}
		}*/
		if (!((state==3)||(state==4))) {//stuff that needs to be checked other than in the returning to archon states: enemy sighted, energon low, enough flux for a tower
			if ((remainingEnergon < ((tilesTraveled) * RobotType.WOUT.energonUpkeep()*5)+WOUT_TOLERANCE_FACTOR) || (enemySighted)) {
				state = 3;
			}
		}
		
		
		player.myRC.setIndicatorString(2, "state is " + state);
		
		switch(state) {
		case 0:
			player.myRC.setIndicatorString(1, "Wout rushing away " + rushDir);
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				player.myNavi.bugInDirection(rushDir);
				tilesTraveled++;
				distance = myLoc.distanceSquaredTo(origLoc);
			}
			
			if (towerSighted && (towerInfo.flux <= towerInfo.type.maxFlux()* TOWER_FLUX_PERCENTAGE)/* && (towerLoc != previousTower)*/) {
				state = 1;
			}
			
			break;
		case 1:
			player.myRC.setIndicatorString(1, "wout going to allied tower loc");
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				player.myNavi.bugTo(towerLoc);
				tilesTraveled++;
				distance = myLoc.distanceSquaredTo(origLoc);
			}
			if (myLoc.distanceSquaredTo(towerLoc) <= 1) {
				state = 2;
			}
			break;
		case 2:
			((WoutRushTowerTransferBehavior) TowerTransfer).setTowerInfo(towerInfo);
			if (TowerTransfer.execute()) {
				state = 0;
				previousTower = towerLoc;
			} else {
				state = 1;
			}
			
			break;
		case 3:
			player.myRC.setIndicatorString(1, "wout going to archon");
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				player.myNavi.bugTo(targetArchon);
			}
//			if (towerSighted && (towerInfo.flux <= towerInfo.type.maxFlux()* TOWER_FLUX_PERCENTAGE)/* && (towerLoc != previousTower)*/) {
//				state = 1;
//			}
			if (myLoc.distanceSquaredTo(targetArchon) <= 1) {
				state = 4;
				player.myRadio.sendSingleNumber(MsgType.MSG_ENEMYPOINTS, (int) enemyPoints);
			}
			break;
		case 4:
			((WoutRushReturnFluxBehavior) ReturnFlux).setTargetArchon(targetArchon);
			if (ReturnFlux.execute()) {
				if (!(enemyTowerLoc==null)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_ENEMYTOWER, enemyTowerLoc);
				} else if (!(enemyLoc==null)) {
					player.myRadio.sendSingleDestination(MsgType.MSG_ENEMYHERE, enemyLoc);
				}
				
				enemyTowerLoc = null;
				enemyLoc = null;
				origLoc = myLoc;
				tilesTraveled = 0;
				fluxTransferred = false;
				previousTower = null;
				//rushDir = player.myUtils.randDir();
				rushDir = fluxDirection();
				distance = 0;
				state = 0;
			}
			else {
				state = 3;
			}
			break;
/*		case 5:
			
			 * check locations around tower
			 
			if (!(firstTowerBuilt)) {
			//	System.out.println("hi");
				state = 6;
			} else if (GoToTowerLoc.execute()) {
				state = 6;
			}
			
			MapLocation buildTarget = towerSpotLocator();
			if (buildTarget != null) {
				player.myNavi.bugTo(buildTarget);
			} else {
				state = 3;
			}
			if (myLoc.distanceSquaredTo(buildTarget) <= 1) {
				state = 6;
			}
			break;
		case 6:
			if (BuildTower.execute()) {
				state = 0;
				firstTowerBuilt = true;
			}
			break;*/
		}
		
		
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		Transfer.execute();
	}
	
	/**
	 * searches through the robots thats the wout can sense and determines the closest enemy, tower points, weakest allied tower, and closest allied tower
	 * @param none
	 * @return none
	 * @throws GameActionException
	 */
	public void robotSearch() throws GameActionException {
		Robot[] nearby = player.myIntel.getNearbyRobots();
		Robot closest = null;
		float lowestDist = 9999;
		myLoc = player.myRC.getLocation();
		enemySighted = false;
		enemyTowerSighted = false;
		
		Robot weakest = null;
		double lowestE = 9999;
		towerSighted = false;
		
		Robot closestTower = null;
		float lowestTowerDist = 9999;
	
		
		for (Robot r:nearby) {
			RobotInfo info = player.myRC.senseRobotInfo(r);
			if (info.team == player.myOpponent) {
				if (!(isTower(info))) {
					if (myLoc.distanceSquaredTo(info.location) < lowestDist) {
						closest = r;
						lowestDist = myLoc.distanceSquaredTo(info.location);
					}
				} else {
					enemyTowerSighted = true;
					enemyTowerLoc = info.location;
					enemyPoints = player.myRC.senseTeamPoints(r);
				}
					
			} else {
				if (isTower(info)) {
					if (info.energonLevel < lowestE) {
						weakest = r;
						lowestE = info.energonLevel;
					}
					if (myLoc.distanceSquaredTo(info.location) < lowestTowerDist) {
						closestTower = r;
						lowestTowerDist = myLoc.distanceSquaredTo(info.location);
					}
				}
			}
		}
		enemy = closest;
		enemySighted = (!(enemy==null));
		if (enemySighted) {
			enemyInfo = player.myRC.senseRobotInfo(enemy);
			enemyLoc = enemyInfo.location;
		}
		tower = weakest;
		towerSighted = (!(tower == null));
		if (towerSighted) {
			towerInfo = player.myRC.senseRobotInfo(tower);
			towerLoc = towerInfo.location;
		}
		
		if (closestTower != null) {
			closestAlliedTowerLoc = player.myRC.senseRobotInfo(closestTower).location;
		}
	}
	public boolean isTower(RobotInfo info) throws GameActionException{
		if (info == null) return false;
		if ((info.type==RobotType.COMM) || (info.type==RobotType.AURA) || (info.type==RobotType.TELEPORTER)) {
			return true;
		}
		return false;
	}
	public MapLocation towerSpotLocator() throws GameActionException {
		int xLoc = hullTowerLoc.getX();
		int yLoc = hullTowerLoc.getY();
		boolean checkNorth = true;
		boolean checkEast = true;
		boolean checkSouth = true;
		boolean checkWest = true;
		for(int i=TOWER_INWARD_SEARCH_DISTANCE; i>=3; i--){
			MapLocation target1 = new MapLocation(xLoc, yLoc-distance); //north
			MapLocation target2 = new MapLocation(xLoc+distance, yLoc);//east
			MapLocation target3 = new MapLocation(xLoc, yLoc+distance);//south
			MapLocation target4 = new MapLocation(xLoc-distance, yLoc);//west
			
			
			if (canSpawnRobot(target1) && checkNorth) {
				return target1;
			} else if (canSpawnRobot(target2) && checkEast) {
				return target2;
			} else if (canSpawnRobot(target3) && checkSouth) {
				return target3;
			} else if (canSpawnRobot(target4) && checkWest) {
				return target4;
			}
			
			//if target locations are invalid, make sure they're not invalid because towers are there
			//we don't want to decrease radius if this is the case because then we'd be creating unnecessary
			//redundancy.
			if(locationHasTower(target1))checkNorth=false;
			else if(locationHasTower(target2)) checkEast=false;
			else if(locationHasTower(target3)) checkSouth=false;
			else if(locationHasTower(target4)) checkWest=false;
		}
		return null;
	}
	private boolean locationHasTower(MapLocation loc) throws GameActionException{
		Robot rob = player.myRC.senseGroundRobotAtLocation(loc);
		if(player.myRC.senseRobotInfo(rob).type == RobotType.COMM) return true;
		return false;
	}
	
	private boolean canSpawnRobot(MapLocation target) throws GameActionException{
		if(player.myRC.senseGroundRobotAtLocation(target)==null && 
				player.myRC.senseTerrainTile(target).getType() == TerrainType.LAND){
			return true;
		}
		return false;
	}
	public Direction fluxDirection() throws GameActionException{
		int highestFlux = 0;
		Direction dir = Direction.NONE;
		for (int i = 0; i < 8; i++) {
			int flux = 0;
			if (i%2==0) {
				flux = player.myRC.senseFluxAtLocation(myLoc.add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]));
			} else {
				flux = player.myRC.senseFluxAtLocation(myLoc.add(Direction.values()[i]).add(Direction.values()[i]).add(Direction.values()[i]));
			}
			
			if (flux > highestFlux) {
				highestFlux = flux;
				dir = Direction.values()[i];
			}
		}
		if (dir != Direction.NONE) {
			return dir;
		} else {
			return player.myUtils.randDir();
		}
	}
}