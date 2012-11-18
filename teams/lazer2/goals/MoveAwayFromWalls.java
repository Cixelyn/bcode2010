package lazer2.goals;

import battlecode.common.*;
import battlecode.common.TerrainTile.TerrainType;
import lazer2.*;



public class MoveAwayFromWalls extends Goal{
	public Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NONE };
	public Direction[] inverseDirections = { Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.NONE };
	public Direction dirToCenter = Direction.NONE;
	public int[] walls = {0,0,0,0,0,0,0,0,0};
	
	public MoveAwayFromWalls(BasePlayer player) {
		super(player);
		initFilters();
	}
	public boolean takeControl() {
		if (player.myRC.isMovementActive() || player.myRC.hasActionSet()) return false;
		return true;
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
			currentSquare = player.myRC.getLocation();
			if (i%2==0) {
				for (int j = 1; j <=6; j++) {
					currentSquare = currentSquare.add(directions[i]);
					if (/*player.myRC.senseTerrainTile(currentSquare).getType() == TerrainType.VOID || */player.myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
						walls[i] = 1;
					}
				}
			} else {
				for (int j = 1; j <=4; j++) {
					currentSquare = currentSquare.add(directions[i]);
					if (/*player.myRC.senseTerrainTile(currentSquare).getType() == TerrainType.VOID || */player.myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
						walls[i] = 1;
					}
				}
			}
		}
		if (player.myRC.senseTerrainTile(player.myRC.getLocation()).getType() == TerrainType.VOID || player.myRC.senseTerrainTile(player.myRC.getLocation()).getType() == TerrainType.OFF_MAP) {
			walls[8] = 1;
		}
		for (int k = 0; k <= 8; k++) {
			if (walls[k] == 1) {
				newDirection = directionAdder(newDirection, inverseDirections[k]);
			}
		}
		//DEBUGGGGG
		player.myRC.setIndicatorString(2, newDirection.toString() + walls[0] + walls[1] + walls[2]+ walls[3] + walls[4] + walls[5] + walls[6] + walls[7] + walls[8]);
		if (newDirection == Direction.NONE){
			player.CenterDirFound=true;
			player.myRC.setIndicatorString(1, "CenterDirFound: " + player.myRC.getDirection());
			return player.myRC.getDirection();
		}
		return newDirection;
	}
	public int execute() throws GameActionException {
		if (!(player.CenterDirFound)) {dirToCenter = directionAwayFromWalls();}
		
		player.myNavi.bugInDirection(dirToCenter);
		
		return GOAL_SUCCESS;
	}
	public void initFilters() {
		
	}
}
