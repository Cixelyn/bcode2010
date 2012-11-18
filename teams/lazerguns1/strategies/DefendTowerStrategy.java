package lazerguns1.strategies;

import java.util.Iterator;

import lazerguns1.MsgType;
import lazerguns1.RobotPlayer;
import lazerguns1.behaviors.Behavior;
import lazerguns1.behaviors.GoToTowerBehavior;
import lazerguns1.behaviors.MobTowerBehavior;
import lazerguns1.behaviors.PatrolTowerBehavior;
import lazerguns1.filters.Filter;
import lazerguns1.filters.FilterFactory;
import lazerguns1.instincts.Instinct;
import lazerguns1.instincts.TransferInstinct;


import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class DefendTowerStrategy extends Strategy {
	private Instinct transfer;
	private Behavior goToTower, mobTower, patrolTower;
	Filter enemies;
	private int state = 0;
	private MapLocation enemyLoc;

	public DefendTowerStrategy(RobotPlayer player) {
		super(player);
		initFilters();
		enemyLoc = new MapLocation(0,0);
		transfer = new TransferInstinct(player);
		goToTower = new GoToTowerBehavior(player);
		mobTower = new MobTowerBehavior(player);
		patrolTower = new PatrolTowerBehavior(player);
	}

	@Override
	public void runBehaviors() throws GameActionException {
		if(state == 0) {
			Iterator<Message> towerHelp = player.myRadio.inbox.iterator();
			while (towerHelp.hasNext()) {
				Message m = towerHelp.next();
				if (m.ints[0] == MsgType.MSG_DEFENDTOWER.ordinal()) {
					enemyLoc = m.locations[2];
					state = 1;
				}
			}
		}else if (state == 1) {
			if(goToTower.execute()) {
				state = 2;
			}
		}
		if (state == 2) {
			if (mobTower.execute()) {state = 0;}
		}
	}

	@Override
	public void runInstincts() throws GameActionException {
		transfer.execute();
		player.myRadio.sendAndReceive();
	}
	
	public void initFilters(){
		enemies = FilterFactory.enemiesInRange(player.myRC, player.myIntel, 10);
	}

	@Override
	public boolean beginStrategy() throws GameActionException {
		// TODO Auto-generated method stub
		return true;
	}

}
