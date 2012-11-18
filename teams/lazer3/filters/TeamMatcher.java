package lazer3.filters;

import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import lazer3.Intelligence;

public class TeamMatcher
  implements Matcher
{
  private final Intelligence intel;
  private final Team team;

  public TeamMatcher(Intelligence intel, Team team)
  {
    this.intel = intel;
    this.team = team;
  }

  public boolean matches(Robot r) {
    RobotInfo info = this.intel.getInfo(r);
    return ((info != null) && (info.team.equals(this.team)));
  }
}