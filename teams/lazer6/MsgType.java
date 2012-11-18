package lazer6;


/**
 * This enum contains all the possible message types we can broadcast along with the timeouts and the mode of propagation
 * @author Cory
 *
 */
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
	//4 - Non Archons
	
	
	//---Database Update---
	//true means that the message will skip the inbox and go straight to the robot's internal database
	
	//Ints / Locs
	//# of ints and locations in the message body
	
									//ttl,pri,rbc, 	udb,	ints,	locs
	MSG_HELLO	 					(	1,	1,	1,	false,	0,		0		),
	MSG_ENEMYLOCPOINTS				(	0,	1,	0,	false,	1,		1		),
	MSG_ENEMYHERE					(	1,	1,	1,	false,	0,		0		),
	MSG_NEEDDIRECTION				(	0,	1,	0,	false,	0,		0		),
	MSG_DIRECTIONREPLY				(	0,	1,	0,	false,	1,		0		),
	MSG_ITSRAPINGTIME				(	1,	1,	2,	false,	0,		1		),
	MSG_TARGETHIT					(   0,  1,  0,  true ,	0,		0		),
	MSG_TARGETSPLASH				(   0,  1,  0,  true ,	0,		0		),  
	MSG_DATABASEDUMP				(	1,	1,	1,	true ,	0,		0		),
	MSG_HULLCENTER					(	1,	1,	0,	false,	0,		0		),
	MSG_BUILDTOWERHERE				(	1,	1,	0,	false,	0,		1		),
	MSG_SWARMLOCATION				(	1,	1,	1,	false,	0,		1		),
	MSG_BUILDERWOUTLOC				(	0,	1,	0,	false,	0,		1		),
	MSG_BATTERYWOUT					(	0,	1,	0,	false,	0,		0		),
	MSG_BUILDMODE					(	1,	1,	1,	false,	0,		1		),
	MSG_ENGAGINGENEMY				(	1,	1,	4,	false,	0,		1		);
	
	public int ttl;
	public int priority;
	public int rebroadcastPriority;
	public int numInts;
	public int numLocs;
	public boolean updateDB;
	
	
	MsgType(int ttl, int priority, int rebroadcastPriority, boolean updateDB, int numInts, int numLocs) {
		this.ttl = ttl;
		this.priority = priority;
		this.rebroadcastPriority = rebroadcastPriority;
		this.updateDB = updateDB;
		this.numInts=numInts;
		this.numLocs=numLocs;
	}
}
