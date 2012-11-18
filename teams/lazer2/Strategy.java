package lazer2;

public abstract class Strategy {
	BasePlayer player;
	
	
	public Strategy(BasePlayer player) {
		this.player = player;
	}
	
	
	
	
	
	public void execute() {
		/*
		Reflexive behaviors go here:
			eg: if i get an enemy broadcast, attack enemy.
		
		State machine executes here:
			if state == moving:
				trace wall
			if state == fleeing
				move towards archon
			if state == defend
				move towards group
			if state == building army
				new behavior(spawn units).run()
				
			State transitions go here
			if under heavy fire
				state = run
				
			Strategy transitions here
			if i see lots of towers
				player.changeStrategy(new Offensive Strategy)
			
		
		Run common robot instincts here
		
		Yield
		
		*/
	}
	
	

	

}
