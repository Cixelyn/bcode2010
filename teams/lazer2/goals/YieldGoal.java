package lazer2.goals;
import lazer2.BasePlayer;
public class YieldGoal extends Goal {
	public YieldGoal(BasePlayer player){
		super(player);
	}
	public int execute() {
		player.myRC.yield();
		return GOAL_SUCCESS;
	}
	public boolean takeControl() {
		if(player.myRC.hasActionSet()) {
			return true;
		}
		return false;
	}
	public void initFilters(){
		
	}
}
