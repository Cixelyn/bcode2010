package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.BuildCommBehavior;
import lazer5.behaviors.GoToBuildLocation;
import lazer5.behaviors.MoveFWDBehavior;
import lazer5.behaviors.ReturnToLastTowerBehavior;
import lazer5.behaviors.SpawnChargeBehavior;
import lazer5.behaviors.SpawnWoutBehavior;
import lazer5.communications.MsgType;
import lazer5.instincts.Instinct;
import lazer5.instincts.RevertedTransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile.TerrainType;

/**
 * Archon builds things
 * @author lazerpewpew
 *
 */
public class ArchonBuilderStrategy extends Strategy {
	
	
	//Behavior Instantiations
	private final Behavior gotoBehavior = new GoToBuildLocation(player,MsgType.MSG_BUILDTOWERHERE);
	private final BuildCommBehavior buildBehavior = new BuildCommBehavior(player);
	private final Behavior spawnBehavior = new SpawnWoutBehavior(player);
	private final Behavior chargeBehavior = new SpawnChargeBehavior(player);
	private final Behavior moveBehavior = new MoveFWDBehavior(player);
	private final Behavior returnToLastTower = new ReturnToLastTowerBehavior(player);
	private final Instinct upkeepInstinct = new RevertedTransferInstinct(player);
	
	
	//Other variables
	private int roundOfLastBuild;
	private MapLocation lastCommLocation;
	private int stuckInBuild = 0;
	private int movingCounter = 0;
	
	//State Variables
	private int state = 3;
	
	
	//Game Constants
	private static double INITAL_WOUT_SPAWN_THRESHOLD = 50.0;
	private static double WOUT_SPAWN_THRESHOLD = 50.0;
//	private final int MAX_ROUNDS_SINCE_LAST_BUILD = 100;
	
	public ArchonBuilderStrategy(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		if( player.myRC.getFlux()>3000) {
			if(buildBehavior.execute()) {
				return true;
			}
		}
		else if( player.myRC.getEnergonLevel()>INITAL_WOUT_SPAWN_THRESHOLD ) {
			spawnBehavior.execute();
			return false;
		}
		
		return false;
	}

