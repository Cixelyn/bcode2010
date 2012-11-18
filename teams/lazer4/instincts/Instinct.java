package lazer4.instincts;

import lazer4.RobotPlayer;


public abstract class Instinct {
	protected final RobotPlayer player;
	
	public Instinct(RobotPlayer player) {
		this.player = player;
	}
	
	public abstract void execute();


}
