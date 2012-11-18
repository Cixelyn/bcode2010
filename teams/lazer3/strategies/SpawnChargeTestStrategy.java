package lazer3.strategies;

import battlecode.common.*;
import lazer3.*;
import lazer3.behaviors.Behavior;
import lazer3.behaviors.SpawnChargeBehavior;
import lazer3.behaviors.SpawnWoutBehavior;
import lazer3.instincts.Instinct;
import lazer3.instincts.TransferInstinct;


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
