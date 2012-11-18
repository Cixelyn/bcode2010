package lazerguns1.behaviors;

import java.util.Random;
import java.util.Set;

import lazerguns1.RobotPlayer;
import lazerguns1.V2d;
import lazerguns1.filters.Filter;
import lazerguns1.filters.FilterFactory;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class ArchonGetSwarmDirection extends Behavior {
	private final static double scaleCohesion = 5.0;
	private final static double scaleSeparation = 8.0;
	private final static double scaleAlignment = 1.0;
	private final static double scaleWallAvoidance = 20.0;
	private final static int separateDistance = 10;

	private V2d vCohesion, vSeparation, vAlignment, vAvoidance, selfV, vCM, vTotal;
	private MapLocation com;
	private MapLocation toGo;
	private Direction toTurn;
	private V2d sum = new V2d(0,0);
	private MapLocation selfLoc;
	private Filter allies;
	
	private Random rndGen;
	
	public ArchonGetSwarmDirection(RobotPlayer player) {
		super(player);
		rndGen = new Random(0);
		allies = FilterFactory.alliesInRange(player.myRC, player.myIntel,
				player.myRC.getRobotType().sensorRadius());
	}

	@Override
	public boolean runActions() throws GameActionException {
		//swarm code
		selfV = new V2d(player.myRC.getLocation());
		selfLoc = player.myRC.getLocation();
		
		if (player.myIntel.getNearbyGroundRobots().length >0) {
			/////////////////////////COHESION///////////////////////////////
			Set<Robot> robots = allies.filter(player.myIntel.getNearbyGroundRobots());
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo info = player.myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(info.location));
				}
				vCM = sum.scale(1.0 / robots.size());

				vCohesion = vCM.sub(selfV).scale(scaleCohesion);
			} else {
				vCohesion = new V2d(0, 0);
			}
			Robot closest = allies.closest(player.myIntel.getNearbyGroundRobots());
			MapLocation closestL;
			vSeparation = new V2d(0, 0);
			if(closest != null){
				closestL = player.myRC.senseRobotInfo(closest).location;
				if (closestL != new MapLocation(0, 0)) {
					if (closestL.distanceSquaredTo(selfLoc) < separateDistance) {
						vSeparation = new V2d(closestL.directionTo(selfLoc))
								.scale(scaleSeparation);
					}
				}
			}
			sum = new V2d(0, 0);
			vAlignment = new V2d(0, 0);
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo info = player.myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(info.directionFacing));
				}
				vAlignment = sum.scale(scaleAlignment);
			}
			//sense wall and add avoidance vector
			vAvoidance = new V2d(0, 0);
			if (player.myRC.senseTerrainTile(selfLoc.add(player.myRC.getDirection())) == TerrainTile.OFF_MAP) {
				vAvoidance = new V2d(player.myRC.getDirection().opposite())
						.scale(scaleWallAvoidance);
			}
			//////////////////////COMPUTATION AND UPDATE///////////////////
			if(vCM != null){
				com = new MapLocation((int) Math.round(vCM.x), (int) Math.round(vCM.y));
			}
			vTotal = vCohesion.add(vSeparation.add(vAlignment.add(vAvoidance)));
			toGo = vTotal.add(selfV).toLoc();
			toTurn = selfLoc.directionTo(toGo);
			
			if (vCM != null && player.myRC.getRoundsUntilMovementIdle()==0) {
				Direction dir = player.myRC.getLocation().directionTo(com);
				if (dir != Direction.OMNI) {
					player.myRC.setDirection(dir);
					return true;
				}
			}
		}
		return false;
	}

}
