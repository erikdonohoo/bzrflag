package com.murphysean.bzrflag.events;

import com.murphysean.bzrflag.models.Point;

public class FlagEvent extends BZRFlagEvent{
	protected String color;
	protected String possessingColor;
	protected Point position;

	public FlagEvent(String color, String possessingColor, Point position){
		super(BZRFlagEvent.FLAG_TYPE);
		this.color = color;
		this.possessingColor = possessingColor;
		this.position = position;
	}

	public FlagEvent(String color, String possessingColor, float x, float y){
		super(BZRFlagEvent.FLAG_TYPE);
		this.color = color;
		this.possessingColor = possessingColor;
		this.position = new Point(x,y);
	}
}
