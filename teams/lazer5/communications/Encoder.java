package lazer5.communications;

import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;


/**
 * These static methods allow us to encode and decode information to and from ints
 * in order to save bytecodes and be more efficient with our broadcasts
 * @author Cory
 *
 */
public final class Encoder {
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////TYPE CONSTANTS/////////////////////////////////////
	
	//RobotType.ordinal=
	//ARCHON, WOUT, CHAINER, SOLDIER, TURRET, COMM, TELEPORTER, AURA
	//  0   ,  1  ,    2   ,    3   ,   4   ,   5 ,     6     ,  7
	//										------------------
	//												A,W,C,S,T,M,L,U
	public static int[] encodePriority = new int[] {0,5,1,4,2,6,7,3};
	//										------------------
	//										 		0,1,2,3,4,5,6,7
	public static int[] decodePriority = new int[] {0,2,4,7,3,1,5,6};
	
	
	

	//////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////ROBOT DATA////////////////////////////////////////
	
	
	//ROBOT DATA Binary Format - 32bit INT:
	//Team(1) + Type(3) + Energon(7) + Status(2) + Attack(5)+ Move(6) + ID(7)
	public static int ROBOT_ID_OFFSET = 0;
	public static int ROBOT_ID_MASK = 0x3FF;
	
	public static int ROBOT_MOVE_OFFSET = 10+ROBOT_ID_OFFSET;
	public static int ROBOT_MOVE_MASK = 0x7C00;
	
	public static int ROBOT_ATTACK_OFFSET = 5+ROBOT_MOVE_OFFSET;
	public static int ROBOT_ATTACK_MASK = 0x78000;
	
	public static int ROBOT_STATUS_OFFSET = 4+ROBOT_ATTACK_OFFSET;
	public static int ROBOT_STATUS_MASK = 0x180000;
	
	public static int ROBOT_ENERGON_OFFSET = 2+ROBOT_STATUS_OFFSET;
	public static int ROBOT_ENERGON_MASK = 0xFE00000;
	
	public static int ROBOT_TYPE_OFFSET = 7+ROBOT_ENERGON_OFFSET;
	public static int ROBOT_TYPE_MASK = 0x70000000;
		
	public static int ROBOT_TEAM_OFFSET = 3+ROBOT_TYPE_OFFSET;
	public static int ROBOT_TEAM_MASK = 0x80000000;
	
	
	//Encoding Function - Lower the number, the higher the priority
	//Team(1) + Type(3) + Energon(7) + Status(2) + Attack(5)+ Move(6) + ID(7)
	public static int encodeRobotInt(RobotInfo info) {
		
		return ((info.team==Team.A ? 0:1) <<ROBOT_TEAM_OFFSET)			//FIXUP
			| (encodePriority[info.type.ordinal()]<<ROBOT_TYPE_OFFSET)
			| ((int)info.energonLevel<<ROBOT_ENERGON_OFFSET)
			| (0<<ROBOT_STATUS_OFFSET)									//IMPLEMENT LATER AS WELL
			| (info.roundsUntilAttackIdle << ROBOT_ATTACK_OFFSET)
			| (info.roundsUntilMovementIdle << ROBOT_MOVE_OFFSET)
			| (info.id << ROBOT_ID_OFFSET);

	}
	
	
	//Decoding Functions
	public static RobotType decodeRobotType(int data) {
		return RobotType.values()[decodePriority[(data & ROBOT_TYPE_MASK)>>ROBOT_TYPE_OFFSET]];
	}
	
	public static int decodeRobotID(int data) {
		return (data & ROBOT_ID_MASK) >> ROBOT_ID_OFFSET;
	}
	
	public static int decodeRobotAttackDelay(int data) {
		return (data & ROBOT_ATTACK_MASK) >> ROBOT_ATTACK_OFFSET;
	}
	
	public static int decodeRobotMoveDelay(int data) {
		return (data & ROBOT_MOVE_MASK) >> ROBOT_MOVE_OFFSET;
	}
	
	public static int decodeRobotEnergon(int data) {
		return (data & ROBOT_ENERGON_MASK) >> ROBOT_ENERGON_OFFSET;
	}
	
	public static Team decodeRobotTeam(int data) {
		return ((data & ROBOT_TEAM_MASK) >> ROBOT_TEAM_OFFSET)==0 ? Team.A : Team.B;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////MSGDATA////////////////////////////////////////////
	
	
	//MSG DATA Binary Format - 32bit INT:
	//Type(6) + Timestamp(16) +ID(10)
	public static int MSG_ID_OFFSET = 0;
	public static int MSG_ID_MASK = 0x3FF;
	
	public static int MSG_TIMESTAMP_OFFSET = 10+MSG_ID_OFFSET;
	public static int MSG_TIMESTAMP_MASK = 0x3FFFC00;
	
	public static int MSG_TYPE_OFFSET = 16+MSG_TIMESTAMP_OFFSET;
	public static int MSG_TYPE_MASK = 0xFC000000;

	

	//Encoding Function -- Lower means older and more important
	public static int encodeMsgInt(MsgType type, int time, int id) {
		return (type.ordinal()<<MSG_TYPE_OFFSET)
			| (time<<MSG_TIMESTAMP_OFFSET)
			| (id<<MSG_ID_OFFSET);		
	}
	
	//Decoding Functions
	public static MsgType decodeMsgType(int data) {
		return MsgType.values()[(data & MSG_TYPE_MASK) >> MSG_TYPE_OFFSET];
	}
	
	public static int decodeMsgTimeStamp(int data) {
		return (data &  MSG_TIMESTAMP_MASK) >> MSG_TIMESTAMP_OFFSET;
	}
	
	public static int decodeMsgID(int data) {
		return (data & MSG_ID_MASK) >> MSG_ID_OFFSET;
	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////HASHING////////////////////////////////////////////
	
	//This function calculates the hash function given a key
	public static int hashMsg(Message m, int key) {
		int hash=0;
		
		for(int i=1; i<m.ints.length; i++) {
			hash ^= m.ints[i];
		}
		
		for(int i=0; i<m.locations.length; i++) {
			if(m.locations[i]!=null) {
				hash ^= m.locations[i].getX();
			}
		}

		return hash^key;
	}
	
	
}
	
	
	
	
	
	
	
	