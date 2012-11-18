package lazerguns2.behaviors;

import java.util.Random;

import lazerguns2.RobotPlayer;




import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class RandomMoveBehavior extends Behavior {
	Random rand;

	public RandomMoveBehavior(RobotPlayer player) {
		super(player);
		rand = new Random(0);
	}

	@Override
	public boolean runActions() throws GameActionException {
		if(player.myRC.getRoundsUntilMovementIdle()!=0) return false;
		int turn = rand.nextInt(40);
		if (turn < 10) {
			int choice = rand.nextInt(4);
			if (choice == 0) {
				player.myRC.setDirection(Direction.NORTH);
				return true;
			} else if (choice == 1) {
				player.myRC.setDirection(Direction.EAST);
				return true;
			} else if (choice == 2) {
				player.myRC.setDirection(Direction.SOUTH);
				return true;
			} else if (choice == 3) {
				player.myRC.setDirection(Direction.WEST);
				return true;
			}
		}
		return false;
	}

}
