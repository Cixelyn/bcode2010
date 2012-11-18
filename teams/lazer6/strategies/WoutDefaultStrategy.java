package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import battlecode.common.Clock;
import battlecode.common.Message;

public class WoutDefaultStrategy extends Strategy {
	private int initialRound;
	public WoutDefaultStrategy(RobotPlayer player) {
		super(player);
		initialRound = Clock.getRoundNum();
	}

	@Override
	public boolean beginStrategy() {
		return true;
	}

	@Override
	public void runBehaviors() {
		 Message m;
			MsgType type;
			int i = 0;
			while (myRadio.inbox[i] != null) {
				m = myRadio.inbox[i];
				type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
				if (type == MsgType.MSG_BUILDERWOUTLOC){
					player.changeStrategy(new WoutBuildStrategy(player, m.locations[Broadcaster.firstData]));
					return;
				}
				if (type == MsgType.MSG_BATTERYWOUT) {
					//TODO add this in player.changeStrategy(new WoutBatteryStrategy(player));
					//return;
				}
				i++;
			}
			if (Clock.getRoundNum() - initialRound >= 2) {
				player.changeStrategy(new RushWoutStrategy(player));
				return;
			}
			
	}

	@Override
	public void runInstincts() {

	}

}
