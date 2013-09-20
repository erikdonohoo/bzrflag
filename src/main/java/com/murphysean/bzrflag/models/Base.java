package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Base{
	protected String teamColor;
	protected List<Point> points;

	public Base(){

	}

	public Base(String serverString){
		points = new ArrayList<>();
		String[] parts = serverString.split("\\s+");

		teamColor = parts[1];

		Point point = null;
		for(int i = 2; i < parts.length; i++){
			if(i % 2 == 0){
				point = new Point();
				point.setX(Float.valueOf(parts[i]));
			}else{
				point.setY(Float.valueOf(parts[i]));
				points.add(point);
			}
		}
	}

	public String getTeamColor(){
		return teamColor;
	}

	public void setTeamColor(String teamColor){
		this.teamColor = teamColor;
	}

	public List<Point> getPoints(){
		return points;
	}

	public void setPoints(List<Point> points){
		this.points = points;
	}
}
