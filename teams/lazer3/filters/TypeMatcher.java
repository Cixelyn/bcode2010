package lazer3.filters;

import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import lazer3.Intelligence;

public class TypeMatcher
  implements Matcher
{
  private final Intelligence intel;
  private final RobotType type;

  public TypeMatcher(Intelligence intel, RobotType type)
  {
    this.intel = intel;
    this.type = type;
  }

  public boolean matches(Robot r) {
    RobotInfo info = this.intel.getInfo(r);
    return ((info != null) && (info.type.equals(this.type)));
  }
}