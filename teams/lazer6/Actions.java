package lazer6;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

/**
 * This class channels the spirit of Michael Jackson and allows your bot to move like a badass
 *  
 * <pre>
 * ∧＿∧　　 　 　
 * <#｀Д´ >　　　＜THIS CLASS MAKES YOU MOVE LIKE MICHAEL JACKSONNNN!
 * （　　　） 　
 * ｜ ｜　|　　　
 * 〈＿フ__フ
 *</pre>
 * @author Kevin Li
 *
 */
public class Actions {
	private final RobotController myRC;
	private final RobotPlayer player;
	
	
	public Direction lastHeading; 			//this is more or less a quick hack to allow bugnav use arbitrary directions
											//with the MJ move system.
											//If i had more time, i would abstract bugnav.  Unfortunantly, I don't.  ~Cory
	
	public Actions(RobotPlayer player) {
		this.myRC = player.myRC;
		this.player = player;
		lastHeading = myRC.getDirection();
	}

	/**
	 * move forward function wrapped in checks
	 * @param none
	 * @author Kevin Li
	 * @return true if moved forward, else false
	 */
	public boolean moveFWD() {
		try {
			if (myRC.getRoundsUntilMovementIdle()==0 && !myRC.hasActionSet() && myRC.canMove(myRC.getDirection())) {
				myRC.moveForward();
				return true;
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: moveFWD");
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * move backward function wrapped in checks
	 * @param none
	 * @author Kevin Li
	 * @return true if moved backward, else false
	 */
	public boolean moveBCK() {
		try {
			if (myRC.getRoundsUntilMovementIdle()==0 && !myRC.hasActionSet() && myRC.canMove(myRC.getDirection().opposite())) {
				myRC.moveBackward();
				return true;
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: moveBCK");
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * changes direction to the one given if possible
	 * @author Kevin Li
	 * @param dir
	 * @return true if changed direction, else false
	 */
	public boolean changeDir(Direction dir) {
		try {
			if (myRC.getRoundsUntilMovementIdle()==0 && !myRC.hasActionSet() && dir.ordinal()<8) {
				myRC.setDirection(dir);
				lastHeading = dir;
				return true;
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: changeDir");
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * move backwards in the given direction if the robot is facing the right way, else change direction to dir.opposite
	 * @author Kevin Li
	 * @param dir
	 * @return true if moved, else false
	 */
	public boolean backUpInDir(Direction dir) {
		try {
			if (myRC.getRoundsUntilMovementIdle()==0 && !myRC.hasActionSet() && dir.ordinal()<8) {
				if (myRC.getDirection().opposite().equals(dir)) {
					if (myRC.canMove(dir)) {
						myRC.moveBackward();
						return true;
					}
				} else {
					myRC.setDirection(dir.opposite());
					lastHeading = dir;
				}
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: backUpInDir");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * move in given direction if robot's direction is already that direction, else change direction to dir and return false
	 * @author Kevin Li
	 * @param dir
	 * @return true if moved in direction, else false
	 */
	public boolean moveInDir(Direction dir) {
		try {
			if (myRC.getRoundsUntilMovementIdle()==0 && !myRC.hasActionSet() && dir.ordinal()<8) {
				if (myRC.getDirection().equals(dir)) {
					if (myRC.canMove(dir)) {
						myRC.moveForward();
						return true;
					}
				} else {
					myRC.setDirection(dir);
					lastHeading = dir;
				}
			}
			
		} catch (GameActionException e) {
//			System.out.println("Action Exception: moveInDir");
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	
	/**
	 * Moves in a given direction like Michael Jackson <br><br>
	 * See the below ascii art for the facing direction when moving in a direction.
	 * The double arrow denotes the desired facing direction, and the single arrows denote the resulting
	 * orientation when taking a move in that particular direction
	 * <pre>
	 *    ↖ ↑ ↙
	 *    ⇐ o ←
	 *    ↙ ↓ ↖
  	 * </pre>
	 * 
	 * 
	 * @author Cory
	 * @param faceDir - Direction you want to keep facing
	 * @param moveDir - Direction you want to move in
	 * @return
	 */
	public boolean moveLikeMJ(Direction faceDir, Direction moveDir) {
		try {
			RobotController tMyRC = myRC;	//fast access variable
			if (tMyRC.getRoundsUntilMovementIdle()==0 && !tMyRC.hasActionSet() && moveDir.ordinal()<8) {	//can we move

				//first compute whether moveDir is "behind" faceDir
				Direction oppDir = faceDir.opposite();			//TODO inline these variables
				Direction moveDirR = moveDir.rotateRight();
				Direction moveDirL = moveDir.rotateLeft();

				if(oppDir==moveDir || oppDir==moveDirR || oppDir==moveDirL) { 
					if (tMyRC.getDirection().opposite().equals(moveDir)) { //backwards movement code
						if (tMyRC.canMove(moveDir)) {
							tMyRC.moveBackward();
							return true;
						}
					} else {
						myRC.setDirection(moveDir.opposite());
						lastHeading = moveDir;
					}					
				} else {//moveDir is in front of faceDir
					if (tMyRC.getDirection().equals(moveDir)) {
						if (tMyRC.canMove(moveDir)) {
							tMyRC.moveForward();
							return true;
						}
					} else {
						myRC.setDirection(moveDir);
						lastHeading = moveDir;
					}	
				}
			}
		} catch(GameActionException e) {
//			System.out.println("Action Exception: moveLikeMJ");
			e.printStackTrace();
		}
		return false;		
		
		
		
	}
	
	
	
	/**
	 * Moves in a given direction like Michael Jackson <br>
	 * This is a modified version of the standard moveLikeMJ in that moonwalking is right-angle inclusive. <br>
	 * This helps helps archons face the enemy while retreating
	 * <pre>
	 *    ↖ ↓ ↙
	 *    ⇐ o ←
	 *    ↙ ↑ ↖
  	 * </pre>
	 * 
	 * @author Cory
	 * @param faceDir - Direction you want to remain facing
	 * @param moveDir - Direction you want to move in
	 * @return true if moved in dir, else false
	 */
	public boolean moveLikeMJArchons(Direction faceDir, Direction moveDir) {
		try {
			RobotController tMyRC = myRC;	//fast access variable
			if (tMyRC.getRoundsUntilMovementIdle()==0 && !tMyRC.hasActionSet() && moveDir.ordinal()<8) {	//can we move

				//first compute whether moveDir is "ahead" of faceDir
				Direction faceDirR = faceDir.rotateRight();
				Direction faceDirL = faceDir.rotateLeft();

				if(moveDir==faceDir || moveDir==faceDirR || moveDir==faceDirL) { //moveDir is ahead of facedir

					if (tMyRC.getDirection().equals(moveDir)) { //forward movement code
						if (tMyRC.canMove(moveDir)) {
							tMyRC.moveForward();
							return true;
						}
					} else {
						myRC.setDirection(moveDir);
						lastHeading = moveDir;
					}					
				} else {//moveDir is behind facedir
					if (tMyRC.getDirection().opposite().equals(moveDir)) {
						if (tMyRC.canMove(moveDir)) {
							tMyRC.moveBackward();
							return true;
						}
					} else {
						myRC.setDirection(moveDir.opposite());
						lastHeading = moveDir;
					}	
				}
			}
		} catch(GameActionException e) {
//			System.out.println("Action Exception: moveLikeMJ");
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	/**
	 * attacks air at the given square if possible
	 * @author Kevin Li
	 * @param atkLoc
	 * @return true if successfully attacked air at location, else false
	 */
	public boolean shootAir(MapLocation atkLoc) {
		Direction myDir = myRC.getDirection();
		Direction enDir = myRC.getLocation().directionTo(atkLoc);
		try {
			if (myRC.getRoundsUntilAttackIdle()==0 && !myRC.hasActionSet()) {
				if (myRC.canAttackSquare(atkLoc)) {
					myRC.attackAir(atkLoc);
					return true;
				} else if (myDir != enDir) {
					changeDir(enDir);
					return false;
				}
			} 
		} catch (GameActionException e) {
//			System.out.println("Action Exception: shootAir");
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * attacks ground at the given square if possible
	 * @author Kevin Li
	 * @param atkLoc
	 * @return true if successfully attacked ground at location, else false
	 */
	public boolean shootGround(MapLocation atkLoc) {
		Direction myDir = myRC.getDirection();
		Direction enDir = myRC.getLocation().directionTo(atkLoc);
		try {
			if (myRC.getRoundsUntilAttackIdle()==0 && !myRC.hasActionSet()) {
				if (myRC.canAttackSquare(atkLoc)) {
					myRC.attackGround(atkLoc);
					return true;
				} else if (myDir != enDir) {
					changeDir(enDir);
					return false;
				}
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: shootGround");
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	/**
	 * 
	 */
	
	public boolean shootTarget(MapLocation atkLoc, boolean isAir) {
		Direction myDir = myRC.getDirection();
		Direction enDir = myRC.getLocation().directionTo(atkLoc);
		
		try {
			if (myRC.getRoundsUntilAttackIdle()==0 && !myRC.hasActionSet()) {
				if (myRC.canAttackSquare(atkLoc)) {
					if(isAir) {
						myRC.attackAir(atkLoc);
					}else {
						myRC.attackGround(atkLoc);
					}
					return true;
				} else if (myDir != enDir) {
					changeDir(enDir);
					return false;
				}
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: shootGround");
			e.printStackTrace();
		}
		
		
		
		return false;
	}
	
	
	
	
	
	
	
	/**
	 * spawns the specified unit type if possible, if not it will rotate to find a space that it can spawn on
	 * @author Kevin Li
	 * @param type
	 * @return true if successfully spawned, else false
	 */
	public boolean spawn(RobotType type) {
		try {
			MapLocation myLoc = myRC.getLocation();
			MapLocation spawnLoc = myLoc.add(myRC.getDirection());
			if (!myRC.hasActionSet()) {
				if (myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND && myRC.senseGroundRobotAtLocation(spawnLoc) == null) {
					if (canSupportAnother(type)) {
						myRC.spawn(type);
					}
					player.myProfiler.numUnitsSpawned[type.ordinal()]++;
					return true;
				} else {
					
					Direction[] possibleDirections = Direction.values();
					for(int i=0; i<7; i++) {
						spawnLoc = myLoc.add(possibleDirections[i]);
						
						//FIXME replace this with the new adjacency system provided in battleprofiler to save bytecodes
						if (myRC.senseGroundRobotAtLocation(spawnLoc) == null &&  //if we can spawn the soldier.
								myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND){
							changeDir(possibleDirections[i]);
							return false;
						}
					}
				}
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: spawn");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Tries to spawn robot in space in front of it and does not rotate if it can't spawn
	 * @param type
	 * @return
	 */
	public boolean spawnInPlace(RobotType type){
		try {
			MapLocation spawnLoc = myRC.getLocation().add(myRC.getDirection());
			if (!myRC.hasActionSet()) {
				if (myRC.senseTerrainTile(spawnLoc).getType() == TerrainType.LAND && myRC.senseGroundRobotAtLocation(spawnLoc) == null) {
					if (canSupportAnother(type)) {
						myRC.spawn(type);
						player.myProfiler.numUnitsSpawned[type.ordinal()]++;
						return true;
					}
				} else {
					return false;
				}
			}
		} catch (GameActionException e) {
//			System.out.println("Action Exception: spawn");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * helper function for spawning to see if we can physically upkeep this additional unit
	 * @param type
	 * @return
	 */
	public boolean canSupportAnother(RobotType type) {

		return (0.5D * (player.myProfiler.alliedArchons.length + 1) >= 
			RobotType.WOUT.energonUpkeep() * player.myProfiler.numAlliedUnitsSensed[1] + 
			RobotType.CHAINER.energonUpkeep() * player.myProfiler.numAlliedUnitsSensed[2] + 
			RobotType.SOLDIER.energonUpkeep() * player.myProfiler.numAlliedUnitsSensed[3] +
			type.energonUpkeep());
	}
	
	
}
