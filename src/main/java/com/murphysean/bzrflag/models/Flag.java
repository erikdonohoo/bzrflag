package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flag{
	protected String teamColor;
	protected String possessingTeamColor;
	protected Point point;

	public Flag(){
		point = new Point();
	}

	public Flag(String serverString){
		String[] parts = serverString.split("\\s+");
		teamColor = parts[1];
		possessingTeamColor = parts[2];
		Point point = new Point();
		point.setX(Float.parseFloat(parts[3]));
		point.setY(Float.parseFloat(parts[4]));
		this.point = point;
	}

	public String getTeamColor(){
		return teamColor;
	}

	public void setTeamColor(String teamColor){
		this.teamColor = teamColor;
	}

	public String getPossessingTeamColor(){
		return possessingTeamColor;
	}

	public void setPossessingTeamColor(String possessingTeamColor){
		this.possessingTeamColor = possessingTeamColor;
	}

	public Point getPoint(){
		return point;
	}

	public void setPoint(Point point){
		this.point = point;
	}
}
