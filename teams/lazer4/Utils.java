package lazer4;
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile.TerrainType;

/**
 * Random utilities and misc functions to make life easier for everyone
 * @author lazer pew pew
 *
 */
public class Utils {
	
	public RobotController myRC;
	public Random randGen;
	
	
	public Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NONE };
	public Direction[] inverseDirections = { Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.NONE };
	public int[] walls = {0,0,0,0,0,0,0,0,0};
	
	
	private int timerStartRound;
	private int timerStartByte;
	private String timerName;
	
	public Utils(RobotController myRC) {
		this.myRC = myRC;
		randGen = new Random(myRC.getRobot().hashCode());
	}
	
	public Direction randDir() {
		return Direction.values()[randGen.nextInt(8)];
	}
	
	public void startTimer(String name) {
		this.timerName = name;
		this.timerStartRound = Clock.getRoundNum();
		this.timerStartByte = Clock.getBytecodeNum();
	}
	
	public int stopTimer() {
		int byteCount;
		if(Clock.getRoundNum()==timerStartRound) { //if we're still in the same round
			byteCount = Clock.getBytecodeNum() - timerStartByte;
		} else {//multiple rounds have passed
			byteCount = (6000-timerStartByte) + (Clock.getRoundNum()-timerStartRound-1) * 6000 + Clock.getBytecodeNum();
		}
		
		System.out.println(timerName + ": "+Integer.toString(byteCount));
		return byteCount;
	}
	
	public Direction directionAdder(Direction dir1, Direction dir2) {
		DirectionVector dirv1 = new DirectionVector(dir1);
		DirectionVector dirv2 = new DirectionVector(dir2);
		
		//DEBUGG
		System.out.println("adding " + dir1 + dir2 + "yields " + dirv1.add(dirv2).toDirection().toString());
		System.out.println("Direction Vectors are " + dirv1.toString() + "and " + dirv2.toString());
		
		
		return dirv1.add(dirv2).toDirection();
	}
	public Direction directionAwayFromWalls() {
		MapLocation currentSquare;
		Direction newDirection = Direction.NONE;
		for (int h = 0; h <=8; h++){
			walls[h] = 0;
		}
		for (int i = 0; i <=7; i++){
			currentSquare = myRC.getLocation();
			if (i%2==0) {
				for (int j = 1; j <=6; j++) {
					currentSquare = currentSquare.add(directions[i]);
					if (/*myRC.senseTerrainTile(currentSquare).getType() == TerrainType.VOID || */myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
						walls[i] = 1;
					}
				}
			} else {
				for (int j = 1; j <=4; j++) {
					currentSquare = currentSquare.add(directions[i]);
					if (/*myRC.senseTerrainTile(currentSquare).getType() == TerrainType.VOID || */myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
						walls[i] = 1;
					}
				}
			}
		}
		if (myRC.senseTerrainTile(myRC.getLocation()).getType() == TerrainType.VOID || myRC.senseTerrainTile(myRC.getLocation()).getType() == TerrainType.OFF_MAP) {
			walls[8] = 1;
		}
		for (int k = 0; k <= 8; k++) {
			if (walls[k] == 1) {
				newDirection = directionAdder(newDirection, inverseDirections[k]);
			}
		}
		//DEBUGGGGG
		myRC.setIndicatorString(2, newDirection.toString() + walls[0] + walls[1] + walls[2]+ walls[3] + walls[4] + walls[5] + walls[6] + walls[7] + walls[8]);
		if (newDirection == Direction.NONE){
			return myRC.getDirection();
		}
		return newDirection;
	}
	public Direction archonDirectionAwayFromEdge() {
		MapLocation currentSquare = myRC.getLocation();
		Direction newDirection = Direction.NONE;
		boolean north = false;
		boolean south = false;
		boolean east = false;
		boolean west = false;
		if (myRC.senseTerrainTile(currentSquare.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH)).getType() == TerrainType.OFF_MAP) {
			north = true;
		}
		if (myRC.senseTerrainTile(currentSquare.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH)).getType() == TerrainType.OFF_MAP) {
			south= true;
		}
		if (myRC.senseTerrainTile(currentSquare.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST)).getType() == TerrainType.OFF_MAP) {
			east = true;
		}
		if (myRC.senseTerrainTile(currentSquare.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST)).getType() == TerrainType.OFF_MAP) {
			west = true;
		}
		if ((north) && !(south) && !(east) && !(west)) {
			newDirection = Direction.SOUTH;
		} else if (!(north) && (south) && !(east) && !(west)) {
			newDirection = Direction.NORTH;
		} else if (!(north) && !(south) && (east) && !(west)) {
			newDirection = Direction.WEST;
		} else if (!(north) && !(south) && !(east) && (west)) {
			newDirection = Direction.EAST;
		} else if ((north) && !(south) && (east) && !(west)) {
			newDirection = Direction.SOUTH_WEST;
		} else if (!(north) && (south) && (east) && !(west)) {
			newDirection = Direction.NORTH_WEST;
		} else if (!(north) && (south) && !(east) && (west)) {
			newDirection = Direction.NORTH_EAST;
		} else if ((north) && !(south) && !(east) && (west)) {
			newDirection = Direction.SOUTH_EAST;
		}
		return newDirection;
	}
	public boolean[] archonMapEdgeFinder() {
		MapLocation currentSquare = myRC.getLocation();
		boolean[] walls = {false,false,false,false,false};
		
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
}
