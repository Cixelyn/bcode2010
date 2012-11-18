package broadcast1;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;

public class HelloMemo extends Memo{

	public HelloMemo(RobotController rc) {
		super(rc);
		
		this.type = this.MSG_HELLO;
	}

	public Message encode() {
		
		Message m = new Message();
		
		//Initialization
		m.locations  = new MapLocation[1];
		m.ints = new int[4];
		m.strings = null;
		
		//Set Origin
		m.locations[0] = rc.getLocation();			//robot origin
		
		//Set Consts
		m.ints[0] = MSG_HELLO; 						//msgtype
		m.ints[1] = rc.getRobot().getID(); 			//store robotID
		m.ints[2] = 666+rc.getTeam().hashCode(); 	//run hash
		m.ints[3] = Clock.getRoundNum(); 			//set round number
		
		return m;
	}

	@Override
	public Memo decode(Message m) {
		return null;
		
	}


}
