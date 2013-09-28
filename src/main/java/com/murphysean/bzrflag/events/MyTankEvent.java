package com.murphysean.bzrflag.events;

public class MyTankEvent extends BZRFlagEvent{

	protected int tankIndex;
	protected String callsign;
	protected String status;
	protected int shotsAvailable;
	protected float timeToReload;
	protected String flag;
	protected float x;
	protected float y;
	protected float angle;
	protected float vx;
	protected float vy;
	protected float vangle;

	public MyTankEvent(int tankIndex, String callsign, String status, int shotsAvailable, float timeToReload, String flag, float x, float y, float angle, float vx, float vy, float vangle){
		super(BZRFlagEvent.MY_TANK_TYPE);

		this.tankIndex = tankIndex;
		this.callsign = callsign;
		this.status = status;
		this.shotsAvailable = shotsAvailable;
		this.timeToReload = timeToReload;
		this.flag = flag;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.vx = vx;
		this.vy = vy;
		this.vangle = vangle;
	}
}
