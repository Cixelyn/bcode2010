package lazerguns1.strategies;

import lazerguns1.RobotPlayer;
import lazerguns1.behaviors.ArchonGetSwarmDirection;
import lazerguns1.behaviors.Behavior;
import lazerguns1.behaviors.MoveFWDBehavior;
import lazerguns1.instincts.Instinct;
import lazerguns1.instincts.TransferInstinct;
import battlecode.common.GameActionException;


public class StupidArchonSwarmStrategy extends Strategy {
	private Instinct transfer;
	
	private Behavior swarm, move;
	
	private int state = 0;
	
	public StupidArchonSwarmStrategy(RobotPlayer player){
		super(player);
		transfer = new TransferInstinct(player);
		swarm = new ArchonGetSwarmDirection(player);
		move = new MoveFWDBehavior(player);
	}
	

	@Override
	public void runBehaviors() throws GameActionException {
		/*
		 * state 0: start
		 * state 1: settingDirection
		 * state 2: moving
		 */
		switch(state){
		case 0:
			if(swarm.execute())
				state=1;
			break;
		case 1:
			if(move.execute())
				state=2;
			break;
		case 2:
			if(swarm.execute())
				state=1;
			else move.execute();
			break;
		}
//		swarm.execute();
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		transfer.execute();
	}
	@Override
	public boolean beginStrategy() throws GameActionException{
		return true;
	}

}
