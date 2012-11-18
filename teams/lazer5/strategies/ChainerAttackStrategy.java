package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.communications.Broadcaster;
import lazer5.communications.Encoder;
import lazer5.communications.MsgType;
import lazer5.instincts.Instinct;
import lazer5.instincts.TransferInstinct;
import lazer5.RobotData;
import lazer5.SensorDatabase;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class ChainerAttackStrategy extends Strategy {

	private int TIME_LEASH = 100;
	RobotData target = null;
	private MapLocation toAtk;
	private MapLocation toGo = player.myRC.getLocation();
	private MapLocation baseLoc = player.myRC.getLocation();
	private MapLocation pMaxLoc = new MapLocation(0, 0);
	private Direction myDir;
	private Direction enDir;
	MapLocation myLoc = player.myRC.getLocation();
	private int pMax = 0;
	private int lastSeen = 0;
	private int i = 0;
	private int j = 0;
	private int squareValue[][] = new int[9][9];
	public static final int[][] canAttack = 
	                                               {{0,0,0,1,0,0,0},
	                                               {0,1,1,1,1,1,0},
	                                               {0,1,1,1,1,1,0},
	                                               {1,1,1,1,1,1,1},
	                                               {0,1,1,1,1,1,0},
	                                               {0,1,1,1,1,1,0},
	                                               {0,0,0,1,0,0,0}};
	private int atkPotential[][] = new int[7][7];
	private int state =1;
	private Instinct transfer;

	public ChainerAttackStrategy(RobotPlayer player) {

		super(player);
		//transfer = new TransferInstinct(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// chainer attack states
		// 0 - swarm archons, wait for broadcast information, none then check nearby
		// 1 - broadcast info, calculate potential of all squares, find highest 3x3
		// 2 - attack, broadcast that we have attacked location

		//player.myRC.setIndicatorString(1, player.myRC.getLocation().toString());
		//player.myRC.setIndicatorString(2, player.myDB.toString());
		//player.myRC.setIndicatorString(0, Integer.toString(state));



		//Implement our brand new autosensing code.  Cost~300 or so bytecodes to pull information from everywhere.
		//player.myIntel.senseNearbyRobots();		

		squareValue = new int[9][9];
		atkPotential = new int[7][7];
		if (state == 0) {
			/*	for (i = 0; i < player.myRadio.inbox.size(); i++) {
				Message m = player.myRadio.inbox.get(i);
				MsgType type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
				if (type == MsgType.MSG_KILLNOW) {
					lastSeen = Clock.getRoundNum();
					toGo = m.locations[2];
					break;
				} else if (type == MsgType.MSG_DEFENDTOWER) {
					lastSeen = Clock.getRoundNum();
					toGo = m.locations[2];
					break;
				} else if (type == MsgType.MSG_BASECAMP) {
					baseLoc = m.locations[2];
					break;
				}
			}

			if (Clock.getRoundNum() - lastSeen > TIME_LEASH) {
				player.myNavi.swarmTo(baseLoc);
				return;
			}
			player.myNavi.swarmTo(toGo);
			state = 1;
			return; */ 

			/*for (i = 0; i < player.myRadio.inbox.size(); i++) {
				Message m = player.myRadio.inbox.get(i);
				MsgType type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
				if (type == MsgType.MSG_CHAINERGO) {
					lastSeen = Clock.getRoundNum();
					state = 1;
					toGo = m.locations[2];
					return;
				}
				player.myNavi.swarmTo(toGo);*/
			
			
			
			}
			if (state == 1) {
				///squareValue = new int[9][9];
				//atkPotential = new int[7][7];
				squareValue[4][4] = -1;
				SensorDatabase myDB = player.myDB;
				myDB.resetPtr();
				while (myDB.hasNext()) {
					target = myDB.next();
					MapLocation targetLoc = target.location;
					int transX = targetLoc.getX() - player.myRC.getLocation().getX() + 4;
					int transY = targetLoc.getY() - player.myRC.getLocation().getY() + 4;
					if (transX >= 0 && transX < 9 && transY >= 0 && transY < 9) {



						//This can be made slightly more efficient, but for now, this is the fastest
						//way to get chainers up and running.  gogogogo
						int priority = Encoder.decodeRobotType(target.data).ordinal();
						if(Encoder.decodeRobotTeam(target.data)==player.myOpponent) {
							squareValue[transX][transY] = priority;
						} else {
							squareValue[transX][transY] = 0;
						} 
					}
				}
						
				/*	Robot[] nearbyRobots = player.myIntel.getNearbyGroundRobots();
					for (i = 0; i < nearbyRobots.length; i++) {
						RobotInfo nearbyInfo = player.myRC.senseRobotInfo(nearbyRobots[i]);
						MapLocation targetLoc = nearbyInfo.location;
						int transX = targetLoc.getX() - player.myRC.getLocation().getX() + 4;
						int transY = targetLoc.getY() - player.myRC.getLocation().getY() + 4;
						if (nearbyInfo.team == player.myOpponent) {
							squareValue[transX][transY] = 1;
						} else {
							squareValue[transX][transY] = -1;
						}
					} */
					
				


				// sum potentials about each square, determine max
				pMax = 0;
				for (i = 0; i < 7; i++) {
					for (j = 0; j < 7; j++) {
						atkPotential[i][j] = calcPotential(i,j);
						if (atkPotential[i][j] > pMax) {
							pMax = atkPotential[i][j]*canAttack[i][j];
							pMaxLoc = new MapLocation(i,j);
						}
					}
				}
			

				if (pMax <= 0) {
					//System.out.println(pMax);
					player.myNavi.swarmTo(player.myIntel.getArchonList()[2]);
					state = 1;
					return;
				} else {
					toAtk = new MapLocation(player.myRC.getLocation().getX()+pMaxLoc.getX()-3,player.myRC.getLocation().getY()+pMaxLoc.getY()-3);
					//System.out.println(pMax);
					//System.out.println(toAtk);
					state = 2;
				}
			}
			if (state == 2) {
				myDir = player.myRC.getDirection();
				enDir = myLoc.directionTo(toAtk);
				if (player.myRC.canAttackSquare(toAtk)) {
					if (player.myRC.getRoundsUntilAttackIdle() == 0) {
						//player.myRadio.sendTargetHit(target.id);
						player.myRC.attackGround(toAtk);
						//System.out.println("CHAINER ATTAAACK");
						state = 1;
						return;
					} else {
						return;
					}
				} else if (myDir != enDir){
					if (player.myRC.getRoundsUntilMovementIdle() == 0) {
						player.myRC.setDirection(enDir);
						return;
					} else {
						return;
					}
				} else {
					//System.out.println("l");
					state = 1;
					return;
				}
			}
		}
	


	@Override
	public void runInstincts() throws GameActionException {
		//transfer.execute();
	}

	public int calcPotential(int i, int j) {
		int potential = 0;
		switch (i) {
		case (0):
		case (6):
			switch (j) {
			case (3):
				potential = getP() + getP(Direction.NORTH) + getP(Direction.SOUTH) + getP(Direction.EAST) + getP(Direction.WEST) 
				+ getP(Direction.NORTH_EAST) + getP(Direction.NORTH_WEST) + getP(Direction.SOUTH_EAST) + getP(Direction.SOUTH_WEST);
			break;
			default:
				potential = 0;
				break;
			}
		break;
		case (3):
			potential = getP() + getP(Direction.NORTH) + getP(Direction.SOUTH) + getP(Direction.EAST) + getP(Direction.WEST) 
			+ getP(Direction.NORTH_EAST) + getP(Direction.NORTH_WEST) + getP(Direction.SOUTH_EAST) + getP(Direction.SOUTH_WEST);
		break;
		default:
			switch (j) {
			case (0):
			case (6):
				potential = 0;
			break;
			default:
				potential = getP() + getP(Direction.NORTH) + getP(Direction.SOUTH) + getP(Direction.EAST) + getP(Direction.WEST) 
				+ getP(Direction.NORTH_EAST) + getP(Direction.NORTH_WEST) + getP(Direction.SOUTH_EAST) + getP(Direction.SOUTH_WEST);
				break;
			}
		}
		return potential;
	}

	private int getP(Direction dir) {
		int dx = 0, dy = 0;
		switch (dir) {
		case NORTH:
			dy = -1;
			break;
		case SOUTH:
			dy = 1;
			break;
		case EAST:
			dx = 1;
			break;
		case WEST:
			dx = -1;
			break;
		case NORTH_EAST:
			dx = 1;
			dy = -1;
			break;
		case NORTH_WEST:
			dx = -1;
			dy = -1;
			break;
		case SOUTH_EAST:
			dx = 1;
			dy = 1;
			break;
		case SOUTH_WEST:
			dx = -1;
			dy = 1;
			break;
		}
		return squareValue[i+dx+1][j+dy+1];
	}

	private int getP() {
		return squareValue[i+1][j+1];
	}



}
