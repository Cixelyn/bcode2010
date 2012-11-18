package lazer6;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile.TerrainType;

public class Utilities {
	

	public RobotPlayer player;
	public Random randGen;
	
	
	
	public Utilities(RobotPlayer player) {
		this.player = player;
		randGen = new Random(player.myRC.getRobot().hashCode());
	}
	
	
	
	//////////////////////////////////////RANDOM CODE/////////////////////////////////////////	
	/**
	 * Generates a random Direction of one of the 8 possible.
	 * @return A random Direction Enum
	 */
	public Direction randDir() {
		return Direction.values()[randGen.nextInt(8)];		
	}
	
	//////////////////////////////////////TIMING CODE/////////////////////////////////////////
	private int timerStartRound;
	private int timerStartByte;
	private String timerName;
	
	
	/**
	 * Begins a timer
	 * @param name
	 */
	public void startTimer(String name) {
		timerName = name;
		timerStartRound = Clock.getRoundNum();
		timerStartByte = Clock.getBytecodeNum();
	}
	
	
	
	/**
	 * Stops the last started timer and returns the time elapsed
	 * @return
	 */
	public int stopTimer() {
		int byteCount;
		if(Clock.getRoundNum()==timerStartRound) { //if we're still in the same round
			byteCount = Clock.getBytecodeNum() - timerStartByte;
		} else {//multiple rounds have passed
			byteCount = (6000-timerStartByte) + (Clock.getRoundNum()-timerStartRound-1) * 6000 + Clock.getBytecodeNum();
		}	
//		System.out.println(timerName+": "+byteCount);
		return byteCount;
	}
	
	
	
	
	////////////////////////////////////DIRECTIONAL CODE/////////////////////////////////////////////////////
	/**
	 * returns array of booleans corresponding to the four walls in directions, North, South, East, West, Screwed ( no walls in sensed)
	 * @return
	 */
	public boolean[] archonMapEdgeFinder() {
		MapLocation currentSquare = player.myRC.getLocation();
		boolean[] walls = {false,false,false,false,false};
		
		if (player.myRC.senseTerrainTile(currentSquare.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH)).getType() == TerrainType.OFF_MAP) {
			walls[0] = true;//north
			walls[4] = true;
		}
		if (player.myRC.senseTerrainTile(currentSquare.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH)).getType() == TerrainType.OFF_MAP) {
			walls[1] = true;//south
			walls[4] = true;
		}
		if (player.myRC.senseTerrainTile(currentSquare.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST)).getType() == TerrainType.OFF_MAP) {
			walls[2] = true;//east
			walls[4] = true;
		}
		if (player.myRC.senseTerrainTile(currentSquare.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST)).getType() == TerrainType.OFF_MAP) {
			walls[3] = true;//west
			walls[4] = true;
		}
		return walls;
	}
	
	
	/**
	 * Returns the normalized x value of a direction vector 
	 * @param d Direction to normalize
	 * @return 1 if cardinal, 0.71 if not.
	 */
	public final double getDirNormX(Direction d) {
		if(d.isDiagonal()) return d.dx*0.71;
		else return d.dx;
	}
	
	/**
	 * Returns the normalized y value of a direction vector
	 * @param d
	 * @return
	 */
	public final double getDirNormY(Direction d) {
		if(d.isDiagonal()) return d.dy*0.71;
		else return d.dy;		
	}

	

}
