package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.SpawnChargeBehavior;
import lazer5.behaviors.SpawnSoldierBehavior;
import lazer5.instincts.Instinct;
import lazer5.instincts.RevertedTransferInstinct;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.TerrainTile.TerrainType;

public class InitArchonStrategy extends Strategy{
	private int state = 0;
	
	private final double SPAWN_THRESHOLD = 50.0;
	
	private Behavior SpawnSoldier, Charge;
	
	private Instinct Transfer;
	private Direction dir;
	private boolean[] walls;


	
	public InitArchonStrategy(RobotPlayer player) {
		super(player);
		Charge = new SpawnChargeBehavior(player);
		SpawnSoldier = new SpawnSoldierBehavior(player);
		Transfer = new RevertedTransferInstinct(player);
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
			if (player.myRC.senseRobotInfo(r).team != player.myTeam) {
				//STRATEGY TRANSITION
				player.changeStrategy(new ArchonSuperJihadStrategy(player));
			}
		}
		
		//System.out.println("i am in state: " + state);
		/*
		 * states:
		 * 0 = find initial direction to go
		 * 1 = bug in direction
		 * 2 = bounce offf the wall
		 * 3 = try to spawn shit
		 * 4 = charge that shit
		 */
		player.myRC.setIndicatorString(2, "state: " + state);
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
				player.myNavi.archonBugInDirection(dir);
			}
			if (player.myRC.senseTerrainTile(player.myRC.getLocation().add(dir).add(dir).add(dir).add(dir)).getType() == TerrainType.OFF_MAP) {
				state = 2;
			} else {
				state = 3;
			}
			break;
		case 2:
			walls = player.myUtils.archonMapEdgeFinder();
			
			player.myRC.setIndicatorString(2, Boolean.toString(walls[0]) + Boolean.toString(walls[1]) + Boolean.toString(walls[2]) + Boolean.toString(walls[3]) + Boolean.toString(walls[4])+ dir.toString());
			
			if ((walls[0]) && !(walls[1]) && !(walls[2]) && !(walls[3])) {
				if (dir.equals(Direction.NORTH_EAST)){
					dir = Direction.SOUTH_EAST;
				} else if (dir.equals(Direction.NORTH_WEST)) {
					dir = Direction.SOUTH_WEST;
				} else if (dir.equals(Direction.NORTH)) {
					dir = Direction.SOUTH;
				}
				
			} else if (!(walls[0]) && (walls[1]) && !(walls[2]) && !(walls[3])) {
				if (dir.equals(Direction.SOUTH)) {
					dir = Direction.NORTH;
				} else if (dir.equals(Direction.SOUTH_WEST)) {
					dir = Direction.NORTH_WEST;
				} else if (dir.equals(Direction.SOUTH_EAST)) {
					dir = Direction.NORTH_EAST;
				}
				
			} else if (!(walls[0]) && !(walls[1]) && (walls[2]) && !(walls[3])) {
				if (dir.equals(Direction.EAST)) {
					dir = Direction.WEST;
				} else if (dir.equals(Direction.SOUTH_EAST)) {
					dir = Direction.SOUTH_WEST;
				} else if (dir.equals(Direction.NORTH_EAST)) {
					dir = Direction.NORTH_WEST;
				}
				
			} else if (!(walls[0]) && !(walls[1]) && !(walls[2]) && (walls[3])) {
				if (dir.equals(Direction.WEST)) {
					dir = Direction.EAST;
				} else if (dir.equals(Direction.SOUTH_WEST)) {
					dir = Direction.SOUTH_EAST;
				} else if (dir.equals(Direction.NORTH_WEST)) {
					dir = Direction.NORTH_EAST;
				}
					
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
			break;
		case 3:
			if (player.myRC.getEnergonLevel() > SPAWN_THRESHOLD) {
				if (player.myRC.getRoundsUntilMovementIdle() == 0) {
					if (SpawnSoldier.execute()) {
						state = 4;
					}
				}	
			} else {
				state = 1;
			}
			break;
		case 4:
			if (Charge.execute()) {
				state = 1;
			}
			break;
		}
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		Transfer.execute();
	}

}