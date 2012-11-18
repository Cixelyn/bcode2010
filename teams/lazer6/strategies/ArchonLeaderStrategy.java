package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.ArchonRetreatBehavior;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.SpawnChargeBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class ArchonLeaderStrategy extends Strategy {
	private int state = 0;
	private int ID;

	private SpawnChargeBehavior Charge;
	private Behavior Retreat;
	private Instinct Transfer;
	private Direction rushDir = myRC.getDirection();
	private int enemyPoints = 0;
	private boolean buildMode = false;
	private boolean chargeComplete = true;
	private MapLocation spawnLoc;
	private double energonLevel;
	private boolean Spawning = false;
	private boolean needsToMove = false;
	private int roundsSpawning = 0;
	private int dangerRadius;
	private boolean directionNeeded = false;
	private final double SPAWN_THRESHOLD = 50.0;
	private final int ROUNDS_TO_TRY_SPAWN = 3;
	private final double MIN_ARCHON_ENERGON = 10.0;
	private final int ENEMY_COUNT_THRESHOLD = 5;
	//private final double CHAINERS_PER_ARCHON = 7.0;
	//XXX tweak this
	
	
	private final static int ROUNDS_UNTIL_ATTACK_BORED = 150;
	private int timeAttacking;
	
	
	private MapLocation lastEnemyLoc;
	private MapLocation Destination;
	

	private MapLocation myLoc;

	public ArchonLeaderStrategy(RobotPlayer player) {
		super(player);
		Charge = new SpawnChargeBehavior(player);
		Retreat = new ArchonRetreatBehavior(player);
		Transfer = new TransferInstinct(player);
		myProfiler.setScanMode(true, true, true, true);
	}

	@Override
	public boolean beginStrategy() {
		boolean[] walls = archonMapEdgeFinder();
		if (walls[4] == true) {
			if ((walls[0]) && !(walls[1]) && !(walls[2]) && !(walls[3])) {
				rushDir = Direction.SOUTH_EAST;
			} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && !(walls[3])) {
				rushDir = Direction.NORTH_WEST;
			} else if (!(walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
				rushDir = Direction.NORTH_WEST;
			} else if (!(walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
				rushDir = Direction.SOUTH_EAST;
			} else if ((walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
				rushDir = Direction.SOUTH_WEST;
			} else if (!(walls[0]) && (walls[1]) && (walls[2]) && !(walls[3])) {
				rushDir = Direction.NORTH_WEST;
			} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && (walls[3])) {
				rushDir = Direction.NORTH_EAST;
			} else if ((walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
				rushDir = Direction.SOUTH_EAST;
			}

		} else {
			//WTF HALP code
			directionNeeded = true;
			myRadio.sendSingleNotice(MsgType.MSG_NEEDDIRECTION);
			
		}
		return true;
	}

	@Override
	public void runBehaviors() {
		energonLevel = myRC.getEnergonLevel();
		myLoc = myRC.getLocation();
		ID = myProfiler.myArchonID;
		
		if (ID == 0) {
			player.changeStrategy(new ArchonBuilderStrategy(player));
			return;
		}
		
		
		Robot[] nearby = player.myProfiler.nearbyGroundRobots;
		RobotInfo rInfo = null;
		RobotInfo[] farAway = new RobotInfo[30];
		int counter = 0;
		if (myProfiler.enemyAirInRange < myProfiler.enemyAttackersInRange) {
			for (int i = 0; i < nearby.length; i++) {
				try {
					rInfo = myRC.senseRobotInfo(nearby[i]);
					if (rInfo.team == player.myOpponent && myLoc.distanceSquaredTo(rInfo.location) >= 9) {
						farAway[counter] = rInfo;
						counter++;
					} 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			nearby = player.myProfiler.nearbyAirRobots;
			for (int i = 0; i < nearby.length; i++) {
				try {
					rInfo = myRC.senseRobotInfo(nearby[i]);
					if (rInfo.team == player.myOpponent && myLoc.distanceSquaredTo(rInfo.location) >= 9) {
						farAway[counter] = rInfo;
						counter++;
					} 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (farAway[0] != null) {
			if (Clock.getRoundNum()%3 == 0) {
				player.myRadio.sendRobotList(farAway);
			}
		}

		
		Message m;
		MsgType type;
		int i = 0;
		while (myRadio.inbox[i] != null) {
			m = myRadio.inbox[i];
			type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
			if (type == MsgType.MSG_NEEDDIRECTION) {
				if (!(directionNeeded)) {
					myRadio.sendSingleInt(MsgType.MSG_DIRECTIONREPLY, rushDir.ordinal());
				}
			}
			if (type == MsgType.MSG_DIRECTIONREPLY) {
				if (directionNeeded) {
					rushDir = Direction.values()[m.ints[Broadcaster.firstData]];
					directionNeeded = false;
				}
			}
			if (type == MsgType.MSG_ENEMYLOCPOINTS) {
				enemyPoints = m.ints[Broadcaster.firstData];
			}
			if (type == MsgType.MSG_BUILDMODE) {
				//move to hull center and defend
			}
			/*if (type == MsgType.MSG_ENEMYHERE) {
				lastEnemyLoc = m.locations[Broadcaster.firstData];
				Destination = lastEnemyLoc;
				state = 1;
				myRadio.sendSingleDestination(MsgType.MSG_ITSRAPINGTIME, lastEnemyLoc);
			}*/
			i++;
		}
		
		
		/*
		 * states
		 * 0 move spawn and charge searching for enemy
		 * 1 enemy found KILL
		 * 2 RETREAT
		 */
		
		myRC.setIndicatorString(2, "chargeComplete: " + chargeComplete + " Spawning: " + Spawning + " rushDir: " + rushDir.toString() + " needsToMove: " + needsToMove + " state: " + state);
		
		
		switch(state) {
		
		
		////////////////////////////////MOVE SPAWN CHARGE AND SEARCH FOR ENEMY///////////////////////////////////
		case 0:   ///////////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			//broadcast location 5 squares in front of self for other units to swarm to
			if(Clock.getRoundNum() % 10 == 0){
				Direction myDir = player.myRC.getDirection();
				MapLocation forwardLocation = myLoc.add(myDir).add(myDir);
				player.myRadio.sendSingleDestination(MsgType.MSG_SWARMLOCATION, forwardLocation);
			}
			
			if (chargeComplete) {
				if (!Spawning) {
					if (myAct.moveInDir(myNavi.bugTo(myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir)))) needsToMove = false;
				}
			} else {
				if (myLoc.add(rushDir).distanceSquaredTo(spawnLoc) <= 2) {
					if (myAct.moveInDir(myNavi.bugTo(myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir)))) needsToMove = false;
				}
				Charge.setSpawnedLoc(spawnLoc);
				chargeComplete = Charge.execute();
			}


			if (energonLevel > SPAWN_THRESHOLD && chargeComplete && !needsToMove) {
				/*if(shouldSpawnChainer()){
					Spawning = !myAct.spawn(RobotType.CHAINER);
				}else{
					Spawning = !myAct.spawn(RobotType.SOLDIER);
				}*/
				Spawning = !myAct.spawn(RobotType.CHAINER);
				if (!Spawning) {
					chargeComplete = false;
					spawnLoc = myLoc.add(myRC.getDirection());
				} else {
					roundsSpawning++;
					if (roundsSpawning > ROUNDS_TO_TRY_SPAWN) {
						Spawning = false;
						roundsSpawning = 0;
						needsToMove = true;
					}
				}
			} else {
				Spawning = false;
			}


			if (myRC.senseTerrainTile(myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir)).getType() == TerrainType.OFF_MAP) {
				boolean[] walls = archonMapEdgeFinder();					
				if ((walls[0]) && !(walls[1]) && !(walls[2]) && !(walls[3])) {
					if (rushDir.equals(Direction.NORTH_EAST)){
						rushDir = Direction.SOUTH_EAST;
					} else if (rushDir.equals(Direction.NORTH_WEST)) {
						rushDir = Direction.SOUTH_WEST;
					} else if (rushDir.equals(Direction.NORTH)) {
						rushDir = Direction.SOUTH;
					}

				} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && !(walls[3])) {
					if (rushDir.equals(Direction.SOUTH)) {
						rushDir = Direction.NORTH;
					} else if (rushDir.equals(Direction.SOUTH_WEST)) {
						rushDir = Direction.NORTH_WEST;
					} else if (rushDir.equals(Direction.SOUTH_EAST)) {
						rushDir = Direction.NORTH_EAST;
					}

				} else if (!(walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
					if (rushDir.equals(Direction.EAST)) {
						rushDir = Direction.WEST;
					} else if (rushDir.equals(Direction.SOUTH_EAST)) {
						rushDir = Direction.SOUTH_WEST;
					} else if (rushDir.equals(Direction.NORTH_EAST)) {
						rushDir = Direction.NORTH_WEST;
					}

				} else if (!(walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
					if (rushDir.equals(Direction.WEST)) {
						rushDir = Direction.EAST;
					} else if (rushDir.equals(Direction.SOUTH_WEST)) {
						rushDir = Direction.SOUTH_EAST;
					} else if (rushDir.equals(Direction.NORTH_WEST)) {
						rushDir = Direction.NORTH_EAST;
					}

				} else if ((walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
					rushDir = Direction.SOUTH_WEST;


				} else if (!(walls[0]) && (walls[1]) && (walls[2]) && !(walls[3])) {
					rushDir = Direction.NORTH_WEST;


				} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && (walls[3])) {
					rushDir = Direction.NORTH_EAST;


				} else if ((walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
					rushDir = Direction.SOUTH_EAST;
				}
			}
			RobotInfo localEnemy = realClosestEnemy();
			if (localEnemy!=null) {
				state = 1;
				timeAttacking = 0;
				//System.out.println("RAPING TIME");
				lastEnemyLoc = localEnemy.location;
				Destination = lastEnemyLoc;
				dangerRadius = localEnemy.type.attackRadiusMaxSquared()+1;
				myRadio.sendSingleDestination(MsgType.MSG_ITSRAPINGTIME, lastEnemyLoc);
			}
			
			break;
			
			
			
			
		/////////////////////////////////////////KILL CODE//////////////////////////////////////////////////////
		case 1://///////////////////////////////////////////////////////////////////////////////////////////////
			
			timeAttacking++;
			
			////////////////////STATE TRANSITIONS//////////////////
			if (energonLevel < MIN_ARCHON_ENERGON) {
				state = 2;
				break;
			}
			
			
			localEnemy = realClosestEnemy();
			if(localEnemy!=null) {
				dangerRadius = localEnemy.type.attackRadiusMaxSquared()+3;
				timeAttacking = 0;
			}else if(timeAttacking >ROUNDS_UNTIL_ATTACK_BORED) {
				state = 0;
				break;
			}
			
			
			myRC.setIndicatorString(1, "E/CoM: "+myProfiler.dirToEnemyCoM()+"");
			
			
			
			////////////////////OFFENSIVE CODE HERE///////////////
			Direction attackVector = myProfiler.dirToEnemyCoM(); //vector calculations
			Direction retreatVector = myProfiler.dirToEnemyCoM().opposite();

			
			
			//CHARGING CODE HERE//////////////
			if (chargeComplete) {  						//NOT CHARGING	
				if (!Spawning) {						//NOT TRYING TO SPAWNS
					
					
					if (localEnemy!=null && myLoc.distanceSquaredTo(localEnemy.location) > dangerRadius) {					//OUTSIDE DANGER RADIUS
						if (myAct.moveLikeMJ(attackVector,attackVector)) needsToMove = false;					//MOVE TOWARD LAST SEEN
					} else {																	//MOVE AWAY FROM ENEMY (WE ARE INSIDE DANGER RADIUS)
						if (myAct.moveLikeMJ(attackVector,retreatVector)) needsToMove = false;
					}
					
					
				}
			} else {																//ARE CHARGING UNIT
				if (localEnemy!=null && myLoc.distanceSquaredTo(localEnemy.location) > dangerRadius) {			//IF NOT IN DANGER RADIUS
								
					if (myLoc.add(attackVector).distanceSquaredTo(spawnLoc) <= 2) {	//IF MOVING TOWARD ENEMY IS OUT OF ADJACENCY
						if (myAct.moveLikeMJ(attackVector,attackVector)) needsToMove = false;		//IF NOT, THEN MOVE
					}
				} else {															//MOVE AWAY FROM THE ENEMY
					if (myAct.moveLikeMJ(attackVector,retreatVector)) needsToMove = false;
				}
				Charge.setSpawnedLoc(spawnLoc);										//CHARGING UNIT AND SHIT
				chargeComplete = Charge.execute();
			}
			
			
			


			//SPAWNING CODE HERE///////////////
			if (energonLevel > SPAWN_THRESHOLD && chargeComplete && !needsToMove) {		//CAN WE SPAWN?
				if(shouldSpawnChainer()){												//SHOULD SPAWN CHAINER
					Spawning = !myAct.spawn(RobotType.CHAINER);
				}else{
					Spawning = !myAct.spawn(RobotType.SOLDIER);
				}
				if (!Spawning) {														//NOT TRYING TO SPAWN, THEN NEED TO CHARGE
					chargeComplete = false;
					spawnLoc = myLoc.add(myRC.getDirection());							//SET SPAWN LOCATION
				} else {
					roundsSpawning++;
					if (roundsSpawning > ROUNDS_TO_TRY_SPAWN) {							//ELSE TRYING TO SPAWN BUT FAILS
						Spawning = false;
						roundsSpawning = 0;
						needsToMove = true;
					}
				}
			} else {								
				Spawning = false;
			}
			
			
			
			
			

			//FIXME add a state transition out here later.  Otherwise we always attack
			break;
			
			
			
		///////////////////////////////////////////RETREAT CODE/////////////////////////////////////////////////
		case 2://///////////////////////////////////////////////////////////////////////////////////////////////
			if (Retreat.execute()) {
				if (player.myProfiler.numAllies > player.myProfiler.enemiesInRange && energonLevel > MIN_ARCHON_ENERGON) {
					state = 0;
					rushDir = myRC.getDirection();
				}
			}
			break;
		}
	}


	@Override
	public void runInstincts() {
		Transfer.execute();
	}

	public boolean[] archonMapEdgeFinder() {
		MapLocation currentSquare = myRC.getLocation();
		boolean[] walls = new boolean[5];

		if (myRC.senseTerrainTile(currentSquare.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH)).getType() == TerrainType.OFF_MAP) {
			walls[0] = true;//north
			walls[4] = true;
		}
		if (myRC.senseTerrainTile(currentSquare.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH)).getType() == TerrainType.OFF_MAP) {
			walls[1] = true;//south
			walls[4] = true;
		}
		if (myRC.senseTerrainTile(currentSquare.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST)).getType() == TerrainType.OFF_MAP) {
			walls[2] = true;//east
			walls[4] = true;
		}
		if (myRC.senseTerrainTile(currentSquare.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST)).getType() == TerrainType.OFF_MAP) {
			walls[3] = true;//west
			walls[4] = true;
		}
		return walls;
	}
	
	/**
	 * Determines whether or not we should spawn a chainer by seeing if we have a sustainable number of chainers per archon in our range.
	 * #chainers - #archons*(sustainable number of chainers per archon)
	 * @return 
	 */
	public boolean shouldSpawnChainer(){
		/*double numChainers = myProfiler.numAlliedUnitsSensed[RobotType.CHAINER.ordinal()];
		double numArchons = myProfiler.numAlliedUnitsSensed[RobotType.ARCHON.ordinal()];
		if(numChainers - numArchons*CHAINERS_PER_ARCHON < CHAINERS_PER_ARCHON){
			return true;
		}
		return false;*/
		
		/*if (myProfiler.enemyAirInRange > myProfiler.enemyAttackersInRange) {
			return false;
		}*/
		if (myProfiler.enemyAirInRange==0 && myProfiler.enemyAttackersInRange == 0) {
			return true;
		}
		if (myProfiler.enemyAttackersInRange < ENEMY_COUNT_THRESHOLD) {
			return false;
		}
		return true;
	}
	
	public RobotInfo realClosestEnemy() {
		RobotInfo closestGround = myProfiler.closestEnemyInfo;
		RobotInfo closestAir = myProfiler.closestEnemyAirInfo;
		if (closestAir==null && closestGround==null) {
			return null;
		} else if (closestAir==null && closestGround!=null) {
			return closestGround;
		} else if (closestAir!=null && closestGround==null) {
			return closestAir;
		} else {
			if (myLoc.distanceSquaredTo(closestAir.location) < myLoc.distanceSquaredTo(closestGround.location)) {
				return closestAir;
			} else {
				return closestGround;
			}
		}
	}

}