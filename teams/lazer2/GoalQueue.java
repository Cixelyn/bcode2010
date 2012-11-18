package lazer2;

import java.util.ArrayList;

import battlecode.common.GameActionException;

import lazer2.goals.Goal;

public class GoalQueue {
	public ArrayList<Goal> goals;
	public Goal current = null;  //DEBUGGING ROUTINEEEE
	
	
	public GoalQueue() {
		goals = new ArrayList<Goal>();
	}

	public void addGoal(Goal g) {
		this.goals.add(g);
	}
	
	public void executeGoals() throws GameActionException {
		
//		System.out.println("reset");
		for(Goal currentGoal: goals) {
//			System.out.println(currentGoal.toString());
			if(currentGoal.takeControl()) {
				currentGoal.execute();
				current = currentGoal;
				return;
			}

		}	
	}
	

}
