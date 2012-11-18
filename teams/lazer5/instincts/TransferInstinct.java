package lazer5.instincts;

import lazer5.RobotPlayer;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * This instinct is designed for units to transfer energon to other ground units.
 * Used to be inefficient, but now rewritten to use a massive amount of static variable calls.
 * 
 * Notes: Needs to be rebenchmarked later.
 * 
 * 
 * 	
 *	　    ＿＿＿_∧∧　　／￣￣￣￣￣￣￣￣￣￣￣￣￣　　　
 *	～'＿＿__(,,ﾟДﾟ)＜　GIVE ME SOME ENERGON!!!!! 
 *　           ＵU 　 　Ｕ U 　　 ＼＿＿＿＿＿＿＿＿＿＿＿＿＿
 * 
 * @author lazer pewpew
 *
 *
 */
public class TransferInstinct extends Instinct{
	private final double minEnergonLevel;
	private final double MIN_ARCHON_ENERGON = 40.0;
	private final double MIN_WOUT_ENERGON = 10.0;
	private final double MIN_CHAINER_ENERGON = 10.0;
	private final double MIN_SOLDIER_ENERGON = 10.0;
	private final double MIN_TURRET_ENERGON = 10.0;
	
	private final Team myTeam;
	private final RobotType myType;
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	//////////BEGIN ARRAY TABLE OF POSSIBLE TRANSFER LOCATIONS
	
	//variable instantiation for easy calling and debugging
	private final static Direction N = Direction.NORTH;
	private final static Direction NE = Direction.NORTH_EAST;
	private final static Direction E = Direction.EAST;
	private final static Direction SE = Direction.SOUTH_EAST;
	private final static Direction S = Direction.SOUTH;
	private final static Direction SW = Direction.SOUTH_WEST;
	private final static Direction W = Direction.WEST;
	private final static Direction NW = Direction.NORTH_WEST;
		
	//Directions that are within unit sensor range
	private final static Direction[] archonDirections = new Direction[]{N, NE, E, SE, S, SW, W, NW, Direction.NONE };
	private final static Direction[] woutDirections = new Direction[]{N, NE, E, SE, S, SW, W, NW};
	
	private final static Direction[] soldierN = new Direction[]{W,NW,N,NE,E};
	private final static Direction[] soldierNE = new Direction[]{NW,N,NE,E,SE};
	private final static Direction[] soldierE = new Direction[]{N,NE,E,SE,S};
	private final static Direction[] soldierSE = new Direction[]{NE,E,SE,S,SW};
	private final static Direction[] soldierS = new Direction[]{E,SE,S,SW,W};
	private final static Direction[] soldierSW = new Direction[]{SE,S,SW,W,NW};
	private final static Direction[] soldierW = new Direction[]{S,SW,W,NW,N};
	private final static Direction[] soldierNW = new Direction[]{SW,W,NW,N,NE};
	
	private final static Direction[] chainerN = new Direction[]{NW,N,NE};
	private final static Direction[] chainerNE = new Direction[]{N,NE,E};
	private final static Direction[] chainerE = new Direction[]{NE,E,SE};
	private final static Direction[] chainerSE = new Direction[]{NE,SE,S};
	private final static Direction[] chainerS = new Direction[]{SE,S,SW};
	private final static Direction[] chainerSW = new Direction[]{S,SW,W};
	private final static Direction[] chainerW = new Direction[]{SW,W,NW};
	private final static Direction[] chainerNW = new Direction[]{W,NE,N};
	
