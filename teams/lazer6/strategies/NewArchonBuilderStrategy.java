package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.GoToBuildLocationBehavior;
import lazer6.behaviors.SpawnChargeBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

public class NewArchonBuilderStrategy extends Strategy {
	private int state = 0;
	private int ID;

	private SpawnChargeBehavior Charge;
	private Instinct Transfer;
	private MapLocation[] pathTraveled;
	private int pathCounter = 0;
	private MapLocation centerLoc;
	private boolean centerFound = false;
	private boolean woutMsgSent = false;
	private Direction rushDir = myRC.getDirection();
	private int enemyPoints = 0;
	private int buildMessageSent = 0;
	private boolean chargeComplete = true;
	private MapLocation spawnLoc;
	private double energonLevel;
	private double fluxLevel;
	private boolean Spawning = false;
	private boolean needsToMove = false;
	private boolean directionNeeded = false;
	private int roundsSpawning = 0;
	private boolean enemySearch = true;
	private MapLocation Destination;
	private MapLocation lastEnemyLoc;
	private int dangerRadius;
	
	private Behavior gotoBuildLocation;
	private int buildState = 0;
	private boolean buildMode = false;

	private final double SPAWN_THRESHOLD = 50.0;
	private final int ROUNDS_TO_TRY_SPAWN = 3;
	
	private final MapLocation startLoc;
	

	private MapLocation myLoc;

	public NewArchonBuilderStrategy(RobotPlayer player) {
		super(player);
		Charge = new SpawnChargeBehavior(player);
		gotoBuildLocation = new GoToBuildLocationBehavior(player);
		Transfer = new TransferInstinct(player);
		myProfiler.setScanMode(true, true, false, true);
		pathTraveled = new MapLocation[180];
		startLoc = myRC.getLocation();
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

		fluxLevel = myRC.getFlux();
		
		
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
        	//player.myRadio.sendRobotList(farAway);
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
			if (type == MsgType.MSG_ENEMYLOCPOINTS) {
				enemyPoints = m.ints[Broadcaster.firstData];
				if (enemyPoints > 500) {
					buildMode = true;
				}
			}
			i++;
		}
		
		if (directionNeeded && Clock.getRoundNum() > 3) {

			rushDir = myUtils.randDir();
			//System.out.println("sent lead dir");
			myRadio.sendSingleInt(MsgType.MSG_DIRECTIONREPLY, rushDir.ordinal());
			directionNeeded = false;

		}
		
		////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////I THINK THIS IS THE ONLY DIFFERENCE BETWEEN THE OLD BUILDER STRATEGY!!!!!!!!!!!!!//////////////////
///////|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||//////////////////////////////////////////////////////////////
///////vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
		//if our location is more than 10 squares away from where we started out, then switch to build mode
		if (myRC.getLocation().distanceSquaredTo(startLoc)>=100 && !buildMode) {
			buildMode = true;
		}
		////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//////////////////////////////////////////////////////////////////////
/////////||||||||||||||||||||||||||||||||||||||||||||||||||||||/////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////I THINK THIS IS THE ONLY DIFFERENCE BETWEEN THE OLD BUILDER STRATEGY!!!!!!!!!!!!!//////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
		
