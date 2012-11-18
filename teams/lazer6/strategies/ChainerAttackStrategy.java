package lazer6.strategies;

import lazer6.Broadcaster;
import lazer6.Encoder;
import lazer6.MsgType;
import lazer6.RobotData;
import lazer6.RobotPlayer;
import lazer6.behaviors.Behavior;
import lazer6.behaviors.WaitForChargingBehavior;
import lazer6.instincts.Instinct;
import lazer6.instincts.TransferInstinct;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class ChainerAttackStrategy extends Strategy {
	private Behavior WaitForCharge;


	//priority system
	private static final int[] typePriorityGround = new int[] {0,2,2,2,2,2,2,2};
	
	
	
	//state variables
	private static final int mobState=0;
	private static final int attackState=1;
	private int state;
	
	
	private MapLocation battleFront;
	private int battleFrontTimestamp;
	
	
	private boolean initialBattleCry = false;
	
	
	
	
	//instincts
	private Instinct transfer;

	public boolean rapingAir = false;

	public ChainerAttackStrategy(RobotPlayer player) {
		super(player);
		state = mobState;
		transfer = new TransferInstinct(player);
		myProfiler.setScanMode(true, false, true, true);
		WaitForCharge = new WaitForChargingBehavior(player);
	}

	
	public boolean beginStrategy() {
//		return WaitForCharge.execute();
		return true;
	}

	
	
	
	/////////////////////////////////////MAIN CODE/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	public void runBehaviors() {
		
		
		
		MapLocation atkLoc = findAtkLoc();
		MapLocation myLoc = myRC.getLocation();
	

		
		
		
		
		
		
		//////////////////////////Transitions
		if(Clock.getRoundNum()-battleFrontTimestamp > 45) {
			battleFront = null;
		}
		
		
		
		if(atkLoc!=null) {
			state=attackState;
		}else {
			state=mobState;		//temporary transition out for the moment.
		}
			
		
		
		////////////////////////State Machine
		switch(state) {
	
		
		case mobState:									//Mobbing to Location
			
			if(myProfiler.closestEnemyDBData==null && battleFront==null) {	//Nothing broadcasted, go home
				//myRC.setIndicatorString(1, "Go Home");
						
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
					
					initialBattleCry = false;


					myAct.moveLikeMJ(myProfiler.archonDifferential,(myNavi.bugInDir(myProfiler.mobDirection())));
			
			}else {										//There's something in our database
				
				if(myProfiler.closestEnemyDBData!=null) { //DB code
					//myRC.setIndicatorString(1, "Rushing to DB");
					
					//Therefore perform a controlled rush.
					MapLocation enemyLoc = myProfiler.closestEnemyDBData.location;
					Direction attackVector = myLoc.directionTo(myProfiler.closestEnemyDBData.location);
					
					
					if(!initialBattleCry) {
						myRadio.sendSingleDestination(MsgType.MSG_ENGAGINGENEMY, enemyLoc);
						initialBattleCry = true;
					}
					
					myAct.moveLikeMJ(attackVector,myNavi.bugTo(enemyLoc));
				}
				else {									//move to front
					myAct.moveInDir(myNavi.bugTo(battleFront));
				}
			}
			break;
			
		case attackState:								//Attacking the shit out of something (in our proximity map)
			
			//myRC.setIndicatorString(1, "Attacking Shit");
			
			battleFront = null;
			
			if(!myRC.canAttackSquare(atkLoc)) {
				Direction attackVector = myLoc.directionTo(atkLoc);
				myAct.moveInDir(attackVector);  //FIXME replace with MJ styled walking
				
				
			}
				
			//Add some sort of transition out here.
			
			break;
		}
		
		

		//////////////////////Attack Code
		if(atkLoc!=null) {
			if(myAct.shootTarget(atkLoc, rapingAir)) {
				player.myDB.hitLocationSplash(atkLoc.getX(), atkLoc.getY());
				//We are engaging enemy, so broadcast the attack location
				//Other units will use this to determine the front line.
				myRadio.sendSingleDestination(MsgType.MSG_ENGAGINGENEMY, atkLoc);
			}
		}
		
		
	}
	
	
	
	
	////////////////////////////////////AUXILARY FUNCTIONS////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Calculates highest potential target for the chainer to hit
	 * @return
	 */
	private MapLocation findAtkLoc() {
		int[][] priority = new int[9][9];

		int maxPotential = 0;
		int i;
		int j;
		int myX = myRC.getLocation().getX();
		int myY = myRC.getLocation().getY();
		int atkX=0;
		int atkY=0;
		int transX=0;
		int transY=0;

		MapLocation targetLoc = null;
		RobotInfo nearbyInfo = null;

		
		
		/////////////////////////////////////////////////////////LOCAL DATA POTENTIAL MAP SEEDING	
		Robot[] nearbyRobots = myProfiler.nearbyGroundRobots;
		for (i = nearbyRobots.length; --i >= 0;) {
			try {
				nearbyInfo = myRC.senseRobotInfo(nearbyRobots[i]);
				targetLoc = nearbyInfo.location;
				transX = targetLoc.getX() - myX + 4;
				transY = targetLoc.getY() - myY + 4;
				//priority[transX][transY] = typePriorityGround[nearbyInfo.type.ordinal()] * (nearbyInfo.team==player.myTeam ? -2 : 1);
				priority[transX][transY] = 2 * (nearbyInfo.team==player.myTeam ? -2 : 1);
			} catch(Exception e) {
				//System.out.println("Chainer Priority Exception");
				e.printStackTrace();
			}
		}

		
		////////////////////////////////////////////////////////DATABASE POTENTIAL MAP SEEDING

		RobotData nextRobot;
		RobotType Type;
		player.myDB.resetPtr();
		while (player.myDB.hasNext()) {
			nextRobot = player.myDB.next();
			Type = Encoder.decodeRobotType(nextRobot.data);
			if (Type == RobotType.ARCHON) { rapingAir = true; }
			targetLoc = nextRobot.location;
			transX = targetLoc.getX() - myX + 4;
			transY = targetLoc.getY() - myY + 4;
			if ((transX < 9 && transX >= 0) && (transY < 9 && transY >= 0)) {
				if (rapingAir) {
					priority[transX][transY] = 1;
				} else {
					priority[transX][transY] = typePriorityGround[Type.ordinal()] * (Encoder.decodeRobotTeam(nextRobot.data)==player.myTeam ? -2 : 1);
				}
			}
		}

		
		// seed negative values around you to prevent self-inflicted dmg
		if (!rapingAir) {
			priority[3][3]--;
			priority[4][3]--;
			priority[5][3]--;
			priority[3][4]--;
			priority[4][4]--;
			priority[5][4]--;
			priority[3][5]--;
			priority[4][5]--;
			priority[5][5]--;
		} else {
			MapLocation[] alliedArchons = myProfiler.alliedArchons;
			for (i = alliedArchons.length; --i >= 0;) {
				targetLoc = alliedArchons[i];
				transX = targetLoc.getX() - myX + 4;
				transY = targetLoc.getY() - myY + 4;
				if ((transX < 9 && transX >= 0) && (transY < 9 && transY >= 0)) {
					priority[transX][transY] = -2;
				}
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////// CONVOLUTION
		///////////////////////////////////////////////////////////////////////////////////////

		//int[][] potential = convolvePriority(priority);
		int[][] addthree  = new int[9][9];
		int[][] potential = new int[7][7];
		
		addthree[4][0]  = priority[3][0] + priority[4][0] + priority[5][0];
		addthree[2][1]  = priority[1][1] + priority[2][1] + priority[3][1];
		addthree[3][1]  = priority[2][1] + priority[3][1] + priority[4][1];
		addthree[4][1]  = priority[3][1] + priority[4][1] + priority[5][1];
		addthree[5][1]  = priority[4][1] + priority[5][1] + priority[6][1];
		addthree[6][1]  = priority[5][1] + priority[6][1] + priority[7][1];
		addthree[2][2]  = priority[1][2] + priority[2][2] + priority[3][2];
		addthree[3][2]  = priority[2][2] + priority[3][2] + priority[4][2];
		addthree[4][2]  = priority[3][2] + priority[4][2] + priority[5][2];
		addthree[5][2]  = priority[4][2] + priority[5][2] + priority[6][2];
		addthree[6][2]  = priority[5][2] + priority[6][2] + priority[7][2];
		addthree[1][3]  = priority[0][3] + priority[1][3] + priority[2][3];
		addthree[2][3]  = priority[1][3] + priority[2][3] + priority[3][3];
		addthree[3][3]  = priority[2][3] + priority[3][3] + priority[4][3];
		addthree[4][3]  = priority[3][3] + priority[4][3] + priority[5][3];
		addthree[5][3]  = priority[4][3] + priority[5][3] + priority[6][3];
		addthree[6][3]  = priority[5][3] + priority[6][3] + priority[7][3];
		addthree[7][3]  = priority[6][3] + priority[7][3] + priority[8][3];
		addthree[2][4]  = priority[1][4] + priority[2][4] + priority[3][4];
		addthree[3][4]  = priority[2][4] + priority[3][4] + priority[4][4];
		addthree[4][4]  = priority[3][4] + priority[4][4] + priority[5][4];
		addthree[5][4]  = priority[4][4] + priority[5][4] + priority[6][4];
		addthree[6][4]  = priority[5][4] + priority[6][4] + priority[7][4];
		addthree[1][5]  = priority[0][5] + priority[1][5] + priority[2][5];
		addthree[2][5]  = priority[1][5] + priority[2][5] + priority[3][5];
		addthree[3][5]  = priority[2][5] + priority[3][5] + priority[4][5];
		addthree[4][5]  = priority[3][5] + priority[4][5] + priority[5][5];
		addthree[5][5]  = priority[4][5] + priority[5][5] + priority[6][5];
		addthree[6][5]  = priority[5][5] + priority[6][5] + priority[7][5];
		addthree[7][5]  = priority[6][5] + priority[7][5] + priority[8][5];
		addthree[2][6]  = priority[1][6] + priority[2][6] + priority[3][6];
		addthree[3][6]  = priority[2][6] + priority[3][6] + priority[4][6];
		addthree[4][6]  = priority[3][6] + priority[4][6] + priority[5][6];
		addthree[5][6]  = priority[4][6] + priority[5][6] + priority[6][6];
		addthree[6][6]  = priority[5][6] + priority[6][6] + priority[7][6];
		addthree[2][7]  = priority[1][7] + priority[2][7] + priority[3][7];
		addthree[3][7]  = priority[2][7] + priority[3][7] + priority[4][7];
		addthree[4][7]  = priority[3][7] + priority[4][7] + priority[5][7];
		addthree[5][7]  = priority[4][7] + priority[5][7] + priority[6][7];
		addthree[6][7]  = priority[5][7] + priority[6][7] + priority[7][7];
		addthree[4][8]  = priority[3][8] + priority[4][8] + priority[5][8];
		potential[3][0] = addthree[4][0] + addthree[4][1] + addthree[4][2];
		potential[1][1] = addthree[2][1] + addthree[2][2] + addthree[2][3];
		potential[2][1] = addthree[3][1] + addthree[3][2] + addthree[3][3];
		potential[3][1] = addthree[4][1] + addthree[4][2] + addthree[4][3];
		potential[4][1] = addthree[5][1] + addthree[5][2] + addthree[5][3];
		potential[5][1] = addthree[6][1] + addthree[6][2] + addthree[6][3];
		potential[1][2] = addthree[2][2] + addthree[2][3] + addthree[2][4];
		potential[2][2] = addthree[3][2] + addthree[3][3] + addthree[3][4];
		potential[3][2] = addthree[4][2] + addthree[4][3] + addthree[4][4];
		potential[4][2] = addthree[5][2] + addthree[5][3] + addthree[5][4];
		potential[5][2] = addthree[6][2] + addthree[6][3] + addthree[6][4];
		potential[0][3] = addthree[1][3] + addthree[1][4] + addthree[1][5];
		potential[1][3] = addthree[2][3] + addthree[2][4] + addthree[2][5];
		potential[2][3] = addthree[3][3] + addthree[3][4] + addthree[3][5];
		potential[3][3] = addthree[4][3] + addthree[4][4] + addthree[4][5];
		potential[4][3] = addthree[5][3] + addthree[5][4] + addthree[5][5];
		potential[5][3] = addthree[6][3] + addthree[6][4] + addthree[6][5];
		potential[6][3] = addthree[7][3] + addthree[7][4] + addthree[7][5];
		potential[1][4] = addthree[2][4] + addthree[2][5] + addthree[2][6];
		potential[2][4] = addthree[3][4] + addthree[3][5] + addthree[3][6];
		potential[3][4] = addthree[4][4] + addthree[4][5] + addthree[4][6];
		potential[4][4] = addthree[5][4] + addthree[5][5] + addthree[5][6];
		potential[5][4] = addthree[6][4] + addthree[6][5] + addthree[6][6];
		potential[1][5] = addthree[2][5] + addthree[2][6] + addthree[2][7];
		potential[2][5] = addthree[3][5] + addthree[3][6] + addthree[3][7];
		potential[3][5] = addthree[4][5] + addthree[4][6] + addthree[4][7];
		potential[4][5] = addthree[5][5] + addthree[5][6] + addthree[5][7];
		potential[5][5] = addthree[6][5] + addthree[6][6] + addthree[6][7];
		potential[3][6] = addthree[4][6] + addthree[4][7] + addthree[4][8];
		
		
		///////////////////////////////////////////////////////////////////////////////////////// END CONVOLUTION

		for (i = 7; --i >= 0;) {
			for (j = 7; --j >= 0;) {
				if (potential[i][j] > maxPotential) { 
					maxPotential = potential[i][j];
					atkX = i;
					atkY = j;
				}
			}
		}

		if (maxPotential > 0) {
			return new MapLocation(myX+atkX-3,myY+atkY-3);
		}
		return null;
	}

/*	private int[][] convolvePriority(int[][] priority) {
		int[][] addthree  = new int[9][9];
		int[][] potential = new int[7][7];
		
		addthree[4][0]  = priority[3][0] + priority[4][0] + priority[5][0];
		addthree[2][1]  = priority[1][1] + priority[2][1] + priority[3][1];
		addthree[3][1]  = priority[2][1] + priority[3][1] + priority[4][1];
		addthree[4][1]  = priority[3][1] + priority[4][1] + priority[5][1];
		addthree[5][1]  = priority[4][1] + priority[5][1] + priority[6][1];
		addthree[6][1]  = priority[5][1] + priority[6][1] + priority[7][1];
		addthree[2][2]  = priority[1][2] + priority[2][2] + priority[3][2];
		addthree[3][2]  = priority[2][2] + priority[3][2] + priority[4][2];
		addthree[4][2]  = priority[3][2] + priority[4][2] + priority[5][2];
		addthree[5][2]  = priority[4][2] + priority[5][2] + priority[6][2];
		addthree[6][2]  = priority[5][2] + priority[6][2] + priority[7][2];
		addthree[1][3]  = priority[0][3] + priority[1][3] + priority[2][3];
		addthree[2][3]  = priority[1][3] + priority[2][3] + priority[3][3];
		addthree[3][3]  = priority[2][3] + priority[3][3] + priority[4][3];
		addthree[4][3]  = priority[3][3] + priority[4][3] + priority[5][3];
		addthree[5][3]  = priority[4][3] + priority[5][3] + priority[6][3];
		addthree[6][3]  = priority[5][3] + priority[6][3] + priority[7][3];
		addthree[7][3]  = priority[6][3] + priority[7][3] + priority[8][3];
		addthree[2][4]  = priority[1][4] + priority[2][4] + priority[3][4];
		addthree[3][4]  = priority[2][4] + priority[3][4] + priority[4][4];
		addthree[4][4]  = priority[3][4] + priority[4][4] + priority[5][4];
		addthree[5][4]  = priority[4][4] + priority[5][4] + priority[6][4];
		addthree[6][4]  = priority[5][4] + priority[6][4] + priority[7][4];
		addthree[1][5]  = priority[0][5] + priority[1][5] + priority[2][5];
		addthree[2][5]  = priority[1][5] + priority[2][5] + priority[3][5];
		addthree[3][5]  = priority[2][5] + priority[3][5] + priority[4][5];
		addthree[4][5]  = priority[3][5] + priority[4][5] + priority[5][5];
		addthree[5][5]  = priority[4][5] + priority[5][5] + priority[6][5];
		addthree[6][5]  = priority[5][5] + priority[6][5] + priority[7][5];
		addthree[7][5]  = priority[6][5] + priority[7][5] + priority[8][5];
		addthree[2][6]  = priority[1][6] + priority[2][6] + priority[3][6];
		addthree[3][6]  = priority[2][6] + priority[3][6] + priority[4][6];
		addthree[4][6]  = priority[3][6] + priority[4][6] + priority[5][6];
		addthree[5][6]  = priority[4][6] + priority[5][6] + priority[6][6];
		addthree[6][6]  = priority[5][6] + priority[6][6] + priority[7][6];
		addthree[2][7]  = priority[1][7] + priority[2][7] + priority[3][7];
		addthree[3][7]  = priority[2][7] + priority[3][7] + priority[4][7];
		addthree[4][7]  = priority[3][7] + priority[4][7] + priority[5][7];
		addthree[5][7]  = priority[4][7] + priority[5][7] + priority[6][7];
		addthree[6][7]  = priority[5][7] + priority[6][7] + priority[7][7];
		addthree[4][8]  = priority[3][8] + priority[4][8] + priority[5][8];
		potential[3][0] = addthree[4][0] + addthree[4][1] + addthree[4][2];
		potential[1][1] = addthree[2][1] + addthree[2][2] + addthree[2][3];
		potential[2][1] = addthree[3][1] + addthree[3][2] + addthree[3][3];
		potential[3][1] = addthree[4][1] + addthree[4][2] + addthree[4][3];
		potential[4][1] = addthree[5][1] + addthree[5][2] + addthree[5][3];
		potential[5][1] = addthree[6][1] + addthree[6][2] + addthree[6][3];
		potential[1][2] = addthree[2][2] + addthree[2][3] + addthree[2][4];
		potential[2][2] = addthree[3][2] + addthree[3][3] + addthree[3][4];
		potential[3][2] = addthree[4][2] + addthree[4][3] + addthree[4][4];
		potential[4][2] = addthree[5][2] + addthree[5][3] + addthree[5][4];
		potential[5][2] = addthree[6][2] + addthree[6][3] + addthree[6][4];
		potential[0][3] = addthree[1][3] + addthree[1][4] + addthree[1][5];
		potential[1][3] = addthree[2][3] + addthree[2][4] + addthree[2][5];
		potential[2][3] = addthree[3][3] + addthree[3][4] + addthree[3][5];
		potential[3][3] = addthree[4][3] + addthree[4][4] + addthree[4][5];
		potential[4][3] = addthree[5][3] + addthree[5][4] + addthree[5][5];
		potential[5][3] = addthree[6][3] + addthree[6][4] + addthree[6][5];
		potential[6][3] = addthree[7][3] + addthree[7][4] + addthree[7][5];
		potential[1][4] = addthree[2][4] + addthree[2][5] + addthree[2][6];
		potential[2][4] = addthree[3][4] + addthree[3][5] + addthree[3][6];
		potential[3][4] = addthree[4][4] + addthree[4][5] + addthree[4][6];
		potential[4][4] = addthree[5][4] + addthree[5][5] + addthree[5][6];
		potential[5][4] = addthree[6][4] + addthree[6][5] + addthree[6][6];
		potential[1][5] = addthree[2][5] + addthree[2][6] + addthree[2][7];
		potential[2][5] = addthree[3][5] + addthree[3][6] + addthree[3][7];
		potential[3][5] = addthree[4][5] + addthree[4][6] + addthree[4][7];
		potential[4][5] = addthree[5][5] + addthree[5][6] + addthree[5][7];
		potential[5][5] = addthree[6][5] + addthree[6][6] + addthree[6][7];
		potential[3][6] = addthree[4][6] + addthree[4][7] + addthree[4][8];

		return potential;
	}
*/
	
	public void runInstincts() {
		transfer.execute();
	}
}