	/*
	 * Organization of unitTransferDirections
	 * Array with list of directions to check = [Unit][Given Direction]
	 */
	private final static Direction[][][] unitTransferDirections = new Direction[][][] {
		
		//North				NorthEast		East			SouthEast			South			SouthWest			West			NorthWest
		{archonDirections, archonDirections,archonDirections,archonDirections,archonDirections,archonDirections, archonDirections, archonDirections},
		{woutDirections,woutDirections,woutDirections,woutDirections,woutDirections,woutDirections,woutDirections,woutDirections}, //wouts
		{soldierN, soldierNE, soldierE, soldierSE, soldierS, soldierSW, soldierW, soldierNW}, //soldier
		{chainerN, chainerNE, chainerE, chainerSE, chainerS, chainerSW, chainerW, chainerNW}, //chainer
		{chainerN, chainerNE, chainerE, chainerSE, chainerS, chainerSW, chainerW, chainerNW}, //turret (same as chainer)
		{}, //comm
		{}, //teleporter
		{} //aura
	};
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	/////////////////INSTINCT CONSTRUCTOR AND CODE
	
	public TransferInstinct(RobotPlayer player){
		super(player);
		
		myType = player.myRC.getRobotType();
		myTeam = player.myRC.getTeam();
		
		
		//Set Minimum Energy Levels
		switch(myType) {
		case ARCHON:
			minEnergonLevel = MIN_ARCHON_ENERGON;
			break;
		case WOUT:
			minEnergonLevel = MIN_WOUT_ENERGON;
			break;
		case CHAINER:
			minEnergonLevel = MIN_CHAINER_ENERGON;
			break;
		case SOLDIER:
			minEnergonLevel = MIN_SOLDIER_ENERGON;
			break;
		case TURRET:
			minEnergonLevel = MIN_TURRET_ENERGON;
			break;
		default:
			minEnergonLevel = 0.0;
		}

		
	}


