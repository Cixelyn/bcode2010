package lazer5.instincts;

import lazer5.RobotPlayer;


public abstract class Instinct {
	protected final RobotPlayer player;
	
	public Instinct(RobotPlayer player) {
		this.player = player;
	}
	
	public abstract void execute();


}
