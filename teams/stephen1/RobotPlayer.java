package stephen1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class RobotPlayer implements Runnable{

    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        //System.out.println("STARTING");
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (myRC.isMovementActive()) {
                    myRC.yield();
                }
                
                if (myRC.getRobotType() == RobotType.ARCHON){
                	myRC.spawn(RobotType.WOUT);
            		for (Robot r: myRC.senseNearbyAirRobots()){
            			myRC.transferUnitEnergon(10, myRC.senseRobotInfo(r).location, RobotLevel.ON_GROUND);
            		}
                }
                
                if (myRC.getRobotType() != RobotType.ARCHON){
                	//attack nearby units
                    for (Robot r: myRC.senseNearbyGroundRobots()){
//                    	if (myRC.getTeam()!= myRC.senseRobotInfo(r).team){
                    	if(myRC.senseRobotInfo(r).team == Team.B){
                    		MapLocation eLoc = myRC.senseRobotInfo(r).location;
                    		myRC.attackAir(eLoc);
                    	}
                    }
                	//if less than a third total energon
                	if(myRC.getEnergonLevel() < myRC.getRobotType().maxEnergon()/4){
                		getNearestArchon(myRC);
                		myRC.setDirection(Direction.NORTH_WEST);
                	}
                	//transfer flux to archon
                	if(myRC.getRobotType() == RobotType.WOUT){
                		for (Robot r: myRC.senseNearbyAirRobots()){
                			if(myRC.senseRobotInfo(r).type == RobotType.ARCHON){
                				myRC.transferFlux(10, myRC.senseRobotInfo(r).location, RobotLevel.IN_AIR);
                			}
                		}
                	}
                	
                	move(myRC);
                }
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
    
    public void move(RobotController rc) throws GameActionException{
        if (rc.canMove(rc.getDirection())) {
            System.out.println("about to move");
            rc.moveForward();
        } else {
            rc.setDirection(rc.getDirection().rotateRight());
        }
    }
    
    public MapLocation getNearestArchon(RobotController rc){
      MapLocation[] archons = rc.senseAlliedArchons();
      MapLocation closest = archons[0];

      for (MapLocation archon : archons) {
        if (archon.distanceSquaredTo(rc.getLocation()) >= closest.distanceSquaredTo(rc.getLocation()))
          continue;
        closest = archon;
      }

      return closest;
    }
}
