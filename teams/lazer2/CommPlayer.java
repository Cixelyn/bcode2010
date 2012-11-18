package lazer2;

import lazer2.goals.YieldGoal;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class CommPlayer extends BasePlayer {

	public CommPlayer(RobotController rc) {
		super(rc);
	}

	@Override
	protected void initGoals() {
		// TODO Auto-generated method stub
		myGoals.addGoal(new YieldGoal(this));

	}

	@Override
	protected void initInstincts() {
		// TODO Auto-generated method stub

	}
	
	
	
	
	
	//Overriding Code To Do Broadcast Tests
	/*
	public void run() {
		
		Random rndGen = new Random(this.myRC.getRobot().getID());
		
		
		int i=0;
		
		while(true) {
			
			if(rndGen.nextInt(20)==1) {
				this.myRadio.sendSingleNumber(MsgType.MSG_HELP,i);
				i+=1;
			}
			try {
				this.myRadio.sendAndReceive();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			
			this.myRC.yield();
		}
	} */

}
