package lazer6.instincts;

import lazer6.Actions;
import lazer6.BattleProfiler;
import lazer6.Broadcaster;
import lazer6.Navigation;
import lazer6.RobotPlayer;
import lazer6.Utilities;
import battlecode.common.RobotController;


public abstract class Instinct {
	protected final RobotPlayer player;
	protected final RobotController myRC;
	protected final Actions myAct;
	protected final BattleProfiler myProfiler;
	protected final Broadcaster myRadio;
	protected final Navigation myNavi;
	protected final Utilities myUtils;
	
	public Instinct(RobotPlayer player) {
		this.player = player;
		this.myRC = player.myRC;
		this.myAct = player.myAct;
		this.myProfiler = player.myProfiler;
		this.myRadio = player.myRadio;
		this.myNavi = player.myNavi;
		this.myUtils = player.myUtils;
	}
	
	public abstract void execute();


}
