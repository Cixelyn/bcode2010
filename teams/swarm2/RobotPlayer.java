package swarm2;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;
import battlecode.common.TerrainTile.TerrainType;

public class RobotPlayer implements Runnable {
	private final RobotController myRC;
	private final RobotType type;
	public final Sensor sense;
	public final static double scaleCohesion = 3.0;
	public final static double scaleSeparation = 12.0;
	public final static double scaleAlignment = 1.0;
	public final static double scaleWallAvoidance = 20.0;
	
	public final static int separateDistance = 10;
	
	private int numWouts = 0;
	private Random rndGen;
	
	public RobotPlayer(RobotController rc) {
		this.myRC = rc;
		type = rc.getRobotType();
		this.sense = new Sensor(this.myRC);
		rndGen = new Random(Clock.getRoundNum());
	}

	@Override
	public void run() {
		while(true){
			
			try {
				if (myRC.isMovementActive())
					myRC.yield();
				if(type==RobotType.ARCHON && myRC.getEnergonLevel()>15.0){
					MapLocation targetLoc = myRC.getLocation().add(myRC.getDirection());
					if (myRC.senseGroundRobotAtLocation(targetLoc) == null &&
							myRC.senseTerrainTile(targetLoc).getType() == TerrainType.LAND) {
						myRC.spawn(RobotType.WOUT);
					}
				}
				if(myRC.getRoundsUntilMovementIdle() == 0){
					swarmMove();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public int roundOfLastRotate = 0;
	public void archonMove(){
		//circular movement
		int thisRound = Clock.getRoundNum();
		int roundsSinceLastRotate = thisRound - roundOfLastRotate;
		try {
			if (roundsSinceLastRotate < 50) {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.moveForward();
				} else{
					myRC.setDirection(myRC.getDirection().rotateRight());
					roundOfLastRotate = Clock.getRoundNum();
				}				
				myRC.yield();
			} else {
				int rotDir = rndGen.nextInt(3);
				if(rotDir<2)
					myRC.setDirection(myRC.getDirection().rotateRight());
				else
					myRC.setDirection(myRC.getDirection().rotateLeft());
				roundOfLastRotate = Clock.getRoundNum();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void woutMove(){
		try {
			//wout movement code
			V2d vCohesion, vSeparation, vAlignment, vAvoidance;
			V2d selfV = new V2d(myRC.getLocation());
			MapLocation selfL = myRC.getLocation();
			/////////////////////////COHESION///////////////////////////////
			V2d sum = new V2d(0, 0);
			ArrayList<Robot> robots = sense.detectNearby(myRC.getTeam());
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo curr = myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(curr.location));
				}

				V2d cM = sum.scale(1.0 / robots.size()); //Center of Mass

				vCohesion = cM.sub(selfV).scale(scaleCohesion);
			} else {
				vCohesion = new V2d(0, 0);
			}
			////////////////////////SEPARATION///////////////////////////
			vSeparation = new V2d(0, 0);
			MapLocation allyL = sense.detectNearestAlly();
			if (allyL != new MapLocation(0, 0)) {
				if (allyL.distanceSquaredTo(selfL) < separateDistance) {
					vSeparation = new V2d(allyL.directionTo(selfL))
							.scale(scaleSeparation);
				}
			}
			sum = new V2d(0, 0);
			robots = sense.detectNearby(myRC.getTeam());
			vAlignment = new V2d(0, 0);
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo curr = myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(curr.directionFacing));
				}

				vAlignment = sum.scale(scaleAlignment);
			}
			//sense wall and add avoidance vector
			vAvoidance = new V2d(0, 0);
			if (myRC.senseTerrainTile(selfL.add(myRC.getDirection())) == TerrainTile.OFF_MAP) {
				vAvoidance = new V2d(myRC.getDirection().opposite())
						.scale(scaleWallAvoidance);
			}
			//////////////////////COMPUTATION AND UPDATe///////////////////
			V2d vTotal = vCohesion.add(vSeparation.add(vAlignment
					.add(vAvoidance)));
			myRC.setIndicatorString(0, "Aln: " + vAlignment.toString());
			myRC.setIndicatorString(1, "Coh: " + vCohesion.toString());
			myRC.setIndicatorString(2, "Sep: " + vSeparation.toString());
			MapLocation toGo = vTotal.add(selfV).toLoc();
			Direction toTurn = selfL.directionTo(toGo);
			
			MapLocation leaderLoc = myRC.senseAlliedArchons()[0];
			Direction myDir = myRC.getDirection();
			Direction targetDir = myRC.getDirection();
			//if there is a leader, set desired direction towards leader
			if(leaderLoc!=null){
				targetDir = myRC.getLocation().directionTo(leaderLoc);
			}
			
			if(myDir != targetDir){
				myRC.setDirection(targetDir);
				myRC.yield();
			}
			if(myRC.canMove(targetDir)){
				myRC.moveForward();
				myRC.yield();
			}
			
			
//			int num = rndGen.nextInt(40);
//			if (num < 30) {
//				Direction dir = myRC.getDirection();
//				if (dir == toTurn && myRC.canMove(dir)) {
//					myRC.moveForward();
//				} else {
//					if (toTurn != Direction.OMNI) {
//						myRC.setDirection(toTurn);
//					}
//				}
//
//				myRC.yield();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void swarmMove(){
		try {
			//swarm movement code
			V2d vCohesion, vSeparation, vAlignment, vAvoidance;
			V2d selfV = new V2d(myRC.getLocation());
			V2d cM = null;;
			MapLocation selfL = myRC.getLocation();
			/////////////////////////COHESION///////////////////////////////
			V2d sum = new V2d(0, 0);
			ArrayList<Robot> robots = sense.detectNearby(myRC.getTeam());
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo curr = myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(curr.location));
				}

				cM = sum.scale(1.0 / robots.size()); //Center of Mass

				vCohesion = cM.sub(selfV).scale(scaleCohesion);
			} else {
				vCohesion = new V2d(0, 0);
			}
			////////////////////////SEPARATION///////////////////////////
			vSeparation = new V2d(0, 0);
			MapLocation allyL = sense.detectNearestAlly();
			if (allyL != new MapLocation(0, 0)) {
				if (allyL.distanceSquaredTo(selfL) < separateDistance) {
					vSeparation = new V2d(allyL.directionTo(selfL))
							.scale(scaleSeparation);
				}
			}
			sum = new V2d(0, 0);
			robots = sense.detectNearby(myRC.getTeam());
			vAlignment = new V2d(0, 0);
			if (robots.size() > 0) {
				for (Robot r : robots) {
					RobotInfo curr = myRC.senseRobotInfo(r);
					sum = sum.add(new V2d(curr.directionFacing));
				}

				vAlignment = sum.scale(scaleAlignment);
			}
			//sense wall and add avoidance vector
			vAvoidance = new V2d(0, 0);
			if (myRC.senseTerrainTile(selfL.add(myRC.getDirection())) == TerrainTile.OFF_MAP) {
				vAvoidance = new V2d(myRC.getDirection().opposite())
						.scale(scaleWallAvoidance);
			}
			//////////////////////COMPUTATION AND UPDATe///////////////////
			V2d vTotal = vCohesion.add(vSeparation.add(vAlignment
					.add(vAvoidance)));
			myRC.setIndicatorString(0, "Aln: " + vAlignment.toString());
			myRC.setIndicatorString(1, "Coh: " + vCohesion.toString());
			myRC.setIndicatorString(2, "Sep: " + vSeparation.toString());
			MapLocation toGo = vTotal.add(selfV).toLoc();
			Direction toTurn = selfL.directionTo(toGo);
			
			
			//movement
			if(type==RobotType.ARCHON){
				if(cM !=null){
					Direction dir = myRC.getLocation().directionTo(new MapLocation((int)Math.round(cM.x), (int)Math.round(cM.y)));
					if(dir != Direction.OMNI){
						myRC.setDirection(dir);
						myRC.yield();
					}
					if(myRC.getRoundsUntilMovementIdle()==0){
						myRC.moveForward();
						myRC.yield();
					}
				}
			}else if(type == RobotType.WOUT){
				int num = rndGen.nextInt(40);
				if (num < 30) {
					Direction dir = myRC.getDirection();
					if (dir == toTurn && myRC.canMove(dir)) {
						myRC.moveForward();
					} else {
						if (toTurn != Direction.OMNI) {
							myRC.setDirection(toTurn);
						}
					}

					myRC.yield();
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
