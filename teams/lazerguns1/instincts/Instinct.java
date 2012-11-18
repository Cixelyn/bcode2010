package lazerguns1.instincts;

import lazerguns1.RobotPlayer;


public abstract class Instinct {
	protected final RobotPlayer player;
	
	public Instinct(RobotPlayer player) {
		this.player = player;
	}
	
	public abstract void execute();


}
