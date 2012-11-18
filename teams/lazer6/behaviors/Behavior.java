package lazer6.behaviors;


import battlecode.common.RobotController;
import lazer6.Actions;
import lazer6.BattleProfiler;
import lazer6.Broadcaster;
import lazer6.Navigation;
import lazer6.RobotPlayer;
import lazer6.Utilities;


public abstract class Behavior {
	protected final RobotPlayer player;
	protected final RobotController myRC;
	protected final Actions myAct;
	protected final BattleProfiler myProfiler;
	protected final Broadcaster myRadio;
	protected final Navigation myNavi;
	protected final Utilities myUtils;

	public Behavior(RobotPlayer player) {
		this.player = player;
		this.myRC = player.myRC;
		this.myAct = player.myAct;
		this.myProfiler = player.myProfiler;
		this.myRadio = player.myRadio;
		this.myNavi = player.myNavi;
		this.myUtils = player.myUtils;
	}

	/**
	 * Runs the action of the individual behavior (eg: wandering around)
	 * @return True on success.  False on failure
	 */
	public abstract boolean runActions();

	public boolean execute(){
//		player.myRC.setIndicatorString(1, this.getClass().getSimpleName());
		return runActions();
	}

}
