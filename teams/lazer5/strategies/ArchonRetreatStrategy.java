package lazer5.strategies;

import lazer5.RobotPlayer;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.Direction;
import battlecode.common.TerrainTile.TerrainType;

public class ArchonRetreatStrategy extends Strategy{
	private int state = 0;
	
	private final float ENEMY_AVOIDANCE_FACTOR = 100.0f;
	private final float MAP_EDGE_AVOIDANCE_FACTOR = 50.0f;
	
	private float[] adjacentPotentials = {0,0,0,0,0,0,0,0};
	private MapLocation[] adjacentSquares;
	private Direction dir;
	
	
	public ArchonRetreatStrategy(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		// TODO Auto-generated method stub
		/*
		 * state = 0 calculate potential map and determine direction to move
		 * state = 1 set direction
		 * state = 2 move.
		 */
		
		adjacentSquares = new MapLocation[8];
		initSquares();
		
		
		switch(state) {
		case 0:
			//account for enemy robot potentials
			
			initPotentials();
			
			Robot[] nearby = player.myIntel.getNearbyGroundRobots();
			for (Robot r : nearby) {
				RobotInfo info = player.myRC.senseRobotInfo(r);
				MapLocation loc = info.location;
				if (info.team == player.myOpponent) {
					for (int i = 0; i< 8; i++) {
						adjacentPotentials[i] += (ENEMY_AVOIDANCE_FACTOR)/(adjacentSquares[i].distanceSquaredTo(loc));
					}
				}
			}
			//account for map edges
			MapLocation currentSquare;
			for (int i = 0; i <8; i++){
				currentSquare = player.myRC.getLocation();
				if (i%2==0) {
					for (int j = 1; j <=6; j++) {
						currentSquare = currentSquare.add(Direction.values()[i]);
						if (player.myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
							for (int k = 0; k < 8; k++) {
								adjacentPotentials[k] += (MAP_EDGE_AVOIDANCE_FACTOR)/(adjacentSquares[k].distanceSquaredTo(currentSquare));
							}
							
							break;
						}
					}
				} else {
					for (int j = 1; j <=4; j++) {
						currentSquare = currentSquare.add(Direction.values()[i]);
						if (player.myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
							for (int k = 0; k < 8; k++) {
								adjacentPotentials[k] += (MAP_EDGE_AVOIDANCE_FACTOR)/(adjacentSquares[k].distanceSquaredTo(currentSquare));
							}
							break;
						}
					}
				}
			}
			
			
			//determine direction to move in
			dir = Direction.NONE;
			float lowestPotential = 9999.0f;
			for (int i = 0; i < 8; i++) {
				if (adjacentPotentials[i] < lowestPotential) {
					dir = player.myRC.getLocation().directionTo(adjacentSquares[i]);
					lowestPotential = adjacentPotentials[i];
				}
			}
			
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.hasActionSet() == false) {
				player.myRC.setDirection(dir);
				state = 2;
			} else {
				state = 1;
			}
			
			
			break;
		case 1:
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.hasActionSet() == false) {
				player.myRC.setDirection(dir);
				state = 2;
			}
			break;
		case 2:
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.hasActionSet() == false && player.myRC.canMove(dir)) {
				player.myRC.moveForward();
			}
			state = 0;
			break;
		}
		player.myRC.setIndicatorString(2, "state: " + state + " dir: " + dir.toString() + adjacentPotentials[0] + " " + adjacentPotentials[1] + " " + adjacentPotentials[2] + " " + adjacentPotentials[3] + " " + adjacentPotentials[4] + " " + adjacentPotentials[5] + " " + adjacentPotentials[6] + " " + adjacentPotentials[7] + " ");
		
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		
	}
	public void initSquares() {
		for(int i = 0; i < 8; i++) {
			adjacentSquares[i] = player.myRC.getLocation().add(Direction.values()[i]);
		}
	}
	public void initPotentials() {
		for (int i = 0; i < 8; i++) {
			adjacentPotentials[i] = 0.0f;
		}
	}
	
}
