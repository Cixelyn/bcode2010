package lazer5.strategies;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import battlecode.common.TerrainTile.TerrainType;
import lazer5.RobotPlayer;

public class ArchonSpreadStrategy extends Strategy{
	private int state = 0;
	private Direction dir;
	private int ID;
	private MapLocation[] archonList;
	private MapLocation myLoc;
	private int myX, myY;
	private Direction myDir;
	private int moveCounter = 0;
	private Direction turnDir;
	private int landSquares;
	
	
	private static final int AMOUNT_TO_SPREAD = 2;

	public ArchonSpreadStrategy(RobotPlayer player) {
		super(player);
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
		
		
		/*
		 * 0 = determine direction
		 * 1 = set direction
		 * 2 = move direction
		 * 3 = check for void
		 */
		ID = player.myIntel.getArchonID();
		archonList = player.myIntel.getArchonList();
		myLoc = player.myRC.getLocation();
		myX = myLoc.getX();
		myY = myLoc.getY();
		myDir = player.myRC.getDirection();
		switch(state) {
		case 0:
			dir = spreadDirection();
			if (dir != Direction.NONE) state = 1;
			break;
		case 1:
			if (myDir.equals(dir)) state = 2;
			else {
				player.myRC.setDirection(dir);
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
					//STRATEGY TRANSITION
				}
			}
			break;
		}
		
		player.myRC.setIndicatorString(2, dir.toString());
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		
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
