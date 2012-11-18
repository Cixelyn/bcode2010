package lazer5.communications;

public enum MsgType {
	
	//Format:  TTL, Priority, Rebroadcast Type, Database Update
	
	
	
	//---TTL---
	//Number of rounds until the message is no longer propagated thorugh the network
	
	
	//---Priority---
	//Does nothing at the moment
	
	
	//---Rebroadcast Type----
	//0 - None - Single transmission
	//1 - only archons retransmit
	//2 - archons and towers retransmit
	//3 - Everyone retransmits
	
	
	//---Database Update---
	//true means that the message will skip the inbox and go straight to the robot's internal database
	
								//ttl,pri,rbc, udb//
	MSG_HELLO	 				(	1,	1,	1,	false),
	MSG_BUILDTOWERHERE			(	0,	1,	2,	false),
	MSG_ENEMYHERE				(	1,	1,	3,	false), 
	MSG_KILLNOW					(   1,  1,  3,	false),
	MSG_ROBOTLIST				(	1,	1,	0,	true),
	MSG_DEFENDTOWER     		(   2,  1,  3,	false), 
	MSG_BASECAMP        		(  -1,  1,  3,	false), 
	MSG_ENEMYTOWER      		(   0,  1,  3,	false),
	MSG_FUCKOFF					( 	1,  1,  0,	false),
	MSG_TARGETHIT				(	1,	0,	1,	true), 
	MSG_DIRECTIONNEEDED			(	0,	1,	0,	false),
	MSG_DIRECTIONREPLY			(	0,	1, 	0,	false),
	MSG_BEGINJIHAD      		(   2,  1,  1,  false),
	MSG_ARCHONCOMMAND			(	1,	1,	1,	false),
	MSG_ENEMYPOINTS				(	0,	1,	0,	false), 
	MSG_PATROL                  (  -1,  1,  0,  false),
	MSG_LEADDIR					(	0,	1,	0,	false);

	
	public int ttl;
	public int priority;
	public int rebroadcastPriority;
	public boolean updateDB;
	
	
	MsgType(int ttl, int priority, int rebroadcastPriority, boolean updateDB) {
		this.ttl = ttl;
		this.priority = priority;
		this.rebroadcastPriority = rebroadcastPriority;
		this.updateDB = updateDB;
	}
}
