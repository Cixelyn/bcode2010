package lazer4.strategies;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.Team;
import battlecode.common.TerrainTile.TerrainType;
import lazer4.RobotPlayer;

public class InitArchonDirectionStrategy extends Strategy{
	private int state = 0;
	
	private Direction dir;
	private boolean[] walls;
	private Team myTeam;
	
	public InitArchonDirectionStrategy(RobotPlayer player) {
		super(player);
		myTeam = player.myRC.getTeam();
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
		
		
		//reflexive behaviors: go to next strategy after seeing enemy
		//CODE GOEZ HEER
		for (Robot r : player.myIntel.getNearbyRobots()) {
			if (player.myRC.senseRobotInfo(r).team != myTeam) {
				//STRATEGY TRANSITION
			}
		}
		
		
		/*
		 * states:
		 * 0 = find initial direction to go
		 * 1 = set direction
		 * 2 = move in direction and transition to next state once a wall is reached
		 * 3 = bounce offf the wall
		 * 4 = set direction
		 * 5 = move until wall again
		 * restart from 0 so it just circles around if it finds no enemies
		 */
		switch(state) {
		case 0:
			walls = player.myUtils.archonMapEdgeFinder();
			if (walls[4] == true) {
				if ((walls[0]) && !(walls[1]) && !(walls[2]) && !(walls[3])) {
					dir = Direction.SOUTH_EAST;
				} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && !(walls[3])) {
					dir = Direction.NORTH_WEST;
				} else if (!(walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
					dir = Direction.NORTH_WEST;
				} else if (!(walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
					dir = Direction.SOUTH_EAST;
				} else if ((walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
					dir = Direction.SOUTH_WEST;
				} else if (!(walls[0]) && (walls[1]) && (walls[2]) && !(walls[3])) {
					dir = Direction.NORTH_WEST;
				} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && (walls[3])) {
					dir = Direction.NORTH_EAST;
				} else if ((walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
					dir = Direction.SOUTH_EAST;
				}
				state = 1;
			} else {
				//no walls found wtf quit and go to archon default strat
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
			if (player.myRC.senseTerrainTile(player.myRC.getLocation().add(dir)).getType() == TerrainType.OFF_MAP) {
				state = 3;
			}
			break;
		case 3:
			switch(dir) {
			case NORTH_WEST:
				dir = Direction.NORTH_EAST;
				break;
			case NORTH_EAST:
				dir = Direction.NORTH_WEST;
				break;
			case SOUTH_EAST:
				dir = Direction.SOUTH_WEST;
				break;
			case SOUTH_WEST:
				dir = Direction.SOUTH_EAST;
				break;
			}
			state = 4;
			break;
		case 4:
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.hasActionSet() == false) {
				player.myRC.setDirection(dir);
				state = 5;
			}
			break;
		case 5:
			if (player.myRC.getRoundsUntilMovementIdle() == 0 && player.myRC.hasActionSet() == false && player.myRC.canMove(dir)) {
				player.myRC.moveForward();
			}
			if (player.myRC.senseTerrainTile(player.myRC.getLocation().add(dir)).getType() == TerrainType.OFF_MAP) {
				state = 0;
			}
			break;			
		}
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

}
