package stephen;

import battlecode.common.*;

public class WoutPlayer extends BasePlayer{
	public final int MAX_MOVES=2;
	public  int moves=0;
	public int lastDir=0;
	
	public WoutPlayer(RobotController r){
		super(r);
	}
	
	public void run(){
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (rc.isMovementActive()) {
                    rc.yield();
                }

                this.energy.transferEnergon();

                for (Robot r: rc.senseNearbyAirRobots()){
                	RobotInfo info =rc.senseRobotInfo(r);
        			if(info.type == RobotType.ARCHON){
        				if(info.location.isAdjacentTo(rc.getLocation())){
        					rc.transferFlux(rc.getFlux(), rc.senseRobotInfo(r).location, RobotLevel.IN_AIR);
        					rc.yield();
        					break;
//        					if(rc.getFlux()>=rc.getFlux()){
//        						rc.transferFlux(10, rc.senseRobotInfo(r).location, RobotLevel.IN_AIR);
////        						rc.yield();
//        						break;
//        					}
        				}
        			}
        		}
                rc.yield();
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
}
