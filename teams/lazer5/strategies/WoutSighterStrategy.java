package lazer5.strategies;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import lazer5.RobotPlayer;
import lazer5.communications.MsgType;

public class WoutSighterStrategy extends Strategy {

	public WoutSighterStrategy(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void runBehaviors() throws GameActionException {
		player.myRC.setIndicatorString(0, player.myRC.getLocation().toString());
		if (Clock.getRoundNum() % 10 == 0) {
			player.myRC.setIndicatorString(2, "BC DATA");
			player.myRadio.sendRobotList(player.myIntel.getNearbyRobots());
		} else if (Clock.getRoundNum() % 10 == 7) {
			player.myRC.setIndicatorString(2, "BC JIHAD");
			player.myRadio.sendSingleNotice(MsgType.MSG_BEGINJIHAD);
		}
		
	}

	@Override
	public void runInstincts() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

}