	public void execute() {
		
		//var initialization
		Robot r;
		RobotInfo info;
		double toTransfer;
		double freeEnergon = player.myRC.getEnergonLevel() - minEnergonLevel;
		
		
		//Early escape if not enough energon
		if (freeEnergon <= 0) return;
		
		//Directions i am allowed to transfer
		Direction myDir = player.myRC.getDirection();
		

		//Build the directions table
		//System.out.println(myType.ordinal()+":"+myDir.ordinal());
		Direction[] directions = unitTransferDirections[myType.ordinal()][myDir.ordinal()];
		
		for (int i = 0; i < directions.length; i++) {
			try{
				r = player.myRC.senseGroundRobotAtLocation(player.myRC.getLocation().add(directions[i]));
				if (r!=null) {
					info = player.myRC.senseRobotInfo(r);
					

						//if the robot is a ground unit, and is on my team
						if ((info.type.ordinal() < 5) && (info.team == myTeam)) {
							toTransfer = GameConstants.ENERGON_RESERVE_SIZE-info.energonReserve; //use to be energon needed
								if (toTransfer > freeEnergon) {
									toTransfer = freeEnergon;
								}
								player.myRC.transferUnitEnergon(toTransfer,	info.location, RobotLevel.ON_GROUND);
								freeEnergon -= toTransfer;
						}
				}
			} catch(GameActionException e) {
				//if we have hit this position, something went wrong.			
				e.printStackTrace();
			}
		}
		
	
		//transfer any remaining energon to self (if archon)
		if(myType==RobotType.ARCHON){
			try {
				toTransfer = GameConstants.ENERGON_RESERVE_SIZE - player.myRC.getEnergonReserve();
				if(toTransfer > freeEnergon){
					toTransfer = freeEnergon;
				}
				player.myRC.transferUnitEnergon(toTransfer, player.myRC.getLocation(), RobotLevel.IN_AIR);
				freeEnergon -= toTransfer;
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//////////END MAIN EXECUTE BLOCK
	////////////////////////////////////////////////////////////////////////////////////////


	
	
	/**
	 * return how much energon can be transferred to robot. Returns 0.0 if no robot, if
	 * robot is on other team or if robot is a friendly archon above the minArchonEnergon level
	 * 
	 * Note: this function has been inlined and is now obsolete ~ Cory
	 * 
	 * 
	 * @param info
	 * @return
	 */
	private double energonNeeded(RobotInfo info){
		if(info==null || info.team!=myTeam)return 0.0;  // unecessary defensive coding
		if(info.type == RobotType.ARCHON && info.energonLevel > MIN_ARCHON_ENERGON) return 0.0; //we are no longer transfering to air
		return (GameConstants.ENERGON_RESERVE_SIZE-info.energonReserve);
	}
	
	/**
	 * returns true if robot is adjacent to or on same square
	 * @param myLoc
	 * @param targetLoc
	 * @return 
	 */
	private boolean inTransferRange(MapLocation myLoc, MapLocation targetLoc){
		return (myLoc.isAdjacentTo(targetLoc) || myLoc.equals(targetLoc));
	}
}




//Large chunk of old code kept for benchmarking / archival purposes


/* IGNORE ARIAL TRANSFERS FOR NOW!!!!!!!!!!!!!!!!!!!!!!!!
 * also, when you go about updating arial transfers, use the optimizations found in the 
 * new ground transfer mechanics
for (int i = 0; i < 9; i++) {
	try {
		if (!(freeEnergon > 0)) break;
		if (player.myRC.canSenseSquare(myLoc.add(directions[i]))) {
			Robot r = player.myRC.senseAirRobotAtLocation(myLoc.add(directions[i]));
			if ((r != null)&&(!(r.equals(player.myRC.getRobot())))) {
				RobotInfo info = player.myRC.senseRobotInfo(r);
				RobotType type = info.type;
				if ((type!=RobotType.AURA && type!=RobotType.COMM && type!=RobotType.TELEPORTER) && (info.team == myTeam)) {
					double toTransfer = energonNeeded(info);
					if (toTransfer > 0.0) {
						if (toTransfer > freeEnergon) {
							toTransfer = freeEnergon;
						}
						MapLocation targetLoc = info.location;
						player.myRC.transferUnitEnergon(toTransfer,
								targetLoc, RobotLevel.IN_AIR);
						freeEnergon -= toTransfer;
					}
				}
			}
		}
	} catch (GameActionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println(Clock.getRoundNum());
	}
}
*/


/*	Robot r;
RobotInfo info;
RobotType rtype;
MapLocation targetLoc;
for(int i=0; i<player.myIntel.getNearbyGroundRobots().length; i++){
	r = player.myIntel.getNearbyGroundRobots()[i];
	try{
		if (freeEnergon<=0.0) break;
		if (!player.myRC.canSenseObject(r)) break;
		info = player.myRC.senseRobotInfo(r);
		if(info!=null){
			rtype = info.type;
			if (rtype!=RobotType.AURA && rtype!=RobotType.COMM && rtype!=RobotType.TELEPORTER) {
				double toTransfer = energonNeeded(info);
				if (toTransfer > 0.0) {
					if (toTransfer > freeEnergon) {
						toTransfer = freeEnergon;
					}
					targetLoc = info.location;
					if (myLoc.isAdjacentTo(targetLoc)) {
						if (player.myRC.senseGroundRobotAtLocation(targetLoc) != null && player.myRC.hasActionSet() == false) {
							player.myRC.transferUnitEnergon(toTransfer,
									targetLoc, RobotLevel.ON_GROUND);
							freeEnergon -= toTransfer;
						}
					}
				}
			}
		}
		
	} catch (Exception e){
		e.printStackTrace();
	}
}

for(int i=0; i<player.myIntel.getNearbyAirRobots().length; i++){
	r = player.myIntel.getNearbyAirRobots()[i];
	try{
		if (freeEnergon<=0.0) break;
		if (!player.myRC.canSenseObject(r)) break;
		info = player.myRC.senseRobotInfo(r);
		if(info!=null){
			double toTransfer = energonNeeded(info);
			if (toTransfer > 0.0) {
				if (toTransfer > freeEnergon) {
					toTransfer = freeEnergon;
				}
				targetLoc = info.location;
				if (inTransferRange(myLoc, targetLoc)) {
					if (player.myRC.senseAirRobotAtLocation(targetLoc) != null) {
						player.myRC.transferUnitEnergon(toTransfer,
								targetLoc, RobotLevel.IN_AIR);
						freeEnergon -= toTransfer;
					}
				}
			}
		}
		
	} catch (Exception e){
		e.printStackTrace();
	}
}*/

















