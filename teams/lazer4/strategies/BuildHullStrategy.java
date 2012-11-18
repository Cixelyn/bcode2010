package lazer4.strategies;

import lazer4.RobotPlayer;
import lazer4.behaviors.Behavior;
import lazer4.behaviors.BuildHullBehavior;
import lazer4.behaviors.MoveFWDBehavior;
import lazer4.behaviors.RandomMoveBehavior;
import battlecode.common.GameActionException;

public class BuildHullStrategy extends Strategy {
	private Behavior build, setDir, move;
	private int state = 0;
	

	//change all code to be in a behavior so we can switch between checking/laying down towers
	//and between moving/changing direction
	public BuildHullStrategy(RobotPlayer player) {
		super(player);
		build = new BuildHullBehavior(player);
		setDir = new RandomMoveBehavior(player);
		move = new MoveFWDBehavior(player);
	}

	@Override
	public void runBehaviors() throws GameActionException {
		/*
		 * state 0: build hull
		 * state 1: set rand direction
		 * state 2: move fwd
		 */
		switch(state){
		case 0:
			build.execute();
			state=1;
			break;
		case 1:
			setDir.execute();
			state=2;
			break;
		case 2:
			if(move.execute()) state=0;
			break;
		}
		
	}

	@Override
	public void runInstincts() throws GameActionException {

	}

	@Override
	public boolean beginStrategy() throws GameActionException{
		return true;
	}
}