	public void runBehaviors() throws GameActionException {
		
		player.myRC.setIndicatorString(2, Integer.toString(state));
		
//		if(roundOfLastBuild !=0 && Clock.getRoundNum()-roundOfLastBuild > MAX_ROUNDS_SINCE_LAST_BUILD){
//			state = 9;
//		}
		if(isSurroundedByVoid(player.myRC.getLocation())){
			if(!player.myRC.canMove(player.myRC.getDirection())){
				if(player.myRC.getRoundsUntilMovementIdle()==0 && !player.myRC.hasActionSet()){
					player.myRC.setDirection(player.myRC.getDirection().opposite());
					return;
				}
			}
			moveBehavior.execute();
			return;
		}
		
		if(player.myRC.getEnergonLevel() > 60.0){
			state = 1;
		}
		
		if(player.myRC.getFlux() > 5000 ){
//			System.out.println("force build");
			buildBehavior.execute();
//			state = 4;
		}
		if(stuckInBuild>20){
//			System.out.println("stuck, reset counter");
			stuckInBuild=0;
			state = 5;
		}
		if(state!=4){
			stuckInBuild = 0;
		}

//		if(player.myRC.getEnergonLevel() < WOUT_SPAWN_THRESHOLD || player.myRC.getFlux() < 3000){
//			state = 10;
//		}
		
		/*
		 * 
		 * state 1: spawn wout
		 * state 2: charge wout
		 * state 3: goto location
		 * state 4: build comm tower
		 * state 5: choose rand direction (for buildings)
		 * state 6: move forward (for state 5)
		 * state 7: choose rand direction (for spawning)
		 * state 8: move forward (for state 7)
		 * state 9: go back to last build location
		 * state 10: idle
		 */
		switch(state) {
		case 1:
			if (player.myRC.getRoundsUntilMovementIdle()==0 && player.myRC.hasActionSet() == false) {
				if(spawnBehavior.execute()) {
					state = 2;
				}else{
					state = 8;
				}
			}
			else{
				state=3;
			}
			break;
		case 2:
			if(chargeBehavior.execute()) {
				state=3;
			}
			break;
		case 3:
			if(gotoBehavior.execute() && player.myRC.getFlux()>3000) {
				state=4;
			}else if(player.myRC.getEnergonLevel()>WOUT_SPAWN_THRESHOLD) {
				state = 1;
			}
			break;
		case 4:
			stuckInBuild++;
//			if (player.myRC.getRoundsUntilMovementIdle()==0 && !player.myRC.hasActionSet()){
//				if(buildBehavior.execute()){
//					state = 4;
//				}else{
//					state = 5;
//				}
//			}
			if(player.myRC.getFlux()<3000){
				state = 3;
				break;
			}
			
			if (player.myRC.getRoundsUntilMovementIdle()==0 && !player.myRC.hasActionSet()) {
				MapLocation target = player.myRC.getLocation().add(player.myRC.getDirection());
				if (buildBehavior.execute()) {
					//remember where we put last comm tower
					MapLocation lastComm = buildBehavior.getTarget();
					if(lastComm != null){
						lastCommLocation = lastComm;
					}
					roundOfLastBuild = Clock.getRoundNum();
					state = 4;
				} else if(!tileOpen(target)){
					state = 5;
				}else{
//					state = 3;
					state = 5;
				}
			}
			break;
		case 5:
			if (!player.myRC.canMove(player.myRC.getDirection()) || movingCounter>2) {
				Direction dir = player.myUtils.randDir();
				if (player.myRC.getRoundsUntilMovementIdle() == 0) {
					player.myRC.setDirection(dir);
					movingCounter = 0;
					state = 6;
				}
			}else{
				state = 6;
			}
			break;
		case 6:
			movingCounter++;
			if (player.myRC.getRoundsUntilMovementIdle()==0 && !player.myRC.hasActionSet()) {
				if (player.myRC.canMove(player.myRC.getDirection())) {
						state = 4;
					player.myNavi.moveInDirection(player.myRC.getDirection());
				} else {
					state = 5;
				}
			}
			break;
		case 7:
			Direction dir2 = player.myUtils.randDir();
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				player.myRC.setDirection(dir2);
				state = 8;
			}
			break;
		case 8:
			if(moveBehavior.execute()){
				state = 1;
			}else{
				state =7;
			}
			break;
		case 9:
			if(returnToLastTower.execute()){
				state = 3;
			}
			break;
		case 10:
			if(player.myRC.getFlux() > 3000.0){
				state =3;
			}
			else if(player.myRC.getEnergonLevel() > WOUT_SPAWN_THRESHOLD){
				state =1;
			}
		}
	}

	@Override
	public void runInstincts() throws GameActionException {
		upkeepInstinct.execute();		
	}
	
	
	
	
	/**
	 * returns true if we can spawn something in the ground in front of myRobot
	 * 
	 * @param loc - location of robot
	 * @param dir - direction of robot
	 * @return true if no robot in front of myRobot and tile in front is a land
	 * @throws GameActionException
	 */
	public boolean tileOpen(MapLocation dest) throws GameActionException{
		//if no ground robot in front and tile in front is a land, return true
		if(player.myRC.senseGroundRobotAtLocation(dest)==null &&
				player.myRC.senseTerrainTile(dest).getType()==TerrainType.LAND){
			return true;
		}
		return false;
	}
	
	public boolean hasAdjacentTower(MapLocation loc) throws GameActionException{
		Direction dir;
		for(int i=0; i<8; i++){
			dir = Direction.values()[i];
			Robot rob = player.myRC.senseGroundRobotAtLocation(loc.add(dir));
			if ((rob != null) && (player.myRC.canSenseObject(rob))) {
				if (player.myRC.senseRobotInfo(rob).type == RobotType.COMM) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSurroundedByVoid(MapLocation location){
		MapLocation adj;
		for(int i=8; --i>=0;){
			adj = location.add(Direction.values()[i]);
			if(player.myRC.senseTerrainTile(adj).getType() == TerrainType.LAND){
				return false;
			}
		}
		return true;
	}
	
	public boolean locTooCloseToOtherTowers(MapLocation center) throws GameActionException{
		if(hasAdjacentTower(center)) return true;
		MapLocation[] nearbyTiles = new MapLocation[40];
		nearbyTiles[0] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST);
		nearbyTiles[1] = center.add(Direction.NORTH).add(Direction.NORTH_WEST);
		nearbyTiles[2] = center.add(Direction.NORTH).add(Direction.NORTH);
		nearbyTiles[3] = center.add(Direction.NORTH).add(Direction.NORTH_EAST);
		nearbyTiles[4] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST);
		nearbyTiles[5] = center.add(Direction.NORTH_EAST).add(Direction.EAST);
		nearbyTiles[6] = center.add(Direction.EAST).add(Direction.EAST);
		nearbyTiles[7] = center.add(Direction.EAST).add(Direction.SOUTH_EAST);
		nearbyTiles[8] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST);
		nearbyTiles[9] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH);
		nearbyTiles[10] = center.add(Direction.SOUTH).add(Direction.SOUTH);
		nearbyTiles[11] = center.add(Direction.SOUTH).add(Direction.SOUTH_WEST);
		nearbyTiles[12] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST);
		nearbyTiles[13] = center.add(Direction.SOUTH_WEST).add(Direction.WEST);
		nearbyTiles[14] = center.add(Direction.WEST).add(Direction.WEST);
		nearbyTiles[15] = center.add(Direction.WEST).add(Direction.NORTH_WEST);
		
		
