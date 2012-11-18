package lazerguns2.strategies;

import java.util.Random;
import java.util.Set;

import lazerguns2.RobotPlayer;
import lazerguns2.V2d;
import lazerguns2.behaviors.Behavior;
import lazerguns2.filters.Filter;
import lazerguns2.filters.FilterFactory;




import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class WoutSwarmBehavior extends Behavior {
	private RobotController myRC;
	private final static double scaleCohesion = 5.0;
	private final static double scaleSeparation = 5.0;
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

	public WoutSwarmBehavior(RobotPlayer player){
		super(player);
		myRC = player.myRC;
		rndGen = new Random(0);
		allies = FilterFactory.alliesInRange(myRC, player.myIntel,
				myRC.getRobotType().sensorRadius());
	}
	
	@Override
	public boolean runActions() throws GameActionException{
		//swarm code
		selfV = new V2d(myRC.getLocation());
		selfLoc = myRC.getLocation();
		
		if (player.myIntel.getNearbyGroundRobots().length >0) {
			/////////////////////////COHESION///////////////////////////////
			Set<Robot> robots = allies.filter(player.myIntel.getNearbyGroundRobots());
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo info = myRC.senseRobotInfo(r);
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
				closestL = myRC.senseRobotInfo(closest).location;
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
					RobotInfo info = myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(info.directionFacing));
				}
				vAlignment = sum.scale(scaleAlignment);
			}
			//sense wall and add avoidance vector
			vAvoidance = new V2d(0, 0);
			if (myRC.senseTerrainTile(selfLoc.add(myRC.getDirection())) == TerrainTile.OFF_MAP) {
				vAvoidance = new V2d(myRC.getDirection().opposite())
						.scale(scaleWallAvoidance);
			}
			//////////////////////COMPUTATION AND UPDATE///////////////////
			vTotal = vCohesion.add(vSeparation.add(vAlignment.add(vAvoidance)));
			toGo = vTotal.add(selfV).toLoc();
			toTurn = selfLoc.directionTo(toGo);
			
			int num = rndGen.nextInt(40);
			if (num < 30 && myRC.getRoundsUntilMovementIdle()==0) {
				Direction dir = myRC.getDirection();
				if (dir == toTurn && myRC.canMove(dir)) {
					return false;
				} else {
					if (toTurn != Direction.OMNI) {
						myRC.setDirection(toTurn);
						return true;
					}
				}
			}
		}
		return false;
	}
}
