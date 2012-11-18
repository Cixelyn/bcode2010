package swarm1;

import battlecode.common.*;
import battlecode.engine.instrumenter.lang.System;

import java.util.*;


public class RobotPlayer implements Runnable {
	
	private final RobotController rc;
	private final Sensor sense;
	
	
	public final static double scaleCohesion = 3.0;
	public final static double scaleSeparation = 10.0;
	public final static double scaleAlignment = 1.0;
	public final static double scaleWallAvoidance = 20.0;
	
	public final static int separateDistance = 10;
	
	private Random rndGen;


	public RobotPlayer(RobotController _rc)
	{
		rc = _rc;
		sense = new Sensor(rc);
		
		rndGen = new Random(Clock.getRoundNum());
	}
	
	public void run(){
		
		while(true) {
			try{
				
				//Initial quick break
				while (rc.isMovementActive()) {
                    rc.yield();
                }
				
				
				V2d vCohesion, vSeparation, vAlignment, vAvoidance;
				
				
				V2d selfV = new V2d(rc.getLocation());
				MapLocation selfL = rc.getLocation();
				
					
				/////////////////////////COHESION///////////////////////////////
				V2d sum = new V2d(0,0);
				
				ArrayList<Robot> robots = sense.detectNearby(rc.getTeam());

				if(robots.size()>0) {
					for(Robot r:robots) {
						RobotInfo curr = rc.senseRobotInfo(r);
						sum = sum.add(new V2d(curr.location));
					}
					
					V2d cM = sum.scale(1.0/robots.size()); //Center of Mass
					
					
					vCohesion = cM.sub(selfV).scale(scaleCohesion);
				} else {
					vCohesion = new V2d(0,0);
				}
				
	
				
				////////////////////////SEPARATION///////////////////////////
				vSeparation = new V2d(0,0);				
				
				MapLocation allyL = sense.detectNearestAlly();
				if(allyL != new MapLocation(0,0)) {
					if(allyL.distanceSquaredTo(selfL)<separateDistance) {
						vSeparation = new V2d(allyL.directionTo(selfL)).scale(scaleSeparation);
					}
				}
				
				

				
				///////////////////////ALIGNMENT/////////////////////////////
				
				sum = new V2d(0,0);
				robots = sense.detectNearby(rc.getTeam());

				vAlignment = new V2d(0,0);
				if(robots.size()>0) {
					for(Robot r:robots) {
						RobotInfo curr = rc.senseRobotInfo(r);
						sum = sum.add(new V2d(curr.directionFacing));
					}
				
					vAlignment = sum.scale(scaleAlignment);
				}
				
				
				/////////////////////AVOIDANCE/////////////////////////////
				
				//sense wall and add avoidance vector
				vAvoidance = new V2d(0,0);
				
				if(rc.senseTerrainTile(selfL.add(rc.getDirection()))==TerrainTile.OFF_MAP){
					vAvoidance = new V2d(rc.getDirection().opposite()).scale(scaleWallAvoidance);
				}
				
				
				
				//////////////////////COMPUTATION AND UPDATe///////////////////
				V2d vTotal = vCohesion.add(vSeparation.add(vAlignment.add(vAvoidance)));
				
				rc.setIndicatorString(0, "Aln: " + vAlignment.toString());
				rc.setIndicatorString(1, "Coh: " + vCohesion.toString());
				rc.setIndicatorString(2, "Sep: " + vSeparation.toString());
				
				
				
				MapLocation toGo = vTotal.add(selfV).toLoc();
				Direction toTurn = selfL.directionTo(toGo);
				
				
				
				int num = rndGen.nextInt(40);
				System.out.println(num);
				
				if(num<30){
					if(rc.getDirection()==toTurn){
						rc.moveForward();
					} else {
						if(toTurn != Direction.OMNI) {
							rc.setDirection(toTurn);
						}
					}
					
					rc.yield();
				}
				else {
					rc.spawn(RobotType.WOUT);
					rc.yield();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}