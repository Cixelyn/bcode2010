package lazerguns2;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Navigation {
	protected final RobotPlayer player;
	protected final RobotController myRC;
	protected final Intelligence myIntel;
	protected final Utils myUtils;
	
	private MapLocation goal;
	private boolean tracing;
	private boolean traceRight;
	private boolean directionTracing;
	private MapLocation traceNode;
	private int roundsTracing = 0;
	
	public final static double scaleCohesion = 4.0;
	public final static double scaleSeparation = 8.0;
	public final static double scaleAlignment = 1.0;
	public final static double scaleDestinationVector = 15.0;
	public final static int separateDistance = 9;
	
	public Navigation(RobotPlayer player) {
		this.player = player;
		this.myRC = player.myRC;
		this.myIntel = player.myIntel;
		this.myUtils = player.myUtils;
		
		
		this.goal = null;
		this.tracing = false;
		this.traceRight = true;
		this.directionTracing = false;
		this.traceNode = null;

	}
	
	// will check for round changes to prevent
	// pointless repeat calls later
	public MapLocation myLocation() {
		return this.myRC.getLocation();
	}
	
	public Direction myDirection() {
		return this.myRC.getDirection();
	}
	
	public Direction destDirection(MapLocation loc) {
		return myLocation().directionTo(loc);
	}
	
	public void moveInDirection(Direction dir) {
		if (dir.ordinal() >= 8) return;
		Direction currentDir = myDirection();
		if (!(this.myRC.canMove(dir))) return;
		try {
			if (currentDir.equals(dir)) {
				this.myRC.moveForward();
			} else if (currentDir.equals(dir.opposite())) {
				this.myRC.moveBackward();
			} else {
				this.myRC.setDirection(dir);
				/*this.myRC.yield();
				if (this.myRC.canMove(dir))
					this.myRC.moveForward();*/
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	private void setTraceRight(Direction dir)
	  {
	    Direction rightDir = dir.rotateRight();
	    Direction leftDir = dir.rotateLeft();
	    int traceRightNum = 0;
	    int traceLeftNum = 0;
	    while (!(this.myRC.canMove(rightDir))) {
	      ++traceRightNum;
	      rightDir = rightDir.rotateRight();
	    }
	    while (!(this.myRC.canMove(leftDir))) {
	      ++traceLeftNum;
	      leftDir = leftDir.rotateLeft();
	    }
	    this.traceRight = (traceRightNum <= traceLeftNum);
	  }
	
	//Take a peak at this piece of code
	//FIX THIS CODE IF POSSIBLE
	protected Direction deadReckon(Direction dir)
	  {
	    if (this.myRC.canMove(dir))
	      return dir;
	    if (this.myRC.canMove(dir.rotateRight()))
	      return dir.rotateRight();
	    if (this.myRC.canMove(dir.rotateLeft())) {
	      return dir.rotateLeft();
	    }
	    return dir;//?
	  }
	
	public void bugInDirection(Direction dir) throws GameActionException {
		if (dir.equals(Direction.NONE)) return;
	    if (this.directionTracing) {
	      if (((this.myRC.canMove(dir)) && (this.myIntel.getLocation().distanceSquaredTo(this.goal) < this.traceNode.distanceSquaredTo(this.goal))) || (this.roundsTracing > 20))
	      {
	        this.directionTracing = false;
	        this.roundsTracing = 0;
	        this.goal = null;
	        moveInDirection(dir);
	      } else {
	        int turns = 0;
	        while ((this.myRC.canMove(dir)) && (turns < 8)) {
	          dir = (this.traceRight) ? dir.rotateLeft() : dir.rotateRight();
	          ++turns;
	        }
	        turns = 0;
	        while ((!(this.myRC.canMove(dir))) && (turns < 8)) {
	          dir = (this.traceRight) ? dir.rotateRight() : dir.rotateLeft();
	          ++turns;
	        }
	        moveInDirection(dir);
	      }
	      this.roundsTracing += 1;
	    } else {
	      Direction nextDir = deadReckon(dir);
	      if (this.myRC.canMove(nextDir)) {
	        moveInDirection(nextDir);
	      } else {
	        setTraceRight(nextDir);
	        this.directionTracing = true;
	        this.goal = this.myIntel.getLocation().add(dir).add(dir).add(dir).add(dir).add(dir);
	        this.traceNode = this.myIntel.getLocation();
	      }
	    }
	}
	public void bugTo(MapLocation dest) throws GameActionException {
		if (!(this.myRC.getLocation().equals(dest))) {
			Direction dir = destDirection(dest);
			if (dir.equals(Direction.NONE)) return;
			if (this.tracing) {
				if (((this.myRC.canMove(dir)) && (this.myIntel.getLocation().distanceSquaredTo(dest) < this.traceNode.distanceSquaredTo(dest))) || (this.roundsTracing > 20)) {
					this.tracing = false;
					this.roundsTracing = 0;
					this.traceRight = (!(this.traceRight));
					moveInDirection(dir);
				} else {
					int turns = 0;
					while ((this.myRC.canMove(dir)) && (turns < 8)) {
						dir = (this.traceRight) ? dir.rotateLeft() : dir.rotateRight();
						++turns;
					}
					turns = 0;
					while ((!(this.myRC.canMove(dir))) && (turns < 8)) {
						dir = (this.traceRight) ? dir.rotateRight() : dir.rotateLeft();
						++turns;
					}
					moveInDirection(dir);
				}
				this.roundsTracing += 1;
			} else {
				Direction nextDir = deadReckon(dir);
				if (this.myRC.canMove(nextDir)) {
					moveInDirection(nextDir);
				} else {
					setTraceRight(nextDir);
					this.tracing = true;
					this.traceNode = this.myIntel.getLocation();
				}
			}
		}
		
		
		/*TerrainType isAdjacent = this.myRC.senseTerrainTile(myLocation().add(myDirection())).getType();
		while (!this.myRC.canMove(destDirection(dest)))
			this.myRC.setDirection(myDirection().rotateLeft());
			this.myRC.yield();
			if (this.myRC.canMove(myDirection())) {
				this.myRC.moveForward();
				this.myRC.yield();
			}*/
	}
	
	
	public void SwarmTo(MapLocation dest) throws GameActionException {
		//Initial quick break
		while (player.myRC.isMovementActive()) {
            player.myRC.yield();
        }
		
		V2d vCohesion = new V2d(0,0);
		V2d vSeparation = new V2d(0,0);
		V2d vAlignment = new V2d(0,0);
		V2d vAvoidance = new V2d(0,0);
		
		
		V2d selfV = new V2d(player.myRC.getLocation());
		MapLocation selfL = player.myRC.getLocation();
		
		V2d vDestination = new V2d(dest).sub(selfV).norm().scale(scaleDestinationVector);
		
			
		/////////////////////////COHESION AND ALIGNMENT AND SEPARATION///////////////////////////////
		

		V2d coSum = new V2d(0,0);
		V2d alSum = new V2d(0,0);
		V2d seSum = new V2d(0,0);
		
		Robot[] robots = player.myIntel.getNearbyRobots();
		if(robots.length>0) {
			for(Robot r:robots) {
				RobotInfo currR = player.myIntel.getInfo(r);

				if(currR!=null) {
					if(currR.team==player.myTeam) { //IF ITS ON OUR TEAMMMMMMM!!!!!!!!!!!!!
						RobotInfo curr = player.myRC.senseRobotInfo(r);
						
						coSum = coSum.add(new V2d(curr.location)); //cohesion
						alSum = alSum.add(new V2d(curr.directionFacing)); //alignment
						
						if(player.myIntel.getLocation().distanceSquaredTo(currR.location)<separateDistance) {
							seSum.add(new V2d(curr.location.directionTo(selfL)));
						}
			
					}
				}
			}
			
			V2d cM = coSum.scale(1.0/robots.length); //Center of Mass
			
			vCohesion = cM.sub(selfV).scale(scaleCohesion);
			vAlignment = alSum.scale(scaleAlignment);
			vSeparation = seSum.scale(scaleSeparation);	
		} 		
		
		
		//////////////////////COMPUTATION AND UPDATE///////////////////
		V2d vTotal = vCohesion.add(vSeparation.add(vAlignment.add(vAvoidance))).add(vDestination);
		
		player.myRC.setIndicatorString(2, "Des: " + vDestination.toString());
		player.myRC.setIndicatorString(1, "Coh: " + vCohesion.toString());
		//player.myRC.setIndicatorString(2, "Sep: " + vSeparation.toString());
		
		MapLocation toGo = vTotal.add(selfV).toLoc();
		Direction toTurn = selfL.directionTo(toGo);
		
		player.myNavi.bugInDirection(toTurn);
	}
	
	
	/*public void goToLocation(MapLocation dest) throws GameActionException {
		Direction changeTo = destDirection(dest);
		if (changeTo != myDirection()) {
			this.myRC.setDirection(changeTo);
			this.myRC.yield();
		} else if (this.myRC.canMove(myDirection()) && myLocation() != dest){
			this.myRC.moveForward();
			this.myRC.yield();
		} else if (!this.myRC.canMove(myDirection())) {
			tracing = true;
			bugTo(dest);
		} else {
			this.myRC.yield();
		}
	}*/


}