package lazerguns1.filters;

import battlecode.common.Robot;

public abstract interface Matcher
{
	
	public static int CLASS_UNIT = 1;
	public static int CLASS_TOWER = 2;
	public abstract boolean matches(Robot paramRobot);
}
