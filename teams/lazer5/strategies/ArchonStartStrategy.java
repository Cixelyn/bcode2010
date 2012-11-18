package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.SpawnChargeBehavior;
import lazer5.behaviors.SpawnSoldierBehavior;
import lazer5.behaviors.SpawnWoutBehavior;
import lazer5.communications.Broadcaster;
import lazer5.communications.Encoder;
import lazer5.communications.MsgType;
import lazer5.instincts.Instinct;
import lazer5.instincts.RevertedTransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.TerrainTile.TerrainType;

public class ArchonStartStrategy extends Strategy {
	private int state = 0;
	private Direction rushDir;
	private Direction spreadDir;
	private final double SPAWN_THRESHOLD = 50.0;
	
	private Behavior spawnSoldier, spawnWout, Charge;
	boolean Charging = false;
	
	private Instinct Transfer;
	private boolean[] walls;
	
	private int ID;
	private MapLocation[] archonList;
	private MapLocation myLoc;
	private int myX, myY;
	private Direction myDir;
	private int moveCounter = 0;
	private Direction turnDir;
	private int landSquares;
	
	private MapLocation origLoc;
	
	//private float distanceRushed = 0.0f;
	private int distanceFromOrigin = 0;
	private boolean directionNeeded = false;
	
	//private int numSoldiers = 0;
	//private int numWouts = 0;
	
	private int numRounds = 0;
	
	private static final int AMOUNT_TO_SPREAD = 1;
	private static final int BUILDER_RUSH_DISTANCE = 150;
	
	public ArchonStartStrategy(RobotPlayer player) {
		super(player);
		spawnSoldier = new SpawnSoldierBehavior(player);
		spawnWout = new SpawnWoutBehavior(player);
		Charge = new SpawnChargeBehavior(player);
		Transfer = new RevertedTransferInstinct(player);
		origLoc = player.myRC.getLocation();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// TODO Auto-generated method stub
		
		if (Clock.getRoundNum() == 0) {
			player.myRC.yield();
			return;
		}
		
		ID = player.myIntel.getArchonID();
		archonList = player.myIntel.getArchonList();
		myLoc = player.myRC.getLocation();
		myX = myLoc.getX();
		myY = myLoc.getY();
		myDir = player.myRC.getDirection();
		
		
		//reflexive: if enemy spotted and you're a jihad archon, change to jihad strat
		if (!(ID==0 || ID==1)) {
			
			if (Clock.getRoundNum()%20 == 0 && Clock.getRoundNum()!=0) {
				player.myRadio.sendSingleDestination(MsgType.MSG_ARCHONCOMMAND, myLoc.add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir).add(rushDir));
			}
			
			Robot[] nearbyRobots = player.myIntel.getNearbyRobots();
			for (int i = 0; i < nearbyRobots.length; i++) {
				if (player.myRC.senseRobotInfo(nearbyRobots[i]).team == player.myOpponent) {
					player.changeStrategy(new ArchonSuperJihadStrategy(player));
				}
			}
		}

		Message m;
		MsgType type;
		for (int i = 0; i < player.myRadio.inbox.size(); i++) {
			m = player.myRadio.inbox.get(i);
			type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
			if (type == MsgType.MSG_DIRECTIONNEEDED) {
				if (!(directionNeeded)) {
					player.myRadio.sendSingleNumber(MsgType.MSG_DIRECTIONREPLY, rushDir.ordinal());
				}
			}
			if (type == MsgType.MSG_DIRECTIONREPLY) {
				if (directionNeeded) {
					rushDir = Direction.values()[m.ints[Broadcaster.firstData]];
					directionNeeded = false;
				}
			}
			if (type == MsgType.MSG_LEADDIR) {
				if (directionNeeded) {
					rushDir = Direction.values()[m.ints[Broadcaster.firstData]];
					directionNeeded = false;
				}
			}
		}
		
		
		if (Clock.getRoundNum() > 5 && directionNeeded) {
			if (ID==0) {
				rushDir = player.myUtils.randDir();
				System.out.println("sent lead dir");
				player.myRadio.sendSingleNumber(MsgType.MSG_LEADDIR, rushDir.ordinal());
				directionNeeded = false;
			}
		}
		
		
		
		switch(state) {
		case 0:
			walls = player.myUtils.archonMapEdgeFinder();
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
				directionNeeded = true;
				rushDir = myDir;
				//no walls found wtf call for direction broadcast and change rushDir to received direction once received
				//System.out.println("WTF HALP");
				player.myRadio.sendSingleNotice(MsgType.MSG_DIRECTIONNEEDED);
			}
			
			spreadDir = spreadDirection();
			if (myDir.equals(spreadDir)) state = 2;
			else {
				if (player.myRC.getRoundsUntilMovementIdle()==0 && player.myRC.hasActionSet()==false) {
					player.myRC.setDirection(spreadDir);
					state = 2;
				} else	state = 1;
			}


