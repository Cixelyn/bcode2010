package lazerguns1.strategies;

import lazerguns1.MsgType;
import lazerguns1.RobotPlayer;
import lazerguns1.behaviors.Behavior;
import lazerguns1.behaviors.BuildCommBehavior;
import lazerguns1.behaviors.GoToBuildLocation;
import lazerguns1.behaviors.MoveFWDBehavior;
import lazerguns1.behaviors.SpawnChargeBehavior;
import lazerguns1.behaviors.SpawnWoutBehavior;
import lazerguns1.instincts.Instinct;
import lazerguns1.instincts.TransferInstinct;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

/**
 * Archon builds things
 * @author lazerpewpew
 *
 */
public class ArchonBuilderStrategy extends Strategy {
	
	
	//Behavior Instantiations
	private final Behavior gotoBehavior = new GoToBuildLocation(player,MsgType.MSG_BUILDTOWERHERE);
	private final Behavior buildBehavior = new BuildCommBehavior(player);
	private final Behavior spawnBehavior = new SpawnWoutBehavior(player);
	private final Behavior chargeBehavior = new SpawnChargeBehavior(player);
	private final Behavior moveBehavior = new MoveFWDBehavior(player);
	private final Instinct upkeepInstinct = new TransferInstinct(player);
	
	
	//State Variables
	private int state = 3;
	
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
		 * state 3: goto location
		 * state 4: build comm tower
		 * state 5: choose rand direction (for buildings)
		 * state 6: move forward (for state 5)
		 * state 7: choose rand direction (for spawning)
		 * state 8: more forward (for state 7)
		 */
		switch(state) {
		case 1:
			if(spawnBehavior.execute()) {
				state = 2;
			}else{
				state = 7;
			}
			break;
		case 2:
			if(chargeBehavior.execute()) {
				state=3;
			}
			break;
		case 3:
			if(gotoBehavior.execute()) {
				state=4;
			}else if(player.myRC.getEnergonLevel()>50.0) {
				state = 1;
			}
			break;
		case 4:
			if(buildBehavior.execute()){
				state=4;
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
			if(moveBehavior.execute()){
				state = 4;
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
			if(moveBehavior.execute()){
				state = 1;
			}else{
				state =7;
			}
			break;
			
			
		}
	}

	@Override
	public void runInstincts() throws GameActionException {
		upkeepInstinct.execute();		
	}
	
	
	
	

	
}