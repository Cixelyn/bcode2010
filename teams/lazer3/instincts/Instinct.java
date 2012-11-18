package lazer3.instincts;

import lazer3.RobotPlayer;


public abstract class Instinct {
	protected final RobotPlayer player;
	
	public Instinct(RobotPlayer player) {
		this.player = player;
	}
	
	public abstract void execute();


}
