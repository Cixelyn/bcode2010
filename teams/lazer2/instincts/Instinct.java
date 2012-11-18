package lazer2.instincts;

import lazer2.BasePlayer;

public abstract class Instinct {
	protected final BasePlayer player;
	
	public Instinct(BasePlayer player) {
		this.player = player;
	}
	
	public abstract void execute();
	
	public abstract String toString();
	
	
	

}
