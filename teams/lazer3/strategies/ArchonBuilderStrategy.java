package lazer3.strategies;

import lazer3.MsgType;
import lazer3.RobotPlayer;
import lazer3.behaviors.Behavior;
import lazer3.behaviors.BuildCommBehavior;
import lazer3.behaviors.GoToBuildLocation;
import battlecode.common.GameActionException;

/**
 * Archon builds things
 * @author lazerpewpew
 *
 */
public class ArchonBuilderStrategy extends Strategy {
	private int state=0;
	
	Behavior gotoBehavior = new GoToBuildLocation(player,MsgType.MSG_BUILDTOWERHERE);
	Behavior buildBehavior = new BuildCommBehavior(player);
	
	
	public ArchonBuilderStrategy(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		if(buildBehavior.execute()) return true;
		if(player.myRadio.inbox.size()>0)
			return true;
		return false;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		/*
		 * state 0: going to location
		 * state 1: building tower;
		 */
		switch(state){
		case 0:
			if(gotoBehavior.execute()) {
				state=1;
			}
			break;
		case 1:
			buildBehavior.execute();
			state=0;
			break;
		}
	}

	@Override
	public void runInstincts() throws GameActionException {
		// None
		
	}
	
	
	
	

	
}
