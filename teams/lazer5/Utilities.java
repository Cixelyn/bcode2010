package lazer5;
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile.TerrainType;

/**
 * Random utilities and misc functions to make life easier for everyone
 * @author lazer pew pew
 *
 */
public class Utilities {
	
	
	public RobotPlayer player;
	public Random randGen;

	

	/////////////////////////////////CONSTRUCTER///////////////////////////////////////
	public Utilities(RobotPlayer player) {
		this.player = player;
		randGen = new Random(player.myRC.getRobot().hashCode());
	}
	
	
	
	////////////////////////////////RANDOM FUNCTIONS//////////////////////////////////
	public Direction randDir() {
		return Direction.values()[randGen.nextInt(8)];
	}


	
	
	//////////////////////////////////TIMING CODE/////////////////////////////////////
	private int timerStartRound;
	private int timerStartByte;
	private String timerName;
	
	public void startTimer(String name) {
		timerName = name;
		timerStartRound = Clock.getRoundNum();
		timerStartByte = Clock.getBytecodeNum();
	}
	
	
	public int stopTimer() {
		int byteCount;
		if(Clock.getRoundNum()==timerStartRound) { //if we're still in the same round
			byteCount = Clock.getBytecodeNum() - timerStartByte;
		} else {//multiple rounds have passed
			byteCount = (6000-timerStartByte) + (Clock.getRoundNum()-timerStartRound-1) * 6000 + Clock.getBytecodeNum();
		}	
		System.out.println(timerName+": "+byteCount);
		return byteCount;
	}
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
	public MapLocation archonCoM(int lo, int hi) {
		
		//I think this works, but there needs to be some testing to make sure
		
		int sumX = 0;
		int sumY = 0;
		MapLocation[] aList = player.myIntel.getArchonList();
		int length = aList.length;
		
		if(length>lo) {  //if we still have attacking archons
			
			hi = Math.min(length-1, hi);
			for(int i=lo; i<=hi; i++) {
				sumX += aList[i].getX();
				sumY += aList[i].getY();
			}
		} else {	//totally fucked, begin last stand defense
			
			lo = 0;
			hi = length-1;
			for(int i=lo; i<=hi; i++) {
				sumX += aList[i].getX();
				sumY += aList[i].getY();
			}
		}
				
		sumX  = sumX / (hi-lo+1);
		sumY = sumY / (hi-lo+1);
		return new MapLocation(sumX,sumY);
	}
	
}
