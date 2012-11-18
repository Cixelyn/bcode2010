package lazerguns2.strategies;

import lazerguns2.*;
import lazerguns2.behaviors.Behavior;
import lazerguns2.behaviors.SpawnChargeBehavior;
import lazerguns2.behaviors.SpawnWoutBehavior;
import lazerguns2.instincts.Instinct;
import lazerguns2.instincts.TransferInstinct;
import battlecode.common.*;


public class SpawnChargeTestStrategy extends Strategy{
	private Behavior Spawn, Charge;
	private Instinct Upkeep;
	private int state = 0;
	private boolean newSpawnCharged;
	public SpawnChargeTestStrategy(RobotPlayer player) {
		super(player);
		Spawn = new SpawnWoutBehavior(player);
		Charge = new SpawnChargeBehavior(player);
		Upkeep = new TransferInstinct(player);
	}
	public void runInstincts() throws GameActionException {
		Upkeep.execute();
	}
	public void runBehaviors() throws GameActionException {
		//reflexive behaviors
		
		/*
		 * state 0 = spawn wout
		 * state 1 = charge it
		 */
		
		switch(state) {
		case 0: 
			if (player.myRC.getEnergonLevel() > 50) {
				Spawn.execute(); 
				newSpawnCharged = false;
			}
			break;
		case 1: 
			newSpawnCharged = Charge.execute(); 
			break;
		}
		
		if (newSpawnCharged != false) {
			state = 0;
		}
	}
	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		return true;
	}
}
