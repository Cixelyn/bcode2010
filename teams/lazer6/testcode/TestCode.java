package lazer6.testcode;

import lazer6.RobotPlayer;
import lazer6.strategies.Strategy;
import battlecode.common.RobotType;

public class TestCode extends Strategy{

	public TestCode(RobotPlayer player) {
		
		super(player);
		myProfiler.setScanMode(true, true, true, true);

	}

	public boolean beginStrategy() {
		
		
		if(myProfiler.myArchonID==0) {
		
		
			/* ORDINAL COMPARISON TESTING -- notes: these two examples are equal
			RobotType rtype = player.myRC.getRobotType();
			
			player.myUtils.startTimer("Enum Comparison");
			boolean a = rtype==RobotType.ARCHON;
			player.myUtils.stopTimer();
			
			player.myUtils.startTimer("Constant Comparison");
			boolean b = rtype.ordinal()==0;
			player.myUtils.stopTimer();
			*/
			
			
	
			
			/* ARRAY TESTING -- notes:  System.arraycopy is always faster
			int[] a = {1,1,2,3,45,6,3,4,24,2};
			int[] c = new int[20];
			int[] d = new int[20];
			
			
			player.myUtils.startTimer("ArrayCopy Method");
			System.arraycopy(a,0,c,0,a.length);
			player.myUtils.stopTimer();
			System.out.println(Arrays.toString(c));
			
			
			player.myUtils.startTimer("ManualCopy Method");
			for(int i=0; i<a.length; i++) {
				d[i]=a[i];
			}
			player.myUtils.stopTimer();
			System.out.println(Arrays.toString(d));
			*/
			
			
			
			
		
		}
		
		
		myRC.suicide();
		
		

		
		
		return true;
		
		

		
		
	}

	@Override
	public void runBehaviors() {
		
	}

	@Override
	public void runInstincts() {
		
	}

}
