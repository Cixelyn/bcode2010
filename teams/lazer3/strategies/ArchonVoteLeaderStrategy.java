package lazer3.strategies;

import lazer3.*;
import battlecode.common.*;



//This strategy performs a vote and then decides on what strategy to do
public class ArchonVoteLeaderStrategy extends Strategy {
	
	public ArchonVoteLeaderStrategy(RobotPlayer player) {
		super(player);
	}

	
	public void runInstincts() throws GameActionException {
	}
	
	
	@Override
	public void runBehaviors() throws GameActionException {
		
		//WITH THIS BEHAVIOR, LOWEST ID BECOMES LEADER!		
		for(Message m:player.myRadio.inbox) {
			int self = player.myRC.getRobot().getID();			
			
			if(m.ints[0]==MsgType.MSG_VOTELEADER.ordinal()) {				
				if(m.ints[1] < self) {
					player.changeStrategy(new DebugPrintStrategy(player,"Not Leader"));
					return;
				}
			}
		}
		player.changeStrategy(new DebugPrintStrategy(player, "Leader"));
		return;

	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		player.myRadio.sendSingleNotice(MsgType.MSG_VOTELEADER);
		return true;
	}

	
	

}
