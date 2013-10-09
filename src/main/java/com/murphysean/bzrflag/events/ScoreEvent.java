package com.murphysean.bzrflag.events;

public class ScoreEvent extends BZRFlagEvent{
	protected String color;
	protected String otherColor;
	protected int score;

	public ScoreEvent(String color, String otherColor, int score){
		super(BZRFlagEvent.SCORE_TYPE);
		this.color = color;
		this.otherColor = otherColor;
		this.score = score;
	}
}