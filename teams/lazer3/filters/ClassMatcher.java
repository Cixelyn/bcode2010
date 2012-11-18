package lazer3.filters;

import lazer3.Intelligence;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class ClassMatcher implements Matcher {

	private final Intelligence intel;
	private final int type;
	
	
	public ClassMatcher(Intelligence intel, int type) {
		this.intel = intel;
		this.type = type;
	}
	
	
	public boolean matches(Robot r) {
		RobotInfo info = this.intel.getInfo(r);
		
		if(type==CLASS_TOWER) {
			return ((info!=null)) && (info.type==RobotType.AURA || info.type==RobotType.TELEPORTER || info.type==RobotType.COMM);
		} else {
			return ((info!=null)) && (info.type!=RobotType.AURA || info.type!=RobotType.TELEPORTER || info.type!=RobotType.COMM);
		}
	}
	

}
