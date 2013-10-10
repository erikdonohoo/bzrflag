package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team{
	protected String id;
	protected String color;
	protected Integer playerCount;

	protected Base base;
	protected Flag flag;

	protected List<Tank> tanks;
	protected Map<String, Integer> score;

	public Team(){
		score = new HashMap<>();
		base = new Base();
		flag = new Flag();
		tanks = new ArrayList<>();
	}

	public Team(int playerCount, String color){
		this();
		this.color = color;
		this.playerCount = playerCount;
		base.setTeamColor(color);
		flag.setTeamColor(color);

		for(int i = 0; i < playerCount; i++){
			Tank tank = new Tank();
			tank.setId(i);
			tank.setCallsign(color + i);
			tank.setTeamColor(color);
			tanks.add(tank);
		}
	}

	public Team(String serverString){
		this();
		String[] parts = serverString.split("\\s+");
		id = parts[1];
		color = parts[1];
		playerCount = Integer.valueOf(parts[2]);

		base.setTeamColor(color);
		flag.setTeamColor(color);

		for(int i = 0; i < playerCount; i++){
			Tank tank = new Tank();
			tank.setId(i);
			tank.setCallsign(color + i);
			tank.setTeamColor(color);
			tanks.add(tank);
		}
	}

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getColor(){
		return color;
	}

	public void setColor(String color){
		this.color = color;
	}

	public Integer getPlayerCount(){
		return playerCount;
	}

	public void setPlayerCount(Integer playerCount){
		this.playerCount = playerCount;
	}

	public Base getBase(){
		return base;
	}

	public void setBase(Base base){
		this.base = base;
	}

	public Flag getFlag(){
		return flag;
	}

	public void setFlag(Flag flag){
		this.flag = flag;
	}

	public List<Tank> getTanks(){
		return tanks;
	}

	public void setTanks(List<Tank> tanks){
		this.tanks = tanks;
	}

	public Map<String, Integer> getScore(){
		return score;
	}

	public void setScore(Map<String, Integer> score){
		this.score = score;
	}

	public void setScore(String team, Integer score){
		this.score.put(team,score);
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Team team = (Team)o;

		if(!id.equals(team.id)) return false;

		return true;
	}

	@Override
	public int hashCode(){
		return id.hashCode();
	}
}
