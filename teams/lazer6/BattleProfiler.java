package lazer6;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;






/**
 * BATTLE PROFILER.  iT PROFILES BATTLES N SHIT.
 * <br>
 * <pre>
 *　  　　　　ミ　　　 ＼　　　　 | ﾐ 　 　
 *　　  　　　　　ミ　　　　＼⊂⊃ 　ﾐ　∠　　　BATTLE PROFILER SEES ALLL
 *　  　　　　　　　ミ　　　　∧＿∧　　　 ￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣
 *　   　 ミ￣￣￣￣￣＼（　・∀・）／￣￣￣彡
 *　  　 　 ミ　　　　　　　⊂ 　 †　⊃＿＿＿彡 
 *　　　　　￣ ￣￣￣／　| 　|　 | 　＼　　　　　
 *　　　　　　 　 　 ／ 　 （_＿）_,,）彡　＼　　　
 *　　　 　 　 　 ／　　　 ミ 　 　 　 彡　　＼　　　　 　
 *　　　　　　　 | 　　　ミ 　 　 　 　 　彡 　　|
 *　　　 　 　 　 ＼　ミ 　　　　　　　　彡　／ 　 　
 *　　　　　　 　 　 ＼ﾐ 　 　 　 　 　 彡／　
 *</pre>
 */
public class BattleProfiler {
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////VARIABLE DECLARATIONS AND STUFF/////////////////////////////////////
	
	
	/////////////////////////////////////MAIN SYSTEM CONTROLLERS
	RobotPlayer player;
	RobotController myRC;
	
	
	///////////////////////////////////MASTER LISTS (In case for some reason, they need to be accessed.
	public Robot[] nearbyGroundRobots;
	public RobotInfo[] nearbyGroundRobotInfos;
	
	public Robot[] nearbyAirRobots;
	public MapLocation[] alliedArchons;
	
	
	
	/////////////////////////////////////SENSOR INFORMATION RETURNS
	public RobotInfo closestEnemyInfo;			public RobotInfo closestEnemyTowerInfo;		public RobotInfo closestEnemyAirInfo; 
	public RobotInfo weakestEnemyInfo;			public RobotInfo weakestEnemyTowerInfo;		public RobotInfo weakestEnemyAirInfo;	
	public RobotInfo closestAlliedTowerInfo;	public RobotInfo weakestAlliedTowerInfo;
	public MapLocation closestAlliedArchon;		public RobotInfo closestAlliedGround;
	public RobotInfo maxAttRangeEnemy;
	
	public RobotData closestEnemyDBData;
	
	
	//These two variables are an extremely quick hack for soldiers.
	//Basically, give the closestGlobalEnemyRobot including both air and ground.  Prioritize air over ground.
	//Should clean this up later if we have time.
	public Robot closestGlobalEnemyRobot;
	public boolean closestGlobalEnemyRobotIsAir;
	
	
	
	///////////////////////////////////////UNIT COUNTING SYSTEMS
	public int numAllies = 0;
	public int numAllyAttackers = 0;
	public int enemiesInRange = 0; //includes towers
	public int enemyAttackersInRange = 0;
	public int enemyTowersInRange = 0;
	public int enemyAirInRange = 0;
	public double[] numUnitsSpawned = new double[8];		//not sure the most efficient way to keep these arrays, may want to combine later (spawned and allied)
	public double[] numAlliedUnitsSensed = new double[8];
	public double[] numEnemyUnitsSensed = new double[8];
	
	
	//////////////////////////////////////////TRANSFER INSTINCT STUFF
	public RobotInfo[] adjacentGroundRobotInfos = new RobotInfo[10]; //a list of allied robots, doesn't include towers
	public Robot[] adjacentGroundRobots = new Robot[10];
	public int[] adjacentGroundRobotsTTL = new int[10];
	public int[] adjacentGroundRobotsTimestamp = new int[10];
	
	
	////////////////////////////////////////STATUS VARIABLES
	public int myArchonID = 0;

	
	
	/////////////////////////////////////////ENEMY VARIABLES
	public double comEnemiesX=0; 	public double comEnemiesY=0;
	