//		nearbyTiles[16] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.NORTH_WEST);
//		nearbyTiles[17] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.NORTH);
//		nearbyTiles[18] = center.add(Direction.NORTH_WEST).add(Direction.NORTH).add(Direction.NORTH);
//		nearbyTiles[19] = center.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH);
//		nearbyTiles[20] = center.add(Direction.NORTH_EAST).add(Direction.NORTH).add(Direction.NORTH);
//		nearbyTiles[21] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.NORTH);
//		nearbyTiles[22] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.NORTH_EAST);
//		
//		nearbyTiles[23] = center.add(Direction.NORTH_EAST).add(Direction.NORTH_EAST).add(Direction.EAST);
//		nearbyTiles[24] = center.add(Direction.NORTH_EAST).add(Direction.EAST).add(Direction.EAST);
//		nearbyTiles[25] = center.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST);
//		nearbyTiles[26] = center.add(Direction.SOUTH_EAST).add(Direction.EAST).add(Direction.EAST);
//		nearbyTiles[27] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.EAST);
//		nearbyTiles[28] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST);
//		
//		nearbyTiles[29] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH_EAST).add(Direction.SOUTH);
//		nearbyTiles[30] = center.add(Direction.SOUTH_EAST).add(Direction.SOUTH).add(Direction.SOUTH);
//		nearbyTiles[31] = center.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH);
//		nearbyTiles[32] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH).add(Direction.SOUTH);
//		nearbyTiles[33] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.SOUTH);
//		nearbyTiles[34] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST);
//		
//		nearbyTiles[35] = center.add(Direction.SOUTH_WEST).add(Direction.SOUTH_WEST).add(Direction.WEST);
//		nearbyTiles[36] = center.add(Direction.SOUTH_WEST).add(Direction.WEST).add(Direction.WEST);
//		nearbyTiles[37] = center.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST);
//		nearbyTiles[38] = center.add(Direction.NORTH_WEST).add(Direction.WEST).add(Direction.WEST);
//		nearbyTiles[39] = center.add(Direction.NORTH_WEST).add(Direction.NORTH_WEST).add(Direction.WEST);
		
		for(int i=0; i<16; i++){
			Robot rob = player.myRC.senseGroundRobotAtLocation(nearbyTiles[i]);
			if ((rob != null) && (player.myRC.canSenseObject(rob))) {
				if (player.myRC.senseRobotInfo(rob).type == RobotType.COMM) {
					return true;
				}
			}
		}
		return false;
	}
	
}
