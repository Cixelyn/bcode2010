package lazer2;

import battlecode.common.*;
import battlecode.common.TerrainTile.TerrainType;
import lazer2.*;

public class Navigation {
	public RobotPlayer player;
	public RobotController myRC;
	public boolean tracing;
	public boolean traceRight;
	public boolean directionTracing;
	public Navigation(RobotController rc) {
		this.myRC = rc;
		this.tracing = false;
		this.traceRight = true;
		this.directionTracing = false;

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
				this.myRC.yield();
				if (this.myRC.canMove(dir))
					this.myRC.moveForward();
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
	protected Direction deadReckon(Direction dir)
	  {
	    if (this.myRC.canMove(dir))
	      return dir;
	    if (this.myRC.canMove(dir.rotateRight()))
	      return dir.rotateRight();
	    if (this.myRC.canMove(dir.rotateLeft())) {
	      return dir.rotateLeft();
	    }
	    return dir;
	  }
	
	public void bugInDirection(Direction dir) throws GameActionException {
		if (dir.equals(Direction.NONE)) return;
	    if (this.directionTracing) {
	      if (this.myRC.canMove(dir))
	      {
	        this.directionTracing = false;
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

	    } else {
	      Direction nextDir = deadReckon(dir);
	      if (this.myRC.canMove(nextDir)) {
	        moveInDirection(nextDir);
	      } else {
	        setTraceRight(nextDir);
	        this.directionTracing = true;
	      }
	    }
	}
	public void bugTo(MapLocation dest) throws GameActionException {
		if (!(this.myRC.getLocation().equals(dest))) {
			Direction dir = destDirection(dest);
			if (dir.equals(Direction.NONE)) return;
			if (this.tracing) {
				if (this.myRC.canMove(dir)) {
					this.tracing = false;
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
			} else {
				Direction nextDir = deadReckon(dir);
				if (this.myRC.canMove(nextDir)) {
					moveInDirection(nextDir);
				} else {
					setTraceRight(nextDir);
					this.tracing = true;
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