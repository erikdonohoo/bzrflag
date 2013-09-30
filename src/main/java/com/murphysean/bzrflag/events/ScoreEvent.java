package com.murphysean.bzrflag.events;

public class ScoreEvent{
	protected String color;
	protected String otherColor;
	protected int score;

	public ScoreEvent(String color, String otherColor, int score){
		this.color = color;
		this.otherColor = otherColor;
		this.score = score;
	}
}