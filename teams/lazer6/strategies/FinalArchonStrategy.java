package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
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

public class FinalArchonStrategy extends Strategy {
	private int state = 0;

	private Instinct Transfer;

	private static final int RUSH_WANDER = 0;
	private static final int ENGAGE = 1;
	
	private static final double SPAWN_THRESHOLD = 60;
	private static final double CHARGE_PERCENTAGE = 0.8;

	private Direction rushDir = myRC.getDirection();
	private Direction engageDir;
	private boolean directionNeeded = false;
	private MapLocation spawnLoc;
	private int roundsCharging = 0;
	private boolean Spawning = false;
	private boolean needsToMove = false;
	private int roundsSpawning = 0;
	private boolean Charging = false;
	private int roundsWithFewEnemies = 0;
	private int roundsWithNoEnemies = 0;

	public FinalArchonStrategy(RobotPlayer player) {
		super(player);
		Transfer = new TransferInstinct(player);
		myProfiler.setScanMode(true, true, false, true);
	}

	@Override
	public boolean beginStrategy() {
		///////////////////////////////////////////INITAL RUSH CODE
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

		} else {	//////////////////////////////WTF HALP CODE
			directionNeeded = true;
			myRadio.sendSingleNotice(MsgType.MSG_NEEDDIRECTION);

		}
		return true;
	}

	@Override
	public void runBehaviors() {
		MapLocation myLoc = myRC.getLocation();
		int ID = myProfiler.myArchonID;
		double energonLevel = myRC.getEnergonLevel();

		if (ID == 0) {
			player.changeStrategy(new ArchonBuilderStrategy(player));
			return;
		}
		//////////////////////////////////SEND ROBOT LIST CODE///////////////////////////////////////////
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
		} else if (myProfiler.enemyAirInRange > myProfiler.enemyAttackersInRange){
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
		///////////////////////////////////////RECIEVE MESSAGES//////////////////////////////////////////////////
		Message m;
		MsgType type;
		int i = 0;
		int closestDistance = 9999;
		int Distance = 9999;
		MapLocation Origin;
		MapLocation closestSender = null;
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
			if (type == MsgType.MSG_BUILDMODE) {
				//TODO move to hull center and defend DO
			}
			if (type == MsgType.MSG_ENGAGINGENEMY) {
				Origin = m.locations[Broadcaster.idxOrigin];
				Distance = myLoc.distanceSquaredTo(Origin);
				if (Distance < closestDistance) {
					closestDistance = Distance;
					closestSender = Origin;
				}
			}
			i++;
		}
		if (closestSender != null) {
			state = ENGAGE;
			engageDir = myLoc.directionTo(closestSender);
		}
		
		myRC.setIndicatorString(2, "state: " + state);
		//////////////////////////////////////STATE MACHINE/////////////////////////////////////////////////////////////
		switch(state) {
		case RUSH_WANDER:
		////////////////////////////////////////WALL BOUNCE//////////////////////////////////////////////////////////////////
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
			////////////////////////////TRY TO MOVE//////////////////////////////////////
			if (!Charging || spawnLoc == null || myLoc.add(rushDir).distanceSquaredTo(spawnLoc)<=2) {
				MapLocation leadArchon = myProfiler.alliedArchons[myProfiler.alliedArchons.length-1];
				if (ID == myProfiler.alliedArchons.length-1) {
					myAct.moveInDir(rushDir);
				} else {
					myAct.moveInDir(myLoc.directionTo(leadArchon));
				}
			}
			if (Charging) {
				roundsCharging++;
				RobotInfo spawnBotInfo = null;
				Robot spawnedBot;
				try {
					spawnedBot = myRC.senseGroundRobotAtLocation(spawnLoc);
					spawnBotInfo = myRC.senseRobotInfo(spawnedBot);
				} catch (GameActionException e) {
					System.out.println("Action Exception: archon sense spawnbot info");
					e.printStackTrace();
				}
				if (roundsCharging > 40 || spawnBotInfo.eventualEnergon > spawnBotInfo.type.maxEnergon()*CHARGE_PERCENTAGE) {
					Charging = false;
					roundsCharging = 0;
					spawnedBot = null;
					spawnLoc = null;
				}
			}
			
			//////////////SPAWNING////////////////////////
			if (energonLevel > SPAWN_THRESHOLD) {
				if (myAct.spawnInPlace(RobotType.CHAINER)) {
					spawnLoc = myLoc.add(myRC.getDirection());
					Charging = true;
				}
			}
			break;
		case ENGAGE:
			
			/////////////////////////////spawn or wander counters//////////////////////////////////
			if (myProfiler.enemyAttackersInRange < 4 && myProfiler.enemyAttackersInRange > 0) {//TODO tweakable: fewer than # enemy attacking units constitutes a round with 'few' enemies (currently 4)
				roundsWithFewEnemies++;
			} else if (myProfiler.enemyAttackersInRange==0) {
				roundsWithNoEnemies++;
			} else {
				roundsWithFewEnemies = 0;
				roundsWithNoEnemies = 0;
			}
			
			
			////////////////////////////TRY TO MOVE//////////////////////////////////////
			MapLocation leadArchon = myProfiler.alliedArchons[myProfiler.alliedArchons.length-1];
			RobotInfo maxRangeEnemy = myProfiler.maxAttRangeEnemy;
			if (ID == myProfiler.alliedArchons.length-1) {
			//if (!Spawning) {
				if (maxRangeEnemy == null) {
					if (myAct.moveInDir(engageDir)) needsToMove = false;
				} else if (Math.min(myLoc.distanceSquaredTo(myProfiler.closestEnemyInfo.location),myLoc.distanceSquaredTo(maxRangeEnemy.location)) > maxRangeEnemy.type.attackRadiusMaxSquared()+9) {
					if (myAct.moveInDir(engageDir)) needsToMove = false;
				} else {
					if (myAct.moveLikeMJArchons(engageDir, myLoc.directionTo(myProfiler.closestEnemyInfo.location).opposite())) needsToMove = false;
				}
			//}
			} else {
				if (maxRangeEnemy == null) {
					if (myAct.moveInDir(myLoc.directionTo(leadArchon))) needsToMove = false;
				} else if (Math.min(myLoc.distanceSquaredTo(myProfiler.closestEnemyInfo.location),myLoc.distanceSquaredTo(maxRangeEnemy.location)) > maxRangeEnemy.type.attackRadiusMaxSquared()+9) {
					if (myAct.moveInDir(myLoc.directionTo(leadArchon))) needsToMove = false;
				} else {
					if (myAct.moveLikeMJArchons(engageDir, myLoc.directionTo(myProfiler.closestEnemyInfo.location).opposite())) needsToMove = false;
				}
			}
			
			
			//////////////SPAWNING////////////////////////
		/*	if (energonLevel > SPAWN_THRESHOLD && !needsToMove) {
				myRC.setIndicatorString(2, "trying to spawn");
				Spawning = !myAct.spawn(typeToSpawn());
			} else {
				Spawning = false;
			}
			if (Spawning) {
				roundsSpawning++;
				needsToMove = true;
				if (roundsSpawning > 2) {
					Spawning = false;
					roundsSpawning = 0;
					needsToMove = false;
				}
			} else {
				roundsSpawning = 0;
			}*/
			if (energonLevel > SPAWN_THRESHOLD) {
				myAct.spawnInPlace(typeToSpawn());
			}
			if (roundsWithNoEnemies > 60) {
				state = RUSH_WANDER;
				Charging = false;
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
	public RobotType typeToSpawn() {
		if (roundsWithFewEnemies > 45) {//TODO tweakable: number of rounds of few enemies before we spawn soldiers
			return RobotType.SOLDIER;
		}
		return RobotType.CHAINER;
	}

}
