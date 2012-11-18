package lazerguns2;

public enum MsgType {
	
	MSG_INVALID 		(0,1),
	MSG_GOTO		 	(3,1),
	MSG_ENEMYLIST 		(0,1),
	MSG_RETREAT 		(3,1),
	MSG_HELP 			(3,1),
	MSG_FORMUP 			(3,1),
	MSG_ENEMYARCHON		(0,1),
	MSG_CHAINATTACK     (0,1),
	MSG_VOTELEADER		(-1,1),
	MSG_DEFENDTOWER		(2,1),
	MSG_BASECAMP        (-1,1),
	MSG_BUILDTOWERHERE	(3,1), 
	MSG_MOBLOCATION     (-1,1), 
	MSG_BASETOWER       (-1,1), 
	MSG_KILLNOW			(0,1), 
	MSG_ENEMYTOWER		(0,1);
	
	public int ttl;
	public int priority;
	
	
	MsgType(int ttl, int priority) {
		this.ttl = ttl;
		this.priority = priority;
	}
	
	
}
