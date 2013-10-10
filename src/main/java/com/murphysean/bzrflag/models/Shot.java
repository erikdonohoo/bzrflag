package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shot{
	protected Point point;
	protected Point velocity;

	public Shot(){
		point = new Point();
		velocity = new Point();
	}

	public Shot(String serverString){
		String[] parts = serverString.split("\\s+");
		Point point = new Point();
		point.setX(Float.parseFloat(parts[1]));
		point.setY(Float.parseFloat(parts[2]));
		this.point = point;
		Point vel = new Point();
		vel.setX(Float.parseFloat(parts[3]));
		vel.setY(Float.parseFloat(parts[4]));
		this.velocity = vel;
	}

	public Point getPoint(){
		return point;
	}

	public void setPoint(Point point){
		this.point = point;
	}

	public Point getVelocity(){
		return velocity;
	}

	public void setVelocity(Point velocity){
		this.velocity = velocity;
	}
}
