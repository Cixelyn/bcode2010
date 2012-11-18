package lazer6.behaviors;

import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile.TerrainType;

public class ArchonRetreatBehavior extends Behavior{
	private int state = 0;

	private final float ENEMY_AVOIDANCE_FACTOR = 100.0f;
	private final float MAP_EDGE_AVOIDANCE_FACTOR = 50.0f;

	private float[] adjacentPotentials = {0,0,0,0,0,0,0,0};
	private MapLocation[] adjacentSquares;
	private Direction dir;


	public ArchonRetreatBehavior(RobotPlayer player) {
		super(player);
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


	@Override
	public boolean runActions() {

		adjacentSquares = new MapLocation[8];
		initSquares();
		//account for enemy robot potentials

		initPotentials();

		Robot[] nearby = myProfiler.nearbyGroundRobots;
		RobotInfo info = null;
		MapLocation loc = null;
		for (int i = 0; i < nearby.length; i++) {
			try {
				info = myRC.senseRobotInfo(nearby[i]);

				loc = info.location;
				if (info.team == player.myOpponent) {
					for (int j = 0; j< 8; j++) {
						adjacentPotentials[j] += (ENEMY_AVOIDANCE_FACTOR)/(adjacentSquares[j].distanceSquaredTo(loc));
					}
				}
			} catch (GameActionException e) {
//				System.out.println("Action Exception: Archon retreat sense robot info");
				e.printStackTrace();
			}
		}
		//account for map edges
		MapLocation currentSquare;
		for (int i = 0; i <8; i++){
			currentSquare = myRC.getLocation();
			if (i%2==0) {
				for (int j = 1; j <=6; j++) {
					currentSquare = currentSquare.add(Direction.values()[i]);
					if (myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
						for (int k = 0; k < 8; k++) {
							adjacentPotentials[k] += (MAP_EDGE_AVOIDANCE_FACTOR)/(adjacentSquares[k].distanceSquaredTo(currentSquare));
						}

						break;
					}
				}
			} else {
				for (int j = 1; j <=4; j++) {
					currentSquare = currentSquare.add(Direction.values()[i]);
					if (myRC.senseTerrainTile(currentSquare).getType() == TerrainType.OFF_MAP) {
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
				dir = myRC.getLocation().directionTo(adjacentSquares[i]);
				lowestPotential = adjacentPotentials[i];
			}
		}

		if (player.myAct.moveInDir(dir)) {
			return true;
		}
		
//		player.myRC.setIndicatorString(2, "state: " + state + " dir: " + dir.toString() + adjacentPotentials[0] + " " + adjacentPotentials[1] + " " + adjacentPotentials[2] + " " + adjacentPotentials[3] + " " + adjacentPotentials[4] + " " + adjacentPotentials[5] + " " + adjacentPotentials[6] + " " + adjacentPotentials[7] + " ");

		return false;
	}
}