			break;
		case 1:
			if (myDir.equals(spreadDir)) state = 2;
			else {
				player.myRC.setDirection(spreadDir);
				state = 2;
			}
			break;
		case 2:
			if (player.myRC.getRoundsUntilMovementIdle()==0 && player.myRC.hasActionSet()==false) {
				if (player.myRC.canMove(myDir)) {
					player.myRC.moveForward();
					moveCounter++;
				} else {
					player.myRC.setDirection(myDir.rotateRight());
				}
				
			}
			if (moveCounter >= AMOUNT_TO_SPREAD) state = 3;
			break;
		case 3:
			if (player.myRC.getRoundsUntilMovementIdle()==0 && player.myRC.hasActionSet()==false) {
				if (player.myRC.senseTerrainTile(myLoc.add(myDir)).getType() != TerrainType.LAND) {
					landFinder();
					if (landSquares == 0) {
						player.myRC.setDirection(player.myUtils.randDir());
						state = 2;
					} else
						player.myRC.setDirection(turnDir);
				} else {
					
					
					state = 4;
					
					
				}
			}
			break;
		case 4:
			if (ID==0 || ID==1) {
				//spawn wouts
				if (player.myRC.getEnergonLevel()>SPAWN_THRESHOLD && Charging == false) {
					if (player.myRC.getRoundsUntilMovementIdle()==0) {
						if (spawnWout.execute()) {
							Charging = true;
							//numWouts++;
						} else {
							Charging = false;
						}
					} else return;
				} else if (Charging) {
					Charging = !Charge.execute();
				}
//				if (numWouts >= 3) {
//					state = 5;
//				}
			} else {
				//spawn soldiers
				
				if(player.myRC.getEnergonLevel()>SPAWN_THRESHOLD && Charging == false) {
					if(player.myRC.getRoundsUntilMovementIdle()==0) {
					
						if (spawnSoldier.execute()) {
							Charging = true;
							//numSoldiers++;
						} else {
							Charging = false;
						}
					} else return;
				}else if (Charging) {
					Charging = !Charge.execute();
				} else{
				
				}
//				if (numSoldiers >= 3) {
//					state = 5;
//				}				
				
			}
			numRounds++;
			if (numRounds >= 110 && Charging == false) {
				state = 5;
			}
			break;
		case 5:
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.hasActionSet() == false) {
				player.myNavi.archonBugInDirection(rushDir);
				distanceFromOrigin = myLoc.distanceSquaredTo(origLoc);
			}
			if (player.myRC.senseTerrainTile(player.myRC.getLocation().add(rushDir).add(rushDir).add(rushDir).add(rushDir)).getType() == TerrainType.OFF_MAP) {
				state = 6;
			} else {
				state = 4;
			}
			
			if ((ID==0 || ID==1) && distanceFromOrigin > BUILDER_RUSH_DISTANCE) {
				player.changeStrategy(new ArchonBuilderStrategy(player));
			}
			
			break;
		case 6:
			walls = player.myUtils.archonMapEdgeFinder();
			
			//player.myRC.setIndicatorString(2, Boolean.toString(walls[0]) + Boolean.toString(walls[1]) + Boolean.toString(walls[2]) + Boolean.toString(walls[3]) + Boolean.toString(walls[4])+ rushDir.toString());
			
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
			
			
			state = 5;
			break;
		}
		//player.myRC.setIndicatorString(2, "State: " + state + " distance rushed: " + distanceRushed + rushDir.toString() + " charging: " + Charging + player.myRC.getRoundsUntilMovementIdle());

	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		Transfer.execute();
	}

	public Direction spreadDirection() {
		int xGreater = 0;
		int yGreater = 0;
		int xSmaller = 0;
		for (int i = 0; i < archonList.length; i++) {
			if (archonList[i].getX() > myX && archonList[i].getY() == myY) xGreater++;
			if (archonList[i].getY() > myY && archonList[i].getX() == myX) yGreater++;
			if (archonList[i].getX() < myX && archonList[i].getY() == myY) xSmaller++;
		}
		if (xGreater+xSmaller == 2) {
			switch(xGreater) {
			case 0:
				if (yGreater==0) return Direction.SOUTH_EAST;
				else return Direction.NORTH_EAST;
			case 1:
				if (yGreater==0) return Direction.SOUTH;
				else return Direction.NORTH;
			case 2:
				if (yGreater==0) return Direction.SOUTH_WEST;
				else return Direction.NORTH_WEST;
			default:
				return Direction.NONE;
			}
		} else {
			switch(yGreater) {
			case 0:
				if (xGreater==0) return Direction.SOUTH_EAST;
				else return Direction.SOUTH_WEST;
			case 1:
				if (xGreater==0) return Direction.EAST;
				else return Direction.WEST;
			case 2:
				if (xGreater==0) return Direction.NORTH_EAST;
				else return Direction.NORTH_WEST;
			default:
				return Direction.NONE;
			}
		}
	}
	public void landFinder() {
		turnDir = Direction.NONE;
		MapLocation currentTile;
		landSquares = 0;
		for (int i = 0; i < 8; i++) {
			currentTile = myLoc.add(Direction.values()[i]);
			if (player.myRC.senseTerrainTile(currentTile).getType() == TerrainType.LAND) {
				landSquares++;
				turnDir = Direction.values()[i];
			}
		}
	}

	
	
	
}
