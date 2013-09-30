package com.murphysean.bzrflag.events;

import com.murphysean.bzrflag.models.Point;

public class OccGridEvent{
	protected Point position;
	protected String line;

	public OccGridEvent(int x, int y, String line){
		this.position = new Point(x,y);
		this.line = line;
	}

	public OccGridEvent(Point position, String line){
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
