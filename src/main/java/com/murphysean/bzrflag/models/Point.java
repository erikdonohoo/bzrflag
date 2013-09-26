package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Point{
	protected volatile float x;
	protected volatile float y;

	public Point(){
		x = 0.0f;
		y = 0.0f;
	}

	public Point(float x, float y){
		this.x = x;
		this.y = y;
	}

	public synchronized Float getX(){
		return x;
	}

	public synchronized void setX(float x){
		this.x = x;
	}

	public synchronized Float getY(){
		return y;
	}

	public synchronized void setY(float y){
		this.y = y;
	}

	public synchronized Point setXY(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public synchronized boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Point point = (Point) o;

		if(Float.compare(point.x, x) != 0) return false;
		if(Float.compare(point.y, y) != 0) return false;

		return true;
	}

	@Override
	public synchronized int hashCode(){
		int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
		result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
		return result;
	}
}
