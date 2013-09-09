package com.murphysean.bzrflag.models;

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
}
