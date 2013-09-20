package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Point{
	protected Float x;
	protected Float y;

	public Point(){
		x = 0.0f;
		y = 0.0f;
	}

	public Point(float x, float y){
		this.x = x;
		this.y = y;
	}

	public Float getX(){
		return x;
	}

	public void setX(Float x){
		this.x = x;
	}

	public Float getY(){
		return y;
	}

	public void setY(Float y){
		this.y = y;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Point point = (Point) o;

		if(x != null ? !x.equals(point.x) : point.x != null) return false;
		if(y != null ? !y.equals(point.y) : point.y != null) return false;

		return true;
	}

	@Override
	public int hashCode(){
		int result = x != null ? x.hashCode() : 0;
		result = 31 * result + (y != null ? y.hashCode() : 0);
		return result;
	}
}
