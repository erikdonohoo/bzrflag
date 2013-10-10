package com.murphysean.bzrflag.events;

import com.murphysean.bzrflag.models.Point;

public class ShotEvent extends BZRFlagEvent{
	protected Point point;
	protected Point velocity;

	public ShotEvent(Point position, Point velocity){
		super(BZRFlagEvent.SHOT_TYPE);
		this.point = position;
		this.velocity = velocity;
	}

	public ShotEvent(float x, float y, float vx, float vy){
		super(BZRFlagEvent.SHOT_TYPE);
		this.point = new Point(x,y);
		this.velocity = new Point(vx,vy);
	}
}