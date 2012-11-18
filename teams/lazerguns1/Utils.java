package lazerguns1;
import battlecode.common.*;
import java.util.Random;

/**
 * Random utilities and misc functions to make life easier for everyone
 * @author lazer pew pew
 *
 */
public class Utils {
	public RobotController myRC;
	public Random randGen;
	
	private int timerStartRound;
	private int timerStartByte;
	private String timerName;
	
	public Utils(RobotController myRC) {
		this.myRC = myRC;
		randGen = new Random(myRC.getRobot().hashCode());
	}
	
	public Direction randDir() {
		return Direction.values()[randGen.nextInt(8)];
	}
	
	public void startTimer(String name) {
		this.timerName = timerName;
		this.timerStartRound = Clock.getRoundNum();
		this.timerStartByte = Clock.getBytecodeNum();
	}
	
	public int stopTimer() {
		int byteCount;
		if(Clock.getRoundNum()==timerStartRound) { //if we're still in the same round
			byteCount = Clock.getBytecodeNum() - timerStartByte;
		} else {//multiple rounds have passed
			byteCount = (6000-timerStartByte) + (Clock.getRoundNum()-timerStartRound-1) * 6000 + Clock.getBytecodeNum();
		}
		
		System.out.println(timerName + ": "+Integer.toString(byteCount));
		return byteCount;
	}
	
	
}
