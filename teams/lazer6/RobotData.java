package lazer6;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;


/**
 * This class encapsulates the RobotData datatype, allowing us to use it interchangably with
 * both local sensor data and broadcasted information.  The extra fields <code>ttl</code> and <code>isLocal</code>
 * allow us to calculate the information's 'currentness.'
 * 
 * <br><br>
 * 
 * 
 *　　　 ∧＿∧∩ 　 ／￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣			<br>
 *　　  （　´∀｀)/　＜   	FUCK YEAH DATATYPES!				<br>
 *彡　⊂　　　ﾉ　　　　＼＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿			<br>
 *　彡　(つ　ﾉ												<br>
 *　　彡　(ノ												<br>
 *
 * 
 * @author Cory
 *
 */
public class RobotData {
	
	//Static Constants
	public final static int ID_MOD = 1024;
	
	
	//Robot Information
	public final int id;
	public final int data;
	public double energon;
	public final int timestamp;
	public final MapLocation location;	
	public final int ttl;
	
	//Data Structure
	public RobotData next;
	
	
	
	
	/**
	 * Blank constructor for building head or null nodes
	 */
	public RobotData() {
		id =0;
		energon = 0;
		timestamp = 0;
		location = null;
		data = 0;
		ttl = 0;
	}
	
	
	/**
	 * Constructor used when building RobotData object from local sensor returned RobotInfo
	 * @param rinfo
	 */
	public RobotData(RobotInfo rinfo) {
		id = rinfo.id;
		energon = rinfo.energonLevel;
		timestamp = Clock.getRoundNum();
		location = rinfo.location;
		data = 0;
		ttl = 0;
	}
	
	
	/**
	 * Constructor used when building RobotData object from received broadcasted information
	 * @return
	 */
	public RobotData(int _data, MapLocation _loc, int _time) {
		id = Encoder.decodeRobotID(_data);
		energon = Encoder.decodeRobotEnergon(_data);
		timestamp = _time;
		ttl = Encoder.decodeRobotMoveDelay(_data);
		location = _loc;
		data = _data;
	}
	
	
	
	/**
	 * Returns whether a robot's information is current or not, based on its MoveDelay parameter
	 * <br>
	 * A robot with a non-zero move delay is assumed to still be in the same position after that many rounds.
	 * Note that this won't take into account teleportation.
	 * But probably if they already teleported out, you won't need to worry too much about wasting shots and such
	 * 
	 * @return Boolean - whether information is outdated
	 */
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
