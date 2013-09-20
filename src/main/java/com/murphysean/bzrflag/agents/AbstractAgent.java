package com.murphysean.bzrflag.agents;


import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.Tank;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
		@JsonSubTypes.Type(value=PFAgent.class, name="dumb"),
		@JsonSubTypes.Type(value=PFAgent.class, name="pf")
})
public abstract class AbstractAgent extends Tank implements Agent{
	protected String type;
	protected Float desiredSpeed;
	protected Float desiredAngularVelocity;
	protected Boolean desiredTriggerStatus;

	public AbstractAgent(){
		type = "abstract";
		desiredSpeed = 0.0f;
		desiredAngularVelocity = 0.0f;
		desiredTriggerStatus = false;
	}

	public String getType(){
		return type;
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
}
