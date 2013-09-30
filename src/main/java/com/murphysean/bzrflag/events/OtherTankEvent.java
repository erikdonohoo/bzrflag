package com.murphysean.bzrflag.events;

public class OtherTankEvent{
	protected String color;
	protected String callsign;
	protected int tankIndex;
	protected String status;
	protected String flag;
	protected float x;
	protected float y;
	protected float angle;

	public OtherTankEvent(String color, String callsign, int tankIndex, String status, String flag, float x, float y, float angle){
		this.tankIndex = tankIndex;
		this.callsign = callsign;
		this.color = color;
		this.status = status;
		this.flag = flag;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}
}
