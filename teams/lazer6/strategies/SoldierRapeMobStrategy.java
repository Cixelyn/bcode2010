package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotPlayer;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class SoldierRapeMobStrategy extends Strategy{
	
	
	//Targeting system
	private Robot target=null;
	public boolean rapingAir = false;

	
	
	//Instincts and Behaviors
	private Instinct transfer;
	
	
	
	private MapLocation battleFront;
	private int battleFrontTimestamp;
	
	
	
	private boolean initialBattleCry = false;
	

	public SoldierRapeMobStrategy(RobotPlayer player) {
		super(player);
		myProfiler.setScanMode(true, true, true, true);
		transfer = new TransferInstinct(player);

	}

	
	public boolean beginStrategy() {
		return true;
	}


	public void runBehaviors() {
		
		
		//////////////////////////Transitions
		if(Clock.getRoundNum()-battleFrontTimestamp > 45) {
			battleFront = null;
		}
		
		
		
		
		
		
		
		if(target==null) { 											//MOB STATE

			//Message Handling
			  Message m;
				MsgType type;
				int i = 0;
				while (myRadio.inbox[i] != null) {
					m = myRadio.inbox[i];
					type = Encoder.decodeMsgType(m.ints[Broadcaster.idxData]);
					if (type == MsgType.MSG_ENGAGINGENEMY) {
						battleFront = m.locations[Broadcaster.firstData];
						battleFrontTimestamp = Clock.getRoundNum();
					}
					i++;
				}
			
			

			myRC.setIndicatorString(2, "mob state");
			target=myProfiler.closestGlobalEnemyRobot;
			
			
			if(target==null) { //no enemy in sight,
				if (myProfiler.closestEnemyDBData==null && battleFront==null) { //first we check the DB
					myAct.moveLikeMJ(myProfiler.archonDifferential,myNavi.bugInDir(myProfiler.mobDirection())); //mob to archon
					initialBattleCry = false;
				} else if(myProfiler.closestEnemyDBData!=null) {				//something in DB
					myAct.moveInDir(myNavi.bugTo(myProfiler.closestEnemyDBData.location));
					
					if(!initialBattleCry) { //if we notice something in DB, send attack shit.
						initialBattleCry = true;
						myRadio.sendSingleDestination(MsgType.MSG_ENGAGINGENEMY, myProfiler.closestEnemyDBData.location);
					}
					
				} else {														//something broadcasted
					myAct.moveInDir(myNavi.bugTo(battleFront));
				}
			}	
		}
		
		else {															//ATTACK STATE (we have a target)
			myRC.setIndicatorString(2, "attack state");
			
			
			try {
				if(myRC.canSenseObject(target)) {						//if the robot can still be sensed
					
					RobotInfo targetInfo = myRC.senseRobotInfo(target);
					
					if(myRC.canAttackSquare(targetInfo.location)) {
						if(myAct.shootTarget(targetInfo.location,myProfiler.closestGlobalEnemyRobotIsAir))
							myRadio.sendSingleDestination(MsgType.MSG_ENGAGINGENEMY, targetInfo.location);
					}else {
						myAct.moveInDir(myNavi.bugTo(targetInfo.location));  //FIXME replace with MJ styled walking
					}
					
				} else{													//we either lost it or killed it.
					target=null;
					myAct.moveLikeMJ(myProfiler.archonDifferential,myNavi.bugInDir(myProfiler.mobDirection())); //mob to archon
				}
				
				
			} catch (GameActionException e) {
				target=null;
				e.printStackTrace();
			}

		}
		
		
	}


	public void runInstincts() {
		transfer.execute();
		
	}

}
