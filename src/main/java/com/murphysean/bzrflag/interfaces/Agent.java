package com.murphysean.bzrflag.interfaces;

public interface Agent{
	public String getType();
	public Float getDesiredSpeed();
	public void setDesiredSpeed(Float desiredSpeed);
	public Float getDesiredAngularVelocity();
	public void setDesiredAngularVelocity(Float desiredAngularVelocity);
	public Boolean getDesiredTriggerStatus();
	public void setDesiredTriggerStatus(Boolean desiredTriggerStatus);
}
