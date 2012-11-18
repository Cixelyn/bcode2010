package lazer6.strategies;


import lazer6.Actions;
import lazer6.BattleProfiler;
import lazer6.Broadcaster;
import lazer6.Navigation;
import lazer6.RobotPlayer;
import lazer6.Utilities;
import battlecode.common.Clock;
import battlecode.common.RobotController;

public abstract class Strategy {
	protected final RobotPlayer player;
	protected final RobotController myRC;
	protected final Actions myAct;
	protected final BattleProfiler myProfiler;
	protected final Broadcaster myRadio;
	protected final Navigation myNavi;
	protected final Utilities myUtils;
	protected int executeStartTime;
	private boolean started;
	
	
	
	//Debug Stuff
	//private boolean debug_BytecodeOverflow = true;
	
	

	public Strategy(RobotPlayer player) {
		this.player = player;
		this.myRC = player.myRC;
		this.myAct = player.myAct;
		this.myProfiler = player.myProfiler;
		this.myRadio = player.myRadio;
		this.myNavi = player.myNavi;
		this.myUtils = player.myUtils;
		started = false;
	}


	public abstract boolean beginStrategy();
	public abstract void runInstincts();
	public abstract void runBehaviors();



	public void execute() {

		//UPDATE OUR CLOCK
		/*
		executeStartTime = Clock.getRoundNum();
		int executeStartByte = Clock.getBytecodeNum();
		*/
		
		
		//RECEIVE ALL BROADCASTS
		try{
			player.myRadio.receiveAll();
		} catch(Exception e) {
			//System.out.println("Radio Receiving Failure");
		}
		
		
		
		//RUN SENSOR SCAN
		try {
			player.myProfiler.sensorScan();
		} catch (Exception e) {
			//System.out.println("Exception caught by RobotPlayer: sensorScan");
			e.printStackTrace();
		}
		


		//RUN INITIAL STRATEGY IF REQUIRED
		if(!started) {
			try {
				started = beginStrategy();
				//runInstincts();
			} catch (Exception e) {
				//System.out.println("Exception caught by RobotPlayer: beginStrategy");
				e.printStackTrace();
			}
		}

		//RUN STANDARD STRATEGY
		else {
			try {//RUN STANDARD BEHAVIORS
				runBehaviors();
			} catch (Exception e) {
				//System.out.println("Exception caught by RobotPlayer: runBehaviors");
				e.printStackTrace();
			}
			
			try {//RUN STANDARD INSTINCTS
				runInstincts();
			} catch (Exception e) {
				//System.out.println("Exception caught by RobotPlayer: runInstincts");
				e.printStackTrace();
			}
		}

		
		//SEND QUEUED BROADCASTS
		try{
			player.myRadio.sendAll();
		}catch(Exception e) {
			//System.out.println("Radio Sending Failure");	
			e.printStackTrace();
		}
		
		
		/*
		//CHECK OUR CLOCK
		if(debug_BytecodeOverflow){
			if(executeStartTime!=Clock.getRoundNum()) {
				int byteCount = (6000-executeStartByte) + (Clock.getRoundNum()-executeStartTime-1) * 6000 + Clock.getBytecodeNum();
				System.out.println("Warning: Unit over Bytecode Limit: "+ byteCount);
			}
		}
		*/
		

		//YIELD AND END TURN
		player.myRC.yield();

	}
}