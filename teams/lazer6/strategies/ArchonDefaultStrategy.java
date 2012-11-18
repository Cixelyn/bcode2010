package lazer6.strategies;

import lazer6.RobotPlayer;
import battlecode.common.MapLocation;

public class ArchonDefaultStrategy extends Strategy {
	
	MapLocation[] archonList;
	int myID = -1;
	
	public ArchonDefaultStrategy(RobotPlayer player) {
		super(player);
		myProfiler.setScanMode(false, false, false, true);
	}

	@Override
	public boolean beginStrategy(){
		myID = myProfiler.myArchonID;

		switch(myID) {
		case 0:
			player.changeStrategy(new NewArchonBuilderStrategy(player));
			return true;
		/*case 5:
			player.changeStrategy(new NewArchonStrategy(player));
			return true;*/
		default:
			player.changeStrategy(new FinalArchonStrategy(player));
			return true;
		}

	}

	@Override
	public void runBehaviors(){
		
	}

	@Override
	public void runInstincts() {
		
	}
}
	