		if (buildMode && buildMessageSent < 2) {
			if (centerLoc != null) {
				myRadio.sendSingleDestination(MsgType.MSG_BUILDMODE, centerLoc);
			} else {
				centerLoc = myLoc;
				centerFound = true;
				myRadio.sendSingleDestination(MsgType.MSG_BUILDMODE, myLoc);
			}
			buildMessageSent++;
		}
		/*
		 * states
		 * 0 move spawn and charge searching for enemy
		 * 1 follow lead archon in combat mode
		 * 2 break off of swarm and build hulls
		 */
		switch(state) {
		case 0:
			if (!centerFound) {
				if (myRC.getRoundsUntilMovementIdle()>=6) {
					pathTraveled[pathCounter] = myLoc;
					pathCounter++;
				}
			}
			
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
				if (buildMode && !woutMsgSent) {
					if (centerLoc != null) {
						myRadio.sendSingleDestination(MsgType.MSG_BUILDERWOUTLOC, centerLoc);
					} else {
						centerLoc = myLoc;
						centerFound = true;
						myRadio.sendSingleDestination(MsgType.MSG_BUILDERWOUTLOC, myLoc);
					}
					woutMsgSent = true;
				}
			}

			
			if (fluxLevel > 3000.0 && chargeComplete && !needsToMove && !buildMode) {
				Spawning = !myAct.spawn(RobotType.AURA);
				if (Spawning) {
					roundsSpawning++;
					if (roundsSpawning > ROUNDS_TO_TRY_SPAWN) {
						Spawning = false;
						roundsSpawning = 0;
						needsToMove = true;
					}
				}
			} else if (energonLevel > SPAWN_THRESHOLD && chargeComplete && !needsToMove) {
				Spawning = !myAct.spawn(RobotType.WOUT);
				if (!Spawning) {
					chargeComplete = false;
					woutMsgSent = false;
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
		case 1:
			if (!centerFound) {
				int centerIndex = pathCounter-1;//TODO tweak this
				while (myRC.senseTerrainTile(pathTraveled[centerIndex]).getType() != TerrainType.LAND) {
					centerIndex--;
				}
				centerLoc = pathTraveled[centerIndex];
				centerFound = true;
			}
			
			//if only archon left stop following lead archon
			if (ID == archonList.length-1) {
				state = 0;
				return;
			}
			
			if (chargeComplete) {
				if (!Spawning) {
					if (myLoc.distanceSquaredTo(lastEnemyLoc) > dangerRadius) {
						if (myAct.moveInDir(myNavi.bugTo(leadArchon))) needsToMove = false;
					} else {
						if (myAct.moveInDir(myLoc.directionTo(lastEnemyLoc).opposite())) needsToMove = false;
					}
				}
			} else {
				if (myLoc.distanceSquaredTo(lastEnemyLoc) > dangerRadius) {
					Direction bugToLead = myNavi.bugTo(leadArchon);
					if (myLoc.add(bugToLead).distanceSquaredTo(spawnLoc) <= 2) {
						if (myAct.moveInDir(bugToLead)) needsToMove = false;
					}
				} else {
					if (myAct.moveInDir(myLoc.directionTo(lastEnemyLoc).opposite())) needsToMove = false;
				}
				Charge.setSpawnedLoc(spawnLoc);
				chargeComplete = Charge.execute();
				if (buildMode && !woutMsgSent) {
					myRadio.sendSingleDestination(MsgType.MSG_BUILDERWOUTLOC, centerLoc);
					woutMsgSent = true;
					//System.out.println("SENT WOUT BUILD MESSAGE@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				}
			}

			if (fluxLevel > 3000.0 && chargeComplete && !needsToMove && !buildMode) {
				Spawning = !myAct.spawn(RobotType.AURA);
				if (Spawning) {
					roundsSpawning++;
					if (roundsSpawning > ROUNDS_TO_TRY_SPAWN) {
						Spawning = false;
						roundsSpawning = 0;
						needsToMove = true;
					}
				}
			} else if (energonLevel > SPAWN_THRESHOLD && chargeComplete && !needsToMove) {
				Spawning = !myAct.spawn(RobotType.WOUT);
				if (!Spawning) {
					chargeComplete = false;
					woutMsgSent = false;
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
		case 2:
			/*
			 * build states:
			 * 0: go to build location
			 * 1: build tower
			 * 2: spawn wout
			 */
			if(myRC.getFlux()>3000 && canSpawnInFront()) buildState = 1;
			else if(myRC.getEnergonLevel()>50) buildState = 2;
			else buildState = 0;
			switch(buildState){
			case 0:
				if(gotoBuildLocation.execute()){
					buildState=1;
				}
				break;
			case 1:
				if (myRC.getFlux()>3000) {
					if (player.myAct.spawn(RobotType.AURA)) {
						buildState = 0;
					}
				}
				buildState = 0;
				break;
			case 2:
				if(player.myAct.spawn(RobotType.WOUT)){
					buildState = 1;
				}
				break;
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
	
	public boolean canSpawnInFront(){
		try {
			MapLocation front = myRC.getLocation().add(myRC.getDirection());
			if(myRC.senseTerrainTile(front).getType()==TerrainType.LAND){
				if(myRC.senseGroundRobotAtLocation(front)==null){
					return true;
				}
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		return false;
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
