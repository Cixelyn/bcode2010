package lazer4;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class DirectionVector {
	public int x;
	public int y;
	
	public DirectionVector(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	public DirectionVector(MapLocation l) {
		x = l.getX();
		y = l.getY();
	}
	
	public DirectionVector(Direction d) {
		if (d==Direction.EAST) {x=1; y=0;}
		else if(d==Direction.WEST) {x=-1; y=0;}
		else if(d==Direction.NORTH) {x=0; y=1;}
		else if(d==Direction.SOUTH) {x=0; y=-1;}
		else if(d==Direction.NORTH_EAST) {x=1; y=1;}
		else if(d==Direction.NORTH_WEST) {x=-1; y=1;}
		else if(d==Direction.SOUTH_EAST) {x=1; y=-1;}
		else if(d==Direction.SOUTH_WEST) {x=-1; y=-1;}
		else {x=0; y=0;}

	}
	
	
	public DirectionVector add(DirectionVector o) {
		return new DirectionVector(x+o.x,y+o.y);
	}
	
	public DirectionVector div(int i) {
		return new DirectionVector(x/i, y/i);
	}
	public Direction toDirection() {
		if (((x==1)||(x==2)) && y==0) {return Direction.EAST;}
		else if(((x==-1)||(x==-2)) && y==0) {return Direction.WEST;}
		else if(x==0 && ((y==1)||(y==2))) {return Direction.NORTH;}
		else if(x==0 && ((y==-1)||(y==-2))) {return Direction.SOUTH;}
		else if(((x==1)||(x==2)) && ((y==1)||(y==2))) {return Direction.NORTH_EAST;}
		else if(((x==-1)||(x==-2)) && ((y==1)||(y==2))) {return Direction.NORTH_WEST;}
		else if(((x==1)||(x==2)) && ((y==-1)||(y==-2))) {return Direction.SOUTH_EAST;}
		else if(((x==-1)||(x==-2)) && ((y==-1)||(y==-2))) {return Direction.SOUTH_WEST;}
		else return Direction.NONE;
	}
	public String toString() {
		return "["+x+","+y+"]";
	}
}