package com.murphysean.bzrflag.events;

import com.murphysean.bzrflag.models.Point;

public class OccGridEvent extends BZRFlagEvent{
	protected Point position;
	protected String line;

	public OccGridEvent(int x, int y, String line){
		super(BZRFlagEvent.OCC_GRID_TYPE);
		this.position = new Point(x,y);
		this.line = line;
	}

	public OccGridEvent(Point position, String line){
		super(BZRFlagEvent.OCC_GRID_TYPE);
		this.position = position;
		this.line = line;
	}

	public Point getPosition(){
		return position;
	}

	public String getLine(){
		return line;
	}
}
