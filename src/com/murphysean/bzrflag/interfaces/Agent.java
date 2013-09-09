package com.murphysean.bzrflag.interfaces;

public interface Agent{
	public Float getDesiredSpeed();
	public void setDesiredSpeed(Float desiredSpeed);
	public Float getDesiredAngularVelocity();
	public void setDesiredAngularVelocity(Float desiredAngularVelocity);
	public Boolean getDesiredTriggerStatus();
	public void setDesiredTriggerStatus(Boolean desiredTriggerStatus);
	public void setPositionAngle(Float positionX, Float positionY, Float angle);
}
