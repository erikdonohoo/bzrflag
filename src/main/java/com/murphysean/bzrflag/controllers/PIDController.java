package com.murphysean.bzrflag.controllers;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class PIDController{
	/** Proportional gain, a tuning parameter**/
	protected Float kp;
	/** Integral gain, a tuning parameter**/
	protected Float ki;
	/** Derivative gain, a tuning parameter**/
	protected Float kd;

	protected Float setPoint;

	protected Float prevError = 0.0f;
	protected Long prevMeasuredAt;
	protected Float integral = 0.0f;

	public PIDController(){
		kp = 1.0f;
		ki = 0.0f;
		kd = 1.0f;
	}

	public PIDController(Float kp, Float ki, Float kd){
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
	}

	public Float calculate(Float measuredValue){
		if(setPoint == null)
			throw new RuntimeException("Set Point must not be null");

		Long measuredAt = new Date().getTime();
		if(prevMeasuredAt == null)
			prevMeasuredAt = measuredAt - 1000;
		long timeDiff = measuredAt - prevMeasuredAt;

		Float error = setPoint - measuredValue;
		integral = integral + error * timeDiff;
		Float derivative = (error - prevError) / timeDiff;

		Float output = kp * error + ki * integral + kd * derivative;

		prevError = error;
		prevMeasuredAt = measuredAt;

		return output;
	}

	public Float getKp(){
		return kp;
	}

	public void setKp(Float kp){
		this.kp = kp;
	}

	public Float getKi(){
		return ki;
	}

	public void setKi(Float ki){
		this.ki = ki;
	}

	public Float getKd(){
		return kd;
	}

	public void setKd(Float kd){
		this.kd = kd;
	}

	public Float getSetPoint(){
		return setPoint;
	}

	public void setSetPoint(Float setPoint){
		this.setPoint = setPoint;
	}

	public Float getPrevError(){
		return prevError;
	}

	public void setPrevError(Float prevError){
		this.prevError = prevError;
	}

	public Long getPrevMeasuredAt(){
		return prevMeasuredAt;
	}

	public void setPrevMeasuredAt(Long prevMeasuredAt){
		this.prevMeasuredAt = prevMeasuredAt;
	}

	public Float getIntegral(){
		return integral;
	}

	public void setIntegral(Float integral){
		this.integral = integral;
	}
}
