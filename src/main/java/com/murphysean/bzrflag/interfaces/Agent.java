package com.murphysean.bzrflag.interfaces;

public interface Agent{
	public String getType();

	public float getDesiredSpeed();

	public void setDesiredSpeed(float desiredSpeed);

	public float getDesiredAngularVelocity();

	public void setDesiredAngularVelocity(float desiredAngularVelocity);

	public boolean getDesiredTriggerStatus();

	public void setDesiredTriggerStatus(boolean desiredTriggerStatus);
}
