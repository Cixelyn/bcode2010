package lazer3.strategies;
import lazer3.RobotPlayer;
import lazer3.behaviors.Behavior;
import lazer3.behaviors.MoveBCKBehavior;
import lazer3.behaviors.MoveFWDBehavior;
import battlecode.common.GameActionException;

public class defaultStrategy extends Strategy{
	private Behavior moveFWD, moveBCK;
	private int state = 0;
	
	public defaultStrategy(RobotPlayer player) {
		super(player);
		moveFWD = new MoveFWDBehavior(player);
		moveBCK = new MoveBCKBehavior(player);
	}
	
	public void runInstincts() throws GameActionException {
		//Add your instincts here.
		//	These instincts are always run right after behaviors, and right before the yield.
		
	}
	
	public void runBehaviors() throws GameActionException {
		/*
		Reflexive behaviors go here:
			eg: if i get an enemy broadcast, attack enemy.
		
		State machine executes here:
			if state == moving:
				tracewallbehavior.execute
			if state == fleeing
				fleebehavior.execute
			if state == defend
				movetowardsgroupbehavior.execute
			if state == building army
				new behavior(spawn units).run()
				
		State transitions go here
			if under heavy fire
				state = run
				
		Crazy strategy transitions here
			if i see lots of towers
				player.changeStrategy(new Offensive Strategy)
		*/
//		
//		/*
//		 * state 0: move forward
//		 * state 1: move backward
//		 */
//		if(state==0)
//			moveFWD.runActions();
//		else if(state==1)
//			moveBCK.runActions();
//		
//		if(state==0)
//			state=1;
//		else if(state==1)
//			state=0;
		
		
		
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		return true;
	}
	
	
}
