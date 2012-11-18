package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.WaitForChargingBehavior;
import lazer6.behaviors.WoutBuildReturnBehavior;
import lazer6.behaviors.WoutRushAwayBehavior;
import lazer6.behaviors.gotoLocationBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

public class WoutBuildStrategy extends Strategy {
	
	private int state = 0;
//	private int lastState = 0;
	
	private boolean suspendEnergonLeash = false;
	
	private final int WOUT_TOLERANCE_FACTOR = 15;
	private final double MIN_WOUT_ENERGON = 5.0;
	private final double READY_WOUT_ENERGON = 25.0;
	
	//states
	private final int RUSH_AWAY = 0;
	private final int RETURN = 1;
	private final int GOTO_BUILD = 2;
	private final int BUILD = 3;
	
	
	private Behavior RushAway, Return, WaitForCharging, GotoBuild;
	private Instinct Transfer;
	
	private MapLocation hullCenter = new MapLocation(1,1);
	//build location is where (in a given round) the wout is told to build a tower.  Assigned when receive
	//a BUILDTOWERHERE broadcast which is sent by the runBroadcast() method in NewTowerStrategy
	private MapLocation buildLocation = null;

	/**
	 * constructor for WoutBuildStrategy
	 * @param player
	 */
	public WoutBuildStrategy(RobotPlayer player) {
		super(player);
		RushAway = new WoutRushAwayBehavior(player);
		Return = new WoutBuildReturnBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);
		Transfer = new TransferInstinct(player);
//		FindStrongerWout = new FindStrongerWoutBehavior(player);
//		EqualizeFlux = new EqualizeFluxBehavior(player);
		GotoBuild = new gotoLocationBehavior(player, hullCenter);
		myProfiler.setScanMode(true, false, false, true);
	}
	
	/**
	 * Overloaded constructor for WoutBuildStrategy
	 * @param player
	 * @param Center - location of hull center
	 */
	public WoutBuildStrategy(RobotPlayer player, MapLocation Center) {	//overloaded constructor to take hullCenter as an argument
		super(player);
		RushAway = new WoutRushAwayBehavior(player);
		Return = new WoutBuildReturnBehavior(player);
		WaitForCharging = new WaitForChargingBehavior(player);
		Transfer = new TransferInstinct(player);
//		FindStrongerWout = new FindStrongerWoutBehavior(player);
//		EqualizeFlux = new EqualizeFluxBehavior(player);
		hullCenter = Center;
		GotoBuild = new gotoLocationBehavior(player, hullCenter);
		myProfiler.setScanMode(true, false, false, true);
	}
	@Override
	public boolean beginStrategy() {
		return WaitForCharging.execute();
	}

	@Override
	public void runBehaviors() {
		Message m;
		MsgType type;
		int i = 0;
		while (myRadio.inbox[i] != null) {
			m = myRadio.inbox[i];
			type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
			if (type == MsgType.MSG_HULLCENTER) {	//I don't think we're actually getting any of these messages as of now, but should be initialized in constructor
				hullCenter = m.locations[Broadcaster.firstData];
			}else if (type == MsgType.MSG_BUILDTOWERHERE) {	//reassigns buildLocation
				buildLocation = m.locations[Broadcaster.firstData];
			}
			i++;
		}
		//a failsafe so if we have not received any BUILDTOWERHERE broadcasts and have max flux, build a tower
		//this (i think) only applies to the first tower built because after we receive even 1 broadcast, buildLocation is no longer null.
		//some reason, if you initialize buildLocation=null at the beginning of runBehaviors(), then building code goes to shit, don't do that plz
		if(myRC.getFlux()>=4000){
			if (buildLocation==null || myRC.getLocation().distanceSquaredTo(buildLocation)>440) { //if don't know of any buildings, or more than 24 squars away
				player.myAct.spawn(RobotType.COMM);
			}
		}
		
		//check if wout needs to go back for charging
		if (state != RETURN && !suspendEnergonLeash) {
			int archonDistance = myRC.getLocation().distanceSquaredTo(player.myProfiler.alliedArchons[0]);
			double remainingEnergon = myRC.getEnergonLevel();
			if ((remainingEnergon < ((Math.sqrt(archonDistance)+WOUT_TOLERANCE_FACTOR) * RobotType.WOUT.energonUpkeep()*5))/* || (enemySighted)*/) {
				state = RETURN;
			}
		}
		
		switch(state) {
		case RUSH_AWAY:
			//goes out to find flux, and avoids enemies while at it
			RushAway.execute();
			//we recharge a tower if we happen to be adjacent to a tower at any time in our rush_away
			if(nextTo(RobotType.COMM) || nextTo(RobotType.AURA) || nextTo(RobotType.TELEPORTER)){
				rechargeTower();
			}
			if(myRC.getFlux()>3100){
				state = GOTO_BUILD;
			}
			
			//else stay in state
//			lastState = RUSH_AWAY;
			break;
		case RETURN:
//			lastState = RETURN;
			Return.execute();
			equalizeFlux();
			if(myRC.getEventualEnergonLevel() >= READY_WOUT_ENERGON){
				state = RUSH_AWAY;
			}
			if (myRC.getFlux() > 3100 && myRC.getEnergonLevel() > MIN_WOUT_ENERGON && buildLocation != null && myRC.getLocation().distanceSquaredTo(buildLocation) <= 9 ) {
				state = GOTO_BUILD;
				suspendEnergonLeash = true;
			} else {
				suspendEnergonLeash = false;
			}
			break;
		case GOTO_BUILD:
			//go to location that we are told to go, or if we are not within broadcasting range of a tower, go to the known hullcenter location
			//(which is currently assigned by the constructor because it is not reassigned anywhere, but it works)
			if(buildLocation!=null){
				GotoBuild = new gotoLocationBehavior(player, buildLocation);
			}else{
				GotoBuild = new gotoLocationBehavior(player, hullCenter);
			}
			//recharge a tower if we walk near one
			if(nextTo(RobotType.COMM) || nextTo(RobotType.AURA) || nextTo(RobotType.TELEPORTER)){
				rechargeTower();
			}
			//gotobuild returns true if the space in front of robot is the given location
			if(GotoBuild.execute()){
				if(myRC.getFlux()>3100){
					state = BUILD;
				}else{
					state = RUSH_AWAY;
				}
			}
			if(myRC.getEnergonLevel() < MIN_WOUT_ENERGON){
				state = RETURN;
			}
//			lastState = GOTO_BUILD;
			break;
		case BUILD:
			if(player.myAct.spawnInPlace(RobotType.COMM)){
				state = RUSH_AWAY;
				suspendEnergonLeash = false;
				
				if(myRC.getFlux() > 3100){
					state = GOTO_BUILD;
				}
				
			}
			if(myRC.getEnergonLevel() < MIN_WOUT_ENERGON){
				state = RETURN;
			}
//			lastState = BUILD;
			break;
		}
		
		//Clearing the adjacent robot array///////////////////
		myProfiler.adjacentGroundRobotInfos = new RobotInfo[10];
	}
	
	/**
	 * returns true if robot is next to a robot of specified type
	 * @param type - type of robot that we are checking is adjacent
	 * @return true if there is a robot of specified type adjacent to self
	 */
	private boolean nextTo(RobotType type){
		MapLocation myLoc = myRC.getLocation();
		MapLocation nearest;
		//if archon
		if(type.ordinal()==0){
			nearest = player.myProfiler.closestAlliedArchon;
			if(myLoc.equals(nearest) || myLoc.isAdjacentTo(nearest)) return true;
		}
		else if(type.ordinal()>4){
			RobotInfo closestTower = player.myProfiler.closestAlliedTowerInfo;
			if(closestTower == null) return false;
			if(myLoc.equals(closestTower.location) || 
					myLoc.isAdjacentTo(closestTower.location)) return true;
		}
		else{
			RobotInfo[] adjacentRobots = player.myProfiler.adjacentGroundRobotInfos;
			RobotInfo rinfo;
			for(int i=0; i<8; i++) {
				rinfo = adjacentRobots[i];
				if(rinfo!=null) {
					if (rinfo.type.equals(type)) return true;
				}
			}
		}
		return false;
	}
	
	private void rechargeTower(){
		RobotInfo closestTower = player.myProfiler.closestAlliedTowerInfo;
		if(closestTower==null) return;
		if (closestTower.energonLevel < 4000) {
			MapLocation nearbyTower = closestTower.location;
			double fluxSpace = (GameConstants.ENERGON_RESERVE_SIZE - closestTower.energonReserve)
					/ GameConstants.FLUX_TO_ENERGON_CONVERSION;
			double fluxToTransfer = Math.min(myRC.getFlux(), fluxSpace);
			try {
				if (nearbyTower.isAdjacentTo(myRC.getLocation()))
					myRC.transferFlux(fluxToTransfer, nearbyTower,
							RobotLevel.ON_GROUND);
			} catch (GameActionException e) {
//				System.out.println("Caught Exception: rechargeTower in WoutBuildStrategy");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Transfer flux to another wout that is stronger than self
	 */
	private void equalizeFlux() {
		RobotInfo[] adjacentRobots =  myProfiler.adjacentGroundRobotInfos;
		RobotInfo rinfo = null;
		RobotInfo strongestAdjacentWout = null;
		double rEnergon = -1;
		double highestEnergon = 0;
		for(int i=0; i<8; i++) {
			rinfo = adjacentRobots[i];
			if(rinfo!=null) {
				rEnergon = rinfo.energonLevel;
				if (rinfo.type == RobotType.WOUT && rEnergon > myRC.getEnergonLevel() && rinfo.flux < 3100) {
					if (rEnergon > highestEnergon) {
						strongestAdjacentWout = rinfo;
						highestEnergon = rEnergon;
					}
				}
			}
		}
		if (strongestAdjacentWout != null) {
			double fluxNeeded = 3100 - strongestAdjacentWout.flux;
			double fluxToTransfer = Math.min(myRC.getFlux(), fluxNeeded);
			try {
				myRC.transferFlux(fluxToTransfer, strongestAdjacentWout.location, RobotLevel.ON_GROUND);
			} catch (GameActionException e) {
//				System.out.println("Action Exception: equalize flux");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void runInstincts() {
		Transfer.execute();
	}

}