	///////////////////////////////////////////SWARM VARIABLES AND CALCULATIONS
	//0 - No swarm
	//1 - Standard unit swarm
	//2 - Archon offensive swarm system
	
	private int swarmMode = 0; 
	
	public double comAlliesX;  public double comAlliesY;	
	public double alnAlliesX;  public double alnAlliesY;
	public double sepAlliesX;  public double sepAlliesY;
	
	public double comArchonsX; public double comArchonsY;
	public double alnArchonsX; public double alnArchonsY;
	public double sepArchonsX; public double sepArchonsY;
	public double dirArchonsX; public double dirArchonsY;
	
	
	public double repArchonsX; public double repArchonsY;
	
	/////////////////////////////////////////NEW ARCHON SWARM SY
	public MapLocation archonCoM=new MapLocation(0,0);
	public MapLocation oldArchonCoM = archonCoM;
	public Direction archonDifferential = Direction.OMNI;
	
	
	//////////////////////////////////////////TOGGLE SWITCHES
	public boolean scanGround = false; 
	public boolean scanAir = false;
	public boolean scanDB = false;
	public boolean scanArchons = false;
	public boolean isArchon = false;
	
	
	////////////////////////////////////////////Location Variables
	MapLocation myLastLoc;
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////BATTLE PROFILER SETUP//////////////////////////////////////////
	
	
	/**
	 * Constructor for the SensorDB System
	 * @param player - RobotPlayer parent
	 */
	public BattleProfiler(RobotPlayer player) {
		this.player = player;	
		this.myRC = player.myRC;
		
		if(myRC.getRobotType()==RobotType.ARCHON) {
			isArchon = true;
		}
		
	}
	
