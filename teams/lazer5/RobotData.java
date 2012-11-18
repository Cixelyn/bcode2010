package lazer5;
import lazer5.communications.Encoder;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;


/**
 * Linked List Node for the SensorDB system.
 * 
 *　　　　　　
 *　　　∧＿∧∩ 　 ／￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣
 *　　  （　´∀｀)/　＜   	FUCK YEAH DATATYPES!
 *彡　⊂　　　ﾉ　　　　＼＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿
 *　彡　(つ　ﾉ
 *　　彡　(ノ
 *
 * 
 * @author Cory
 *
 */




public class RobotData {
	
	private static final int ID_MOD=1024;
	
	public final int id;
	public final int timestamp;
	public final int ttl;
	public final int data;
	public final MapLocation location;
	public double energon;
	public final boolean isLocal;
	
	public RobotData next;
	
	
	
	

	
	//Only use this constructor to create null RobotData objects
	//eg: when constructing the head of the linked list
	public RobotData() {
		id = 0;
		timestamp = 0;
		ttl = 0;
		data = 0;
		location = null;
		energon = 0;
		isLocal = false;
	}
	
	
	//If we already have the robot info
	public RobotData(RobotInfo _rinfo) {
		id = _rinfo.id%ID_MOD;
		data = Encoder.encodeRobotInt(_rinfo);
		ttl = _rinfo.roundsUntilMovementIdle;
		timestamp = Clock.getRoundNum();
		isLocal = true;
		energon = _rinfo.energonLevel;  //this might need to be modified to +1 because of reserve
		location = _rinfo.location;
		next = null;
	}
	

	//Constructor used to sense robot data
	public RobotData(Robot _robot, RobotController _rc) throws GameActionException{	
		this(_rc.senseRobotInfo(_robot));
	}
	
	
	//Constructor used to create list from broadcast data
	public RobotData(int _data, MapLocation _loc, int _timestamp) {
		id = Encoder.decodeRobotID(_data); //should already be modded 1024
		data = _data;
		ttl = Encoder.decodeRobotMoveDelay(_data);
		timestamp = _timestamp;
		isLocal = false;
		energon = Encoder.decodeRobotEnergon(_data);
		location = _loc;
		next = null;
	}

	
	
	public boolean isCurrent() {	
			//Data is current if elapsed time is less than the movement delay of the robot
		if (Clock.getRoundNum()-timestamp <= ttl) {	
			if(energon<=0) {
				return false;
			}
			return true;	
		}	
		return false;
	}

	
	public String toString() {
		return "["+id+":"+(ttl-(Clock.getRoundNum()-timestamp))+"."+energon+"]";
	}
	
	
	
}