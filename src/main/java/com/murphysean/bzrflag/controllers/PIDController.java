package com.murphysean.bzrflag.controllers;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class PIDController{
	/**
	 * Proportional gain, a tuning parameter*
	 */
	protected volatile float kp;
	/**
	 * Integral gain, a tuning parameter*
	 */
	protected volatile float ki;
	/**
	 * Derivative gain, a tuning parameter*
	 */
	protected volatile float kd;

	protected volatile float setPoint;

	protected volatile float prevError = 0.0f;
	protected volatile long prevMeasuredAt;
	protected volatile float integral = 0.0f;

	public PIDController(){
		kp = 1.0f;
		ki = 0.0f;
		kd = 1.0f;

		prevMeasuredAt = 0;
	}

	public PIDController(float kp, float ki, float kd){
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;

		prevMeasuredAt = 0;
	}

	public synchronized float calculate(float measuredValue){
		Long measuredAt = new Date().getTime();
		if(prevMeasuredAt == 0l)
			prevMeasuredAt = measuredAt - 1000;
		long timeDiff = measuredAt - prevMeasuredAt;

		float error = setPoint - measuredValue;
		integral = integral + error * timeDiff;
		float derivative = (error - prevError) / timeDiff;

		float output = kp * error + ki * integral + kd * derivative;

		prevError = error;
		prevMeasuredAt = measuredAt;

		return output;
	}

	public synchronized float getKp(){
		return kp;
	}

	public synchronized void setKp(float kp){
		this.kp = kp;
	}

	public synchronized float getKi(){
		return ki;
	}

	public synchronized void setKi(float ki){
		this.ki = ki;
	}

	public synchronized float getKd(){
		return kd;
	}

	public synchronized void setKd(float kd){
		this.kd = kd;
	}

	public synchronized float getSetPoint(){
		return setPoint;
	}

	public synchronized void setSetPoint(float setPoint){
		this.setPoint = setPoint;
	}

	public synchronized float getPrevError(){
		return prevError;
	}

	public synchronized void setPrevError(Float prevError){
		this.prevError = prevError;
	}

	public synchronized long getPrevMeasuredAt(){
		return prevMeasuredAt;
	}

	public synchronized void setPrevMeasuredAt(long prevMeasuredAt){
		this.prevMeasuredAt = prevMeasuredAt;
	}

	public synchronized float getIntegral(){
		return integral;
	}

	public synchronized void setIntegral(float integral){
		this.integral = integral;
	}
}
