package lazer2.goals;

import java.util.Iterator;

import lazer2.BasePlayer;
import lazer2.Broadcaster;
import lazer2.Intelligence;
import lazer2.MsgType;
import lazer2.filters.Filter;
import lazer2.filters.Matcher;
import lazer2.filters.TeamMatcher;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class AttackGoal extends Goal {
	public AttackGoal(BasePlayer player) {
		super(player);
		initFilters();
	}

	public boolean takeControl() {
		for (Message m: player.myRadio.inbox) {
			if (m.ints[0] == MsgType.MSG_ENEMYARCHON.ordinal())
				return true;
		}
		return false;
	}
	public int execute() throws GameActionException {
		MapLocation attackLoc = new MapLocation(0, 0);
		
		Iterator<Message> it = player.myRadio.inbox.iterator();
		while(it.hasNext()) {
			Message m = it.next();
			if (m.ints[0] == MsgType.MSG_ENEMYARCHON.ordinal()) {
				System.out.println(Clock.getRoundNum() - m.ints[3]);
				if(Clock.getRoundNum() - m.ints[3] < 20) {
					attackLoc = m.locations[2];
				}
				it.remove();
			}
			
		}
		
		if (player.myRC.canAttackSquare(attackLoc)) {
			if (player.myRC.getRoundsUntilAttackIdle() == 0) {
				player.myRC.attackAir(attackLoc);
				player.myRC.yield();
			}
		} else {
			if (player.myRC.getRoundsUntilMovementIdle() == 0) {
				Direction enemyDirection = player.myRC.getLocation().directionTo(attackLoc);
				player.myRC.setDirection(enemyDirection);
				player.myRC.yield();
				if (player.myRC.canMove(enemyDirection)) {
					player.myRC.moveForward();
					player.myRC.yield();
				}
			}
		}
		return GOAL_SUCCESS;
	}
	
	public void initFilters(){
		
	}
}
