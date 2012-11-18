package lazer3;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class V2d {
	public double x;
	public double y;
	
	public V2d(double _x, double _y) {
		x = _x;
		y = _y;
	}
	
	public V2d(MapLocation l) {
		x = l.getX();
		y = l.getY();
	}
	
	public V2d(Direction d) {
		if (d==Direction.EAST) {x=1; y=0;}
		else if(d==Direction.WEST) {x=-1; y=0;}
		else if(d==Direction.NORTH) {x=0; y=-1;}
		else if(d==Direction.SOUTH) {x=0; y=1;}
		else if(d==Direction.NORTH_EAST) {x=0.71; y=-0.71;}
		else if(d==Direction.NORTH_WEST) {x=-0.71; y=-0.71;}
		else if(d==Direction.SOUTH_EAST) {x=0.71; y=0.71;}
		else if(d==Direction.SOUTH_WEST) {x=-0.71; y=0.71;}
		else {x=0; y=0;}

	}
	
	
	public V2d add(V2d o) {
		return new V2d(x+o.x,y+o.y);
	}
	
	public V2d sub(V2d o) {
		return new V2d(x-o.x,y-o.y);
	}
	
	public V2d scale(double i) {
		return new V2d(x*i,y*i);
	}
	
	public MapLocation toLoc() {
		return new MapLocation((int)x,(int)y);
	}
	
	public String toString() {
		return "["+x+","+y+"]";
		
	}
	
	
	
	public static void main(String args[]) {
		V2d a = new V2d(3,4);
		V2d b = new V2d(1,1);
		
		System.out.println(a.add(b));
	}
	
	
	

}
