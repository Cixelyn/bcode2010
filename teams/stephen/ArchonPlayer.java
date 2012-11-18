package stephen;

import battlecode.common.*;

public class ArchonPlayer extends BasePlayer{
	
	private final int MAX_MOVES =4;
	private int moves =0;
	private int lastDir=0; //0 = backwards, 1 = forwards
	private int spawnTime = 0;
	private int ROUNDS_TO_WAIT = 20;

	public ArchonPlayer(RobotController r){
		super(r);
	}
	
	public void run(){
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (rc.isMovementActive()) {
                    rc.yield();
                }
                if(rc.getFlux() >= 3000 && canSpawnGround(rc.getDirection())){
                	rc.spawn(RobotType.AURA);	
                	spawnTime = Clock.getRoundNum();
                	rc.yield();
                }
                
                if(canSpawnGround(rc.getDirection())){
                	rc.spawn(RobotType.WOUT);
                	spawnTime = Clock.getRoundNum();
                	rc.yield();
                }
                
                if(Clock.getRoundNum()-spawnTime < ROUNDS_TO_WAIT){
//                	if(spawnType == 0)
                		this.energy.chargeSpawnedUnit(rc.getLocation().add(rc.getDirection()));
//                	else{
//                		this.energy.chargeSpawnedTower(rc.getLocation().add(rc.getDirection()));
//                	}
                	rc.setIndicatorString(2,"charging spawned unit");
                	rc.yield();
                }
                else{
                	rc.setIndicatorString(2, "not charging spawned Unit");
	                this.energy.transferEnergon();
	              //patrol back and forth 4 spaces
	                if(rc.getRoundsUntilMovementIdle()==0){
		                if(lastDir==0){ //last moving backwards
		                	if(moves == 0){ //if where we started, go forward
		                		if (rc.canMove(rc.getDirection())) {
		    	                    rc.setIndicatorString(1, "moving forward");
		    	                    rc.moveForward();
		    	                    lastDir =1;
		                		}
		    	                moves+=1;
		    	                rc.yield();
		                	}
		                	else{ //keep moving backwards
		                		if(rc.canMove(rc.getDirection().opposite())){
		                			rc.setIndicatorString(1, "moving backward");
		                			rc.moveBackward();
		                			lastDir=0;
		                		}
		                		moves-=1;
		                		rc.yield();
		                	}
		                }
		                else{ //last moving forward (lastDir==1)
		                	if(moves == MAX_MOVES){ //if as far as we can go, go backwards
		                		if (rc.canMove(rc.getDirection().opposite())) {
		    	                    rc.setIndicatorString(1, "moving backwards");
		    	                    rc.moveBackward();
		    	                    lastDir=0;
		                		}
		    	                moves-=1;
		                	}
		                	else{ //keep moving forwards
		                		if(rc.canMove(rc.getDirection())){
		                			rc.setIndicatorString(1, "moving forward");
		                			rc.moveForward();
		                			lastDir=1;
		                		}
		                		moves+=1;
		                		rc.yield();
		                	}
		                }
	                }
                }
                

                /*** end of main loop ***/
            } catch (Exception e) {
//                System.out.println("caught exception:");
//                e.printStackTrace();
            }
        }
	}
	
	public boolean canSpawnGround(Direction dir)throws GameActionException{
		MapLocation loc = rc.getLocation().add(dir);
		return (rc.senseTerrainTile(loc).getType()==TerrainTile.TerrainType.LAND)&& (rc.senseGroundRobotAtLocation(loc)==null); 
	}
}
