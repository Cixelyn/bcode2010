package lazer5;

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
		switch(d) {
		case EAST:
			x=1; y=0;
			break;
		case WEST:
			x=-1; y=0;
			break;
		case NORTH:
			x=0; y=-1;
			break;
		case SOUTH:
			x=0; y=1;
			break;
		case NORTH_EAST:
			x=0.71; y=-0.71;
			break;
		case NORTH_WEST:
			x=-0.71; y=-0.71;
			break;
		case SOUTH_EAST:
			x=0.71; y=0.71;
			break;
		case SOUTH_WEST:
			x=-0.71; y=0.71;
			break;
		default:
			x=0; y=0;
			break;
		}
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

	public V2d norm() {		
		double mag = Math.sqrt((x*x) + (y*y));
		return new V2d(x/mag,y/mag);
	}
	
	public double mag() {
		return Math.sqrt((this.x*this.x) + (this.y*this.y));
	}

}
