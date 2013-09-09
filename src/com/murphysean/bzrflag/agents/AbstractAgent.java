package com.murphysean.bzrflag.agents;


import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.Tank;

public abstract class AbstractAgent extends Tank implements Agent{
	protected Float desiredSpeed;
	protected Float desiredAngularVelocity;
	protected Boolean desiredTriggerStatus;

	public AbstractAgent(){
		desiredSpeed = 0.0f;
		desiredAngularVelocity = 0.0f;
		desiredTriggerStatus = false;
	}

	public Float getDesiredSpeed(){
		return desiredSpeed;
	}

	public void setDesiredSpeed(Float desiredSpeed){
		this.desiredSpeed = desiredSpeed;
	}

	public Float getDesiredAngularVelocity(){
		return desiredAngularVelocity;
	}

	public void setDesiredAngularVelocity(Float desiredAngularVelocity){
		this.desiredAngularVelocity = desiredAngularVelocity;
	}

	public Boolean getDesiredTriggerStatus(){
		boolean ret = desiredTriggerStatus;
		if(ret)
			desiredTriggerStatus = false;
		return ret;
	}

	public void setDesiredTriggerStatus(Boolean desiredTriggerStatus){
		this.desiredTriggerStatus = desiredTriggerStatus;
	}

	public void setPositionAngle(Float positionX, Float positionY, Float angle){
		position.setX(positionX);
		position.setY(positionY);
		this.angle = angle;
	}
}
