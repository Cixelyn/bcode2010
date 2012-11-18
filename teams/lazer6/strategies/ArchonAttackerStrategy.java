package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.SpawnChargeBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class ArchonAttackerStrategy extends Strategy {
	private int state = 0;
	private int ID;

	private SpawnChargeBehavior Charge;
	private Instinct Transfer;
	private Direction rushDir = myRC.getDirection();
	private boolean chargeComplete = true;
	private MapLocation spawnLoc;
	private double energonLevel;
	private boolean Spawning = false;
	private boolean needsToMove = false;
	private int roundsSpawning = 0;
	private boolean directionNeeded = false;
	private boolean enemySearch = true;
	private MapLocation Destination;
	private MapLocation lastEnemyLoc;
	private int dangerRadius;
	
	private final int ENEMY_COUNT_THRESHOLD = 5;
	private final double SPAWN_THRESHOLD = 50.0;
	private final int ROUNDS_TO_TRY_SPAWN = 3;
	//private final double CHAINERS_PER_ARCHON = 2.0;

	private MapLocation myLoc;

	public ArchonAttackerStrategy(RobotPlayer player) {
		super(player);
		Charge = new SpawnChargeBehavior(player);
		Transfer = new TransferInstinct(player);
		myProfiler.setScanMode(true, true, false, true);
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
		MapLocation[] archonList = myProfiler.alliedArchons;
		MapLocation leadArchon = archonList[archonList.length-1];
		ID = myProfiler.myArchonID;
		
		//check if needs to become leader or build archon
		if (ID == 0) {
			player.changeStrategy(new ArchonBuilderStrategy(player));
			return;
		}
		if (ID == archonList.length-1) {
			player.changeStrategy(new ArchonLeaderStrategy(player));
			return;
		}
		
        Robot[] nearby = player.myProfiler.nearbyGroundRobots;
        RobotInfo rInfo = null;
        RobotInfo[] farAway = new RobotInfo[30];
        int counter = 0;
        for (int i = 0; i < nearby.length; i++) {
              try {
              rInfo = myRC.senseRobotInfo(nearby[i]);
              if (rInfo.team == player.myOpponent && myLoc.distanceSquaredTo(rInfo.location) >= 1) {
                    farAway[counter] = rInfo;
                    counter++;
              } 
              } catch (Exception e) {
                    e.printStackTrace();
              }
        }
        nearby = player.myProfiler.nearbyAirRobots;
		for (int i = 0; i < nearby.length; i++) {
			try {
				rInfo = myRC.senseRobotInfo(nearby[i]);
				if (rInfo.team == player.myOpponent && myLoc.distanceSquaredTo(rInfo.location) >= 1) {
					farAway[counter] = rInfo;
					counter++;
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        if (farAway[0] != null) {
        	player.myRadio.sendRobotList(farAway);
        	/*lastEnemyLoc = myProfiler.closestEnemy.location;
			state = 1;*/
        }
        RobotInfo localEnemy = realClosestEnemy();
        if (localEnemy != null) {
			lastEnemyLoc = localEnemy.location;
			state = 1;
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
			if (type == MsgType.MSG_ITSRAPINGTIME) {
				state = 1;
				Destination = m.locations[Broadcaster.firstData];
				lastEnemyLoc = Destination;
				enemySearch = false;
			}
			i++;
		}
		
		
		/*if (enemySearch) {
			if (myProfiler.closestEnemy != null) {
				myRadio.sendSingleDestination(MsgType.MSG_ENEMYHERE,
						myProfiler.closestEnemy.location);
				lastEnemyLoc = myProfiler.closestEnemy.location;
				state = 1;
			}
		}*/
		
		/*
		 * states
		 * 0 move spawn and charge searching for enemy
		 * 1 enemy found FIGHT (follow lead archon)
		 */
		myRC.setIndicatorString(2, "chargeComplete: " + chargeComplete + " Spawning: " + Spawning + " rushDir: " + rushDir.toString() + " needsToMove: " + needsToMove + " state: " + state);
		switch(state) {
		case 0:
			if (chargeComplete) {
				if (!Spawning) {
					if (myAct.moveInDir(myNavi.bugTo(leadArchon/*myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir)*/))) needsToMove = false;
				}
			} else {
				Direction bugToLead = myNavi.bugTo(leadArchon);
				if (myLoc.add(bugToLead).distanceSquaredTo(spawnLoc) <= 2) {
					if (myAct.moveInDir(bugToLead/*myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir)*/)) needsToMove = false;
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

			
			break;
			
		
		//////////////////////////////////KILL CODE////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////

		

			
			
		case 1:
			
			Direction attackVector = myProfiler.dirToEnemyCoM(); //vector calculations
			Direction retreatVector = attackVector.opposite();
			
			
			//Direction moveVector = myNavi.bugTo(myNavi.archonFormation());
			Direction moveVector = myNavi.bugTo(myProfiler.alliedArchons[myProfiler.alliedArchons.length-1]);
			

			
			if (chargeComplete) {				
				if (!Spawning) {													//if not spawning
					if (myLoc.distanceSquaredTo(lastEnemyLoc) > dangerRadius) {
						if (myAct.moveLikeMJ(attackVector,moveVector)) needsToMove = false;
					} else {
						if (myAct.moveLikeMJ(attackVector, retreatVector)) needsToMove = false;
					}
				}
				
			} else {
				if (myLoc.distanceSquaredTo(lastEnemyLoc) > dangerRadius) {
					Direction bugToLead = moveVector;
					if (myLoc.add(bugToLead).distanceSquaredTo(spawnLoc) <= 2) {
						if (myAct.moveInDir(bugToLead)) needsToMove = false;
					}
				} else {
					if (myAct.moveInDir(myLoc.directionTo(lastEnemyLoc).opposite())) needsToMove = false;
				}
				Charge.setSpawnedLoc(spawnLoc);
				chargeComplete = Charge.execute();
			}


			if (energonLevel > SPAWN_THRESHOLD && chargeComplete && !needsToMove) {
				if(shouldSpawnChainer()){
					Spawning = !myAct.spawn(RobotType.CHAINER);
				}else{
					Spawning = !myAct.spawn(RobotType.SOLDIER);
				}
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
			
			if (localEnemy != null) {
				lastEnemyLoc = localEnemy.location;
				dangerRadius = localEnemy.type.attackRadiusMaxSquared()+4;
			} else {
				dangerRadius = -1;
			}
			
			if (!enemySearch && myLoc.distanceSquaredTo(Destination) < 9 && localEnemy==null) {
				enemySearch = true;
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
/*	
	public int getID() {

		int myID = 0;
		for(int i=0; i<archonList.length; i++){
			MapLocation l = archonList[i];
			if(l==myRC.getLocation()) {
				break;
			}
			myID++;
		}
		return myID;

	}*/
}
