package broadcast1;


import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;

public abstract class Memo {
	
	//MESSAGE TYPES
	public static final int MSG_INVALID = 0;
	public static final int MSG_HELLO = 1;
	public static final int MSG_ENEMIES = 2;
	
	
	//MESSAGE PROPERTIES - these are always to be set by the constructor
	public int type;
	public int ttl;
	public MapLocation oriLoc;
	public int oriID;
	public RobotController rc;
	
	
	public Memo(RobotController rc) {
		
		this.rc = rc;
	}
	
	
	public abstract Message encode();
	public abstract Memo decode(Message m);

}