	/**
	 * Changes the mode of swarming (swarm is different for units like archons, etc.)
	 * Default swarm type is 0 (no swarm)
	 * 
	 * @param swarmMode
	 */
	public void switchSwarmMode(int swarmMode) {
		this.swarmMode = swarmMode;
	}
	
		
	/**
	 * Sets the profiling mode of the BattleProfiler.  The boolean flags designate what types of sensor information
	 * should be taken into account when generating the profile.
	 * @param scanGround whether the 
	 * @param scanAir whether the scan should take into account air units
	 * @param scanDB
	 */
	public void setScanMode(boolean scanGround, boolean scanAir, boolean scanDB, boolean scanArchons){
		this.scanGround = scanGround;
		this.scanAir = scanAir;
		this.scanDB = scanDB;
		this.scanArchons = scanArchons;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////MAIN SENSOR SWEEP//////////////////////////////////////////////
	/**
	 * Runs a sensor sweep and profiles all the variables that we need the robot to be aware of.
	 */
	public void sensorScan() {
		
		
		//VARIABLE REINITIALIZATION//////////////////////////////////////////////////////////////////////////////
		
		
		/////////////////////SELF INFORMATION///////////////////
		MapLocation myLoc = myRC.getLocation();
		Team myTeam = player.myTeam;
		
		
		///////////////////POSITION AND SWARM INFORMATION//////
		int tAlnAlliesX = 0; 				int tAlnAlliesY = 0;	 			//alignment
		int tSepAlliesX = 0; 				int tSepAlliesY = 0;				//separation
		int tComAlliesX = myLoc.getX(); 	int tComAlliesY = myLoc.getY();	//center of mass
		
		
		
		//////////////////////ENEMY LIST////////////////////////
		int tComEnemiesX = 0;		int tComEnemiesY = 0;			//center of mass
		
		
		
		///////////////////ARCHON SWARM INFORMATION/////////////
		alnArchonsX = 0;			alnArchonsY = 0;			//alignment
		sepArchonsX = 0;			sepArchonsY = 0;			//separation
		int tComArchonsX = 0;		int tComArchonsY = 0;			//center of mass
		
		int tRepArchonsX = 0;		int tRepArchonsY = 0;		//repulsion from enemies.
		
	
		
		///////////////////////SPECIFIC ROBOT FILTERS////////////
		closestEnemyInfo = null;				int enemyClosestDistance = 9999;
		weakestEnemyInfo = null;				double enemyWeakestEnergon = 9999;
		closestEnemyTowerInfo = null;			int enemyClosestTowerDistance = 9999;
		weakestEnemyTowerInfo = null;			double enemyWeakestTowerEnergon = 9999;
		closestAlliedTowerInfo = null;			int alliedClosestTowerDistance = 9999;
		weakestAlliedTowerInfo = null;			double alliedWeakestTowerEnergon = 9999;
		closestAlliedGround = null;			int closestAlliedGroundDistance = 9999;
		
		closestGlobalEnemyRobot = null;
		
		
		closestEnemyDBData = null;			int closestEnemyDBDistance = 9999;
		
		
		maxAttRangeEnemy = null;
		
		/////////////////////COUNTERS/////////////////////////////
		numAllies = 1; numAllyAttackers = 0;
		enemiesInRange = 0; enemyAttackersInRange = 0; 	enemyTowersInRange = 0;
		
		numAlliedUnitsSensed = new double[8];
		numEnemyUnitsSensed = new double[8];
		
			
		/////////////////////INFORMATION LISTS///////////////////
		if(myLoc!=myLastLoc) {
			adjacentGroundRobotInfos = new RobotInfo[10];
			adjacentGroundRobots = new Robot[10];
			adjacentGroundRobotsTTL = new int[10];
			adjacentGroundRobotsTimestamp = new int[10];
		}
		
		
		//Generic Variable Reassignment
		Robot r;
		RobotInfo rinfo;
		MapLocation rloc;
		RobotType rtype;
		
		
		
		

		//GROUND SENSING////////////////////////////////////////////////////////////////////////////////////
		if(scanGround) {
			//Main Sensing Call
			Robot[] groundRobots = myRC.senseNearbyGroundRobots();
			nearbyGroundRobots = groundRobots;
			nearbyGroundRobotInfos = new RobotInfo[groundRobots.length];

			int sensedDist;
			double sensedEnergon;
			Direction sensedDirection;
			Direction sensedDirectionTo;
			int maxAttRange = 0;
			
			for(int i=groundRobots.length; --i>=0;){
//			for(int i=0; i<groundRobots.length; i++){
				try {
					r = groundRobots[i];
					
					rinfo = myRC.senseRobotInfo(r);
					rtype = rinfo.type;
					rloc = rinfo.location;
					sensedDist = myLoc.distanceSquaredTo(rinfo.location);
					sensedEnergon = rinfo.energonLevel;

					nearbyGroundRobotInfos[i] = rinfo;
					
					if (rinfo.team != myTeam) { //if robot is an enemy
						enemiesInRange++;
		
						
						if (!(rtype == RobotType.WOUT || rtype.ordinal()>4)) {
							enemyAttackersInRange++;
							int attRange = rinfo.type.attackRadiusMaxSquared();
							if (attRange > maxAttRange) {
								maxAttRangeEnemy = rinfo;
								maxAttRange = attRange;
							}
							
							
							if(isArchon) {
								if(sensedDist < rtype.attackRadiusMaxSquared()+3) {
									
									sensedDirectionTo = rloc.directionTo(myLoc);
									
									tRepArchonsX += (sensedDirectionTo.dx);
									tRepArchonsY += (sensedDirectionTo.dy);
									
								}
							}
		
						}
						numEnemyUnitsSensed[rtype.ordinal()]++;
						if (enemyClosestDistance > sensedDist) {	//if enemy is closer than closest known enemy
							closestEnemyInfo = rinfo;
							enemyClosestDistance = sensedDist;
							
							closestGlobalEnemyRobot = r; //Also store this variable as the closestGlobalEnemyRobot
							closestGlobalEnemyRobotIsAir = false;
							
						}
						if (enemyWeakestEnergon > sensedEnergon) {	//if enemy is weaker than weakest known enemy
							weakestEnemyInfo= rinfo;
							enemyWeakestEnergon = sensedEnergon;
						}
						if(rtype.ordinal()>4){	//if enemy robot is a tower
							enemyTowersInRange++;
							if (enemyClosestTowerDistance > sensedDist){	//if tower is closer than closest known enemy tower
								closestEnemyTowerInfo = rinfo;
								enemyClosestTowerDistance = sensedDist;
							}
							if (enemyWeakestTowerEnergon > sensedEnergon){	//if tower is weaker than closest known weakest tower
								weakestEnemyTowerInfo = rinfo;
								enemyWeakestTowerEnergon = sensedEnergon;
							}
						} else{  //if enemy robot is a ground unit
							
							
							tComEnemiesX += rloc.getX();
							tComEnemiesY += rloc.getY();
		
							
						}
					}else{		//if robot is on our team
						numAlliedUnitsSensed[rtype.ordinal()]++;
						if(rinfo.type.ordinal() > 4){	//if robot is a tower
							if (alliedClosestTowerDistance > sensedDist){	//if allied tower is closer than closest known allied tower
								closestAlliedTowerInfo = rinfo;
								alliedClosestTowerDistance = sensedDist;
							}
							if (alliedWeakestTowerEnergon > sensedEnergon){	//if allied tower is weaker than closest known allied tower
								weakestAlliedTowerInfo = rinfo;
								alliedWeakestTowerEnergon = sensedEnergon;
							}
						} else{	//if not a tower, (AND on our team, AKA friendly unit) Do a bunch of ally calculations
							numAllies++;
							if (rinfo.type.ordinal() != 1) {
								numAllyAttackers++;
							}
							if(closestAlliedGroundDistance > sensedDist){
								closestAlliedGround = rinfo;
								closestAlliedGroundDistance = sensedDist;
							}

							/////////////////////////////////////////SWARM VARIABLE CALCULATIONS
							if (rinfo.type.ordinal() != 1) {
								if(swarmMode == 1) {

									//center of mass calculation-----------------
									tComAlliesX += rloc.getX();
									tComAlliesY += rloc.getY();

									//alignment vector calculation---------------
									sensedDirection = rinfo.directionFacing;
									if(sensedDirection.isDiagonal()) { //rescale for diagonal directions (1/sqrt(2))
										tAlnAlliesX += sensedDirection.dx * 0.71;
										tAlnAlliesY += sensedDirection.dy * 0.71;
									} else {
										tAlnAlliesX += sensedDirection.dx;
										tAlnAlliesY += sensedDirection.dy;
									}

									//separation vector calculation--------------
									if(sensedDist < swarmSeparationDistance) {
										Direction sepDir = rloc.directionTo(myLoc);

										if(sepDir.isDiagonal()) { //rescale for diagonal directions (1/sqrt(2))
											tSepAlliesX += sepDir.dx * 0.71;
											tSepAlliesY += sepDir.dy * 0.71;
										} else {
											tSepAlliesX += sepDir.dx;
											tSepAlliesY += sepDir.dy;
										}
									}
								}
							}


							////////////////////////////////////////TRANSFER INSTINCT CALCULATIONS
							if (sensedDist<=2){
								int dir = myLoc.directionTo(rloc).ordinal();
								adjacentGroundRobots[dir] = r;
								adjacentGroundRobotInfos[dir] = rinfo;
								adjacentGroundRobotsTTL[dir] = rinfo.roundsUntilMovementIdle;
								adjacentGroundRobotsTimestamp[dir] = Clock.getRoundNum();
							}
						}
					}

				}catch(GameActionException e) {
//					System.out.println("Exception: Ground Scanning Failed");
					e.printStackTrace();
				}
			}
		}
		


		//AIR SENSING/////////////////////////////////////////////////////////////////////////////////////////////
		
		if(scanAir) {
			
			//Air Specific Variables
			enemyAirInRange = 0;
			Robot[] airRobots = myRC.senseNearbyAirRobots();
			nearbyAirRobots = airRobots;
			
			
	
			//Initialize to null
			closestEnemyAirInfo = null; 	weakestEnemyAirInfo = null;
			
		
			try {
				for(int i=0; i<airRobots.length; i++){
					r = airRobots[i];
					rinfo = myRC.senseRobotInfo(r);
					rloc = rinfo.location;
					int dist = myLoc.distanceSquaredTo(rloc);
					double energon = rinfo.energonLevel;
					
					if(rinfo.team != player.myTeam){  			//Enemy archon not on my team
						enemyAirInRange++;
						int closestAirDist = 9999;
						double weakestAirEnergon = 9999;
						
						if(dist < closestAirDist){
							closestAirDist = dist;
							closestEnemyAirInfo = rinfo;
							
							closestGlobalEnemyRobot = r;
							closestGlobalEnemyRobotIsAir = true;
							
						}
						if(energon < weakestAirEnergon){
							weakestAirEnergon = energon;
							weakestEnemyAirInfo = rinfo;
						}
						
						
						
						tComEnemiesX += rloc.getX();				//Add to center of mass
						tComEnemiesY += rloc.getY();
						
						
						
					}else{										//Allied archon on my team
						/*
						if(swarmMode == 2) {					//Not utilized yet
						}*/						
					}			
				}
			} catch (GameActionException e) {
//				System.out.println("Exception: Air Scanning Failed");
				e.printStackTrace();
			}
		}
		
		
		
		
		//DATABASE SCANNING//////////////////////////////////////////////////////////////////
		if(scanDB) {
			
			
			int sensedDist;
			SensorDB myDB = player.myDB;
			
			
			myDB.resetPtr();
			while(myDB.hasNext()) {
				RobotData rdata = myDB.next();
				
				sensedDist = myLoc.distanceSquaredTo(rdata.location);
				
				if(sensedDist < closestEnemyDBDistance) {
					closestEnemyDBData = rdata;
					closestEnemyDBDistance = sensedDist;
				}
			}
		}
		
		
		

		
		//ARCHON SENSING//////////////////////////////////////////////////////////////////
		if(scanArchons) {
			alliedArchons = myRC.senseAlliedArchons();
			int numArchons = alliedArchons.length;
			int closestArchonDistance = 9999;	closestAlliedArchon = null;
			
			MapLocation loc;
			int archonDist;
			for(int i=numArchons; --i>=0;){
//			for(int i=0; i< numArchons; i++){ 	//find closestAlliedArchon
				loc = alliedArchons[i];
				archonDist = myLoc.distanceSquaredTo(loc);
				
				tComArchonsX += loc.getX();
				tComArchonsY += loc.getY();
				
				
				//archonID
				if(isArchon) {
					if(myLoc.equals(loc)) {
						myArchonID = i;
					}
				}
					
				//closestAlliedArchon
				if(archonDist < closestArchonDistance){
					closestAlliedArchon = loc;
					closestArchonDistance = archonDist;
				}
			}
			
			

			//Set Archons CoM
			comArchonsX = tComArchonsX/alliedArchons.length;
			comArchonsY = tComArchonsY/alliedArchons.length;	
			
	
			archonCoM = new MapLocation((int)comArchonsX,(int)comArchonsY);
			
			Direction dirToCoM = oldArchonCoM.directionTo(archonCoM);
			if(dirToCoM.ordinal()<8 && Clock.getRoundNum()%2==0) {
				archonDifferential = oldArchonCoM.directionTo(archonCoM);
				oldArchonCoM = archonCoM;
			}
						
			
		}
		
		
		//FINAL ASSIGNMENTS AND CALCULATIONS//////////////////////////////////////////////////

		//set our swarm variables
		comAlliesX = tComAlliesX/numAllies;
		comAlliesY = tComAlliesY/numAllies;
		alnAlliesX = tAlnAlliesX;
		alnAlliesY = tAlnAlliesY;
		sepAlliesX = tSepAlliesX;
		sepAlliesY = tSepAlliesY;
		
		
		repArchonsX = tRepArchonsX;
		repArchonsY = tRepArchonsY;
		
		//enemy center of mass
		int totalEnemies = (enemiesInRange+enemyAirInRange);
		if(tComEnemiesX!=0) { //only need one check.  Assume the Y will be zero too
			comEnemiesX = tComEnemiesX/totalEnemies;
			comEnemiesY = tComEnemiesY/totalEnemies;
		}

		
		//Store last location to check for timeouts
		myLastLoc = myLoc;
		
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////SWARM MOVEMENT COMPUTATIONS/////////////////////////////////////////
	
	//UNIT SWARMING SUBSYSTEM////////////////////////////////////////////////////////
	
	private static final int swarmSeparationDistance = 9; //Distance Squared
	private static final double swarmSeparationScale = 4.0;
	private static final double swarmAlignmentScale = 3.0;
	private static final double swarmCohesionScale =  9.0;
	private static final double swarmDestinationScale = 10.0;
	
	//TODO add more complex swarm behaviors.  Getting formations and such up and running would be very cool.
	/**
	 * Calculates direction in which to swarm.
	 * @return Direction to Swarm
	 */
	public MapLocation calculateSwarmUnitLocation(MapLocation destination) {
		
		MapLocation myLoc = player.myRC.getLocation();
		
		if(swarmMode == 1) {
			
			Direction destDir = myLoc.directionTo(destination);
			
			//Need to calculate cohesion vector from CoM
			double cohAlliesX = comAlliesX - myLoc.getX();
			double cohAlliesY = comAlliesY - myLoc.getY();
			
			//Now sum up all the vectors
			double swarmX = swarmCohesionScale * cohAlliesX + 
							swarmAlignmentScale * alnAlliesX +
							swarmSeparationScale * sepAlliesX +
							swarmDestinationScale * destDir.dx;
			
			double swarmY = swarmCohesionScale * cohAlliesY + 
							swarmAlignmentScale * alnAlliesY +
							swarmSeparationScale * sepAlliesY +
							swarmDestinationScale * destDir.dy;
			
			
			//player.myRC.setIndicatorString(0, "Coh: "+cohAlliesX+","+cohAlliesY);
			//player.myRC.setIndicatorString(1, "Aln: "+alnAlliesX+","+alnAlliesY);
			//player.myRC.setIndicatorString(2, "Sep: "+sepAlliesX+","+sepAlliesY);
			
			
			
			//TODO make this final return calculation a little more efficient
			return new MapLocation((int)(myLoc.getX()+swarmX),(int)(myLoc.getY()+swarmY));
		}

		//If swarm is enabled, we should not hit this point.
//		System.out.println("Error: Unit calling swarm without set mode!");
		return null;
	}
	


	
	///ARCHON SWARMING SUBSYSTEM///////////////////////////////////////////////////
	
	private static final double swarmArchonSeparationScale = 4.0;
	private static final double swarmArchonCohesionScale = 1.0;
	private static final double swarmArchonAlignmentScale = 7.0;
	//private static final double swarmArchonDestinationScale = 1.0;
	
	
	/**
	 * Calculates direction in which to swarm for Archons
	 * @return Direction to Swarm
	 */
	public MapLocation calculateSwarmArchonLocation() {
		
		MapLocation myLoc = player.myRC.getLocation();
		
		if(swarmMode == 2) {
			
			//Need to calculate cohesion vector from CoM
			double cohArchonsX = alliedArchons[alliedArchons.length-1].getX() - myLoc.getX();
			double cohArchonsY = alliedArchons[alliedArchons.length-1].getY() - myLoc.getY();
			
			
			//Now sum up all the vectors
			double swarmX = swarmArchonCohesionScale * cohArchonsX + 
							swarmArchonAlignmentScale * alnArchonsX +
							swarmArchonSeparationScale * sepArchonsX;
			
			double swarmY = swarmArchonCohesionScale * cohArchonsY + 
							swarmArchonAlignmentScale * alnArchonsY +
							swarmArchonSeparationScale * sepArchonsY;
			
//			player.myRC.setIndicatorString(0, "Coh: "+cohArchonsX+","+cohArchonsY);
//			player.myRC.setIndicatorString(1, "Aln: "+alnArchonsX+","+alnArchonsY);
//			player.myRC.setIndicatorString(2, "Sep: "+sepArchonsX+","+sepArchonsY);
			
			
			//TODO make this final return calculation a little more efficient
			return new MapLocation((int)(myLoc.getX()+swarmX),(int)(myLoc.getY()+swarmY));
		}

		//If swarm is enabled, we should not hit this point.
//		System.out.println("Error: Unit calling archon without set mode!");
		return null;
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////Positional Computations//////////////////////////////////////////
	/**
	 * Returns the direction to the LAST KNOWN enemy center of mass.
	 */
	public Direction dirToEnemyCoM() {
//		myRC.setIndicatorString(1, "CoM: "+comEnemiesX+","+comEnemiesY);
		return myRC.getLocation().directionTo(new MapLocation((int)comEnemiesX, (int)comEnemiesY));
	}
	
	/**
	 * Returns the location of the computed army center of mass.
	 * @return 
	 */
	public MapLocation locArmyCoM() {
		return new MapLocation((int)comAlliesX,(int)comAlliesY);
	}
	

	
	/**
	 * Calculates what direction to move to. We either move in the direction that the overall com of archons are moving
	 * or towards the com of archons if robot is too far away
	 * @return
	 */
	public Direction mobDirection(){
		myRC.setIndicatorString(2, archonDifferential+"");
		int distToArmy = myRC.getLocation().distanceSquaredTo(archonCoM);
		if(distToArmy <= 9){
			return archonDifferential;
		}else{
			return myRC.getLocation().directionTo(archonCoM);
		}
	}
	
	/**
	 * Experimental function that returns an archon's best heading during a battle
	 * @return direction for the archon to head in.
	 */
	public Direction dirArchonAttackVector() { //TODO inline all these if it works.
		
		MapLocation myLoc = myRC.getLocation();

		int myX = myLoc.getX();
		int myY = myLoc.getY();

		//Direction Vector = Enemy Vector + Army Vector + Retreat Vector
		int vecEnemiesX = (int)comEnemiesX - myX;
		int vecEnemiesY = (int)comEnemiesY - myY;
		

		
		int vecArmyX = (int)comAlliesX - myX;
		int vecArmyY = (int)comAlliesY - myY;
		
		
		int moveVecX = vecEnemiesX + vecArmyX + (int)repArchonsX*10;
		int moveVecY = vecEnemiesY + vecArmyY + (int)repArchonsY*10;
		
//		System.out.println("rep: "+repArchonsX+","+repArchonsY);
		
		
		return myLoc.directionTo(new MapLocation(myX+moveVecX,myY+moveVecY));
	}
	
	
	
	
	/**
	 * Returns whether the lead archon should advance forward
	 * @return
	 */
	public boolean shouldAdvance(MapLocation destLoc) {
		return isAhead(myRC.getLocation(), myRC.getLocation().directionTo(destLoc), locArmyCoM());
	}
	

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////UTILITY FUNCTIONS/////////////////////////////////////////////////////////
	
	
	public static boolean isBehind(MapLocation myLoc, Direction myDir, MapLocation oLoc) {
		Direction dirToSelf = oLoc.directionTo(myLoc);
		Direction myDirR = myDir.rotateRight();
		Direction myDirL = myDir.rotateLeft();
			
		if(dirToSelf==myDir || dirToSelf==myDirR || dirToSelf==myDirL) {
			return true;
		}
		return false;		
	}
	
	public static boolean isAhead(MapLocation myLoc, Direction myDir, MapLocation oLoc) {
		Direction dirToSelf = myLoc.directionTo(oLoc);
		Direction myDirR = myDir.rotateRight();
		Direction myDirL = myDir.rotateLeft();
			
		if(dirToSelf==myDir || dirToSelf==myDirR || dirToSelf==myDirL) {
			return true;
		}
		return false;	
	}
	
	public static boolean isToRight(MapLocation myLoc, Direction myDir, MapLocation oLoc) {
		Direction dirToSelf = oLoc.directionTo(myLoc);
		Direction myDirR = myDir.rotateRight().rotateRight();
		Direction myDirLofR = myDirR.rotateLeft();
		Direction myDirRofR = myDirR.rotateRight();
		
		if(dirToSelf==myDirR || dirToSelf==myDirLofR || dirToSelf==myDirRofR) {
			return true;
		}
		return false;	
	}
	
	public static boolean isToLeft(MapLocation myLoc, Direction myDir, MapLocation oLoc) {

		Direction dirToSelf = oLoc.directionTo(myLoc);
		Direction myDirL = myDir.rotateLeft().rotateLeft();
		Direction myDirRofL = myDirL.rotateLeft();
		Direction myDirLofL = myDirL.rotateRight();
		
		if(dirToSelf==myDirL || dirToSelf==myDirRofL || dirToSelf==myDirLofL) {
			return true;
		}
		return false;	
	}
	

	
	
}
