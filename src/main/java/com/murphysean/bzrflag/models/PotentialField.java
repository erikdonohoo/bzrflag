package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class PotentialField{
	protected String type;
	protected Point point;
	protected Float radius;
	protected Float spread;
	protected Float strength;

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	public Point getPoint(){
		return point;
	}

	public void setPoint(Point point){
		this.point = point;
	}

	public Float getRadius(){
		return radius;
	}

	public void setRadius(Float radius){
		this.radius = radius;
	}

	public Float getSpread(){
		return spread;
	}

	public void setSpread(Float spread){
		this.spread = spread;
	}

	public Float getStrength(){
		return strength;
	}

	public void setStrength(Float strength){
		this.strength = strength;
	}
}
