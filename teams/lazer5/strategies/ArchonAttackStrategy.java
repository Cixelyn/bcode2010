package lazer5.strategies;

import lazer5.RobotPlayer;
import lazer5.behaviors.ArchonMoveToEnemyBehavior;
import lazer5.behaviors.Behavior;
import lazer5.behaviors.SpawnChargeBehavior;
import lazer5.behaviors.SpawnSoldierBehavior;
import lazer5.instincts.Instinct;
import lazer5.instincts.RevertedTransferInstinct;
import battlecode.common.GameActionException;

public class ArchonAttackStrategy extends Strategy {
	private Behavior moveToEnemy = new ArchonMoveToEnemyBehavior(player);
	private Behavior spawnSoldier = new SpawnSoldierBehavior(player);
	private Behavior charge = new SpawnChargeBehavior(player);
	private Instinct Transfer = new RevertedTransferInstinct(player);
	
	private int state = 0;

	public ArchonAttackStrategy(RobotPlayer player) {
		super(player);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		
		switch(state){
		case 0:
			if(player.myRC.getEnergonLevel() > 50){
				state = 1;
			}
			moveToEnemy.execute();
			break;
		case 1:
			if(spawnSoldier.execute()){
				state = 2;
			}
			else state = 0;
			break;
		case 2:
			if(charge.execute()){
				state = 0;
			}
			break;
		
		}
		
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		Transfer.execute();
	}


}
