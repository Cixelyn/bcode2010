package lazerguns2.strategies;

import lazerguns2.MsgType;
import lazerguns2.RobotPlayer;
import lazerguns2.behaviors.Behavior;
import lazerguns2.behaviors.BuildCommBehavior;
import lazerguns2.behaviors.CalcBuildLocationBehavior;
import lazerguns2.behaviors.MoveFWDBehavior;
import lazerguns2.behaviors.MoveToBuildLocationBehavior;
import lazerguns2.behaviors.SpawnChargeBehavior;
import lazerguns2.behaviors.SpawnWoutBehavior;
import lazerguns2.instincts.Instinct;
import lazerguns2.instincts.TransferInstinct;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

/**
 * Archon builds things
 * @author lazerpewpew
 *
 */
public class NewArchonBuilderStrategy extends Strategy {
	
	private MapLocation buildDest;
	private int closestDist = 10000;
	
	
	//Behavior Instantiations
	private MoveToBuildLocationBehavior gotoBehavior = new MoveToBuildLocationBehavior(player,buildDest);
	private final CalcBuildLocationBehavior calcLocBehavior = new CalcBuildLocationBehavior(player, MsgType.MSG_BUILDTOWERHERE);
	private final Behavior buildBehavior = new BuildCommBehavior(player);
	private final Behavior spawnBehavior = new SpawnWoutBehavior(player);
	private final Behavior chargeBehavior = new SpawnChargeBehavior(player);
	private final Behavior moveFwdBehavior = new MoveFWDBehavior(player);
	private final Instinct upkeepInstinct = new TransferInstinct(player);
	
	
	//State Variables
	private int state = 2;
	
	public NewArchonBuilderStrategy(RobotPlayer player) {
		super(player);
		buildDest = new MapLocation(0,0);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		if( player.myRC.getFlux()>3000) {
			if(buildBehavior.execute()) {
				return true;
			}
		}
		else if( player.myRC.getEnergonLevel()>50.0 ) {
			spawnBehavior.execute();
			return false;
		}
		
		return false;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		
		player.myRC.setIndicatorString(2, Integer.toString(state));

		/*
		 * state 1: spawn wout
		 * state 2: charge wout
		 * state 9: calculate build location to move to
		 * state 3: goto location
		 * state 4: build comm tower
		 * state 5: choose rand direction (for buildings)
		 * state 6: move forward (for state 5)
		 * state 7: choose rand direction (for spawning) - obsolete, cory implemented a choose valid direction inf
		 * spawn wout behavior if can't spawn, will still return false
		 * state 8: more forward (for state 7)
		 * state 9: calculate destination
		 * state 10: calculate destination - to go to without interrupts (state 11) - not implemented
		 * state 11: go to building direction with no spawning interrruption - not implemented
		 */
		if(buildDest.getX()==0 && buildDest.getY()==0) state = 9;
//		if(player.myRC.getFlux()>=7000) state = 9;
		
		switch(state) {
		case 1:
			if(spawnBehavior.execute()) {
				state = 2;
			}else{
				state = 8;
			}
			break;
		case 2:
			if(chargeBehavior.execute()) {
				state=9;
			}
			break;
		case 3:
			if(gotoBehavior.execute()) {
				state=4;
			}else if(player.myRC.getLocation().distanceSquaredTo(buildDest) > closestDist){
				state = 9;
			}else if(player.myRC.getEnergonLevel()>50.0) {
				state = 1;
			}
			break;
		case 4:
			if(buildBehavior.execute()){
				state=9;
			}else{
				state = 5;
			}
			break;
		case 5:
			Direction dir = player.myUtils.randDir();
			if (player.myRC.getRoundsUntilMovementIdle()==0) {
				player.myRC.setDirection(dir);
				state = 6;
			}
			break;
		case 6:
			if(moveFwdBehavior.execute()){
				state = 9;
			}else{
				state =5;
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
			if(moveFwdBehavior.execute()){
				state = 1;
			}else{
				state =7;
			}
			break;
		case 9:
			buildDest = calcLocBehavior.getDest();
			closestDist = player.myRC.getLocation().distanceSquaredTo(buildDest);
			gotoBehavior = new MoveToBuildLocationBehavior(player, buildDest);
			state=3;
			break;			
		}
	}

	@Override
	public void runInstincts() throws GameActionException {
		upkeepInstinct.execute();		
	}
	
	
	
	

	
}
