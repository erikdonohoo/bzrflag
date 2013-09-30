package com.murphysean.bzrflag.events;

import com.murphysean.bzrflag.models.Point;

public class ShotEvent{
	protected Point point;
	protected Point velocity;

	public ShotEvent(Point position, Point velocity){
		this.point = position;
		this.velocity = velocity;
	}

	public ShotEvent(float x, float y, float vx, float vy){
		this.point = new Point(x,y);
		this.velocity = new Point(vx, vy);
	}
}