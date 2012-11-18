package lazerguns1.strategies;

import lazerguns1.*;
import lazerguns1.behaviors.Behavior;
import lazerguns1.behaviors.SpawnChargeBehavior;
import lazerguns1.behaviors.SpawnWoutBehavior;
import lazerguns1.instincts.Instinct;
import lazerguns1.instincts.TransferInstinct;
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
