package kevin1;
import battlecode.common.*;

import java.util.Random;
public class ArchonPlayer extends BasePlayer {
	Random rand;
	public ArchonPlayer(RobotController r){
		super(r);
		rand = new Random(0);
	}
	public void run(){
        while (true){
			try{
				while (rc.isMovementActive()) {
                    rc.yield();
                }
                //routinely spawn stuff
				if (Clock.getRoundNum()%30 == 0){
	                if ((rc.senseGroundRobotAtLocation(rc.getLocation().add(rc.getDirection())) == null) && (rc.senseTerrainTile(rc.getLocation().add(rc.getDirection())).getType() == TerrainTile.TerrainType.LAND)){
	                	if (rc.getEnergonLevel()>40) {
	                		rc.spawn(RobotType.SOLDIER);
	                		rc.yield();
	                	}

	                }
				}
				if (Clock.getRoundNum()%20 == 0){
					if ((rc.senseGroundRobotAtLocation(rc.getLocation().add(rc.getDirection())) == null) && (rc.senseTerrainTile(rc.getLocation().add(rc.getDirection())).getType() == TerrainTile.TerrainType.LAND)){
	                	if (rc.getEnergonLevel()>40) {
	                		rc.spawn(RobotType.WOUT);
	                		rc.yield();
	                	}
	                }
				}
				if (Clock.getRoundNum()%35==0) {
					if ((rc.senseGroundRobotAtLocation(rc.getLocation().add(rc.getDirection())) == null) && (rc.senseTerrainTile(rc.getLocation().add(rc.getDirection())).getType() == TerrainTile.TerrainType.LAND)){
	                	if (rc.getEnergonLevel()>40) {
	                		rc.spawn(RobotType.CHAINER);
	                		rc.yield();
	                	}
	                }
				}
				//Transfer Energon to Allies
				Robot[] nearbyAllies = rc.senseNearbyGroundRobots();
				
				for (Robot r:nearbyAllies) {
    				if (rc.senseRobotInfo(r).energonLevel < 20 && rc.senseRobotInfo(r).team != rc.getTeam().opponent() && rc.getEnergonLevel()>50) {
    					MapLocation loc = rc.senseRobotInfo(r).location;
    					if (loc.isAdjacentTo(rc.getLocation())) {
    						rc.transferUnitEnergon(5, loc, RobotLevel.ON_GROUND);
    						
    					}
    				}
    			}
				//Random movement
				int choice = rand.nextInt(4);
				if(choice==0){
					rc.setDirection(rc.getDirection().rotateLeft());
					rc.setIndicatorString(0, "RotL");
				}else if (choice==1) {
					rc.setDirection(rc.getDirection().rotateRight());
					rc.setIndicatorString(0, "RotR");
				}else if (choice==2) {
					if(rc.canMove(rc.getDirection())){
						rc.setIndicatorString(1, Boolean.toString(rc.hasActionSet()));
						rc.moveForward();
						rc.setIndicatorString(0, "MovF");
					}else {
						if (rc.canMove(rc.getDirection().opposite())) rc.moveBackward();
					}

                    }
                rc.yield();

	        	}
	        
	        
	        catch (Exception e){
	        	System.out.println("caught exception:");
	            e.printStackTrace();
	        }
        }
	}
}
