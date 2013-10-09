package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Game{
	//connecting, initializing, playing, quitting, finished
	protected volatile String id;
	protected volatile String state;

	//Protocol Version
	protected volatile String version;

	//Constants
	protected volatile String teamColor;
	protected volatile int worldSize;
	protected volatile float tankAngVel;
	protected volatile float tankLength;
	protected volatile float tankRadius;
	protected volatile float tankSpeed;
	protected volatile String tankAlive;
	protected volatile String tankDead;
	protected volatile float linearAccel;
	protected volatile float angularAccel;
	protected volatile float tankWidth;
	protected volatile float shotRadius;
	protected volatile float shotRange;
	protected volatile float shotSpeed;
	protected volatile float flagRadius;
	protected volatile float explodeTime;
	protected volatile float truePositive;
	protected volatile float trueNegative;

	//Game Time
	protected volatile float timeElapsed;
	protected volatile float timeLimit;
	protected volatile float hertz;

	//Internal Objects
	protected Team team;
	protected List<Team> teams;
	protected List<Obstacle> obstacles;

	//Communication Objects
	protected volatile String host;
	protected volatile int port;


	public Game(){
		state = "instantiated";

		teams = new ArrayList<>();
		obstacles = new ArrayList<>();
	}

	public Game(String id, String host, int port){
		this();
		this.id = id;
		this.host = host;
		this.port = port;
	}

	public Team findTeamByColor(String color){
		for(Team team : teams){
			if(team.getColor().equals(color))
				return team;
		}
		return null;
	}

	public synchronized String getId(){
		return id;
	}

	public synchronized void setId(String id){
		this.id = id;
	}

	public synchronized String getState(){
		return state;
	}

	public synchronized void setState(String state){
		this.state = state;
	}

	public synchronized String getVersion(){
		return version;
	}

	public synchronized void setVersion(String version){
		this.version = version;
	}

	public synchronized String getTeamColor(){
		return teamColor;
	}

	public synchronized void setTeamColor(String teamColor){
		this.teamColor = teamColor;
	}

	public synchronized int getWorldSize(){
		return worldSize;
	}

	public synchronized void setWorldSize(int worldSize){
		this.worldSize = worldSize;
	}

	public synchronized float getTankAngVel(){
		return tankAngVel;
	}

	public synchronized void setTankAngVel(float tankAngVel){
		this.tankAngVel = tankAngVel;
	}

	public synchronized float getTankLength(){
		return tankLength;
	}

	public synchronized void setTankLength(float tankLength){
		this.tankLength = tankLength;
	}

	public synchronized float getTankRadius(){
		return tankRadius;
	}

	public synchronized void setTankRadius(float tankRadius){
		this.tankRadius = tankRadius;
	}

	public synchronized float getTankSpeed(){
		return tankSpeed;
	}

	public synchronized void setTankSpeed(float tankSpeed){
		this.tankSpeed = tankSpeed;
	}

	public synchronized String getTankAlive(){
		return tankAlive;
	}

	public synchronized void setTankAlive(String tankAlive){
		this.tankAlive = tankAlive;
	}

	public synchronized String getTankDead(){
		return tankDead;
	}

	public synchronized void setTankDead(String tankDead){
		this.tankDead = tankDead;
	}

	public synchronized float getLinearAccel(){
		return linearAccel;
	}

	public synchronized void setLinearAccel(float linearAccel){
		this.linearAccel = linearAccel;
	}

	public synchronized float getAngularAccel(){
		return angularAccel;
	}

	public synchronized void setAngularAccel(float angularAccel){
		this.angularAccel = angularAccel;
	}

	public synchronized float getTankWidth(){
		return tankWidth;
	}

	public synchronized void setTankWidth(float tankWidth){
		this.tankWidth = tankWidth;
	}

	public synchronized float getShotRadius(){
		return shotRadius;
	}

	public synchronized void setShotRadius(float shotRadius){
		this.shotRadius = shotRadius;
	}

	public synchronized float getShotRange(){
		return shotRange;
	}

	public synchronized void setShotRange(float shotRange){
		this.shotRange = shotRange;
	}

	public synchronized float getShotSpeed(){
		return shotSpeed;
	}

	public synchronized void setShotSpeed(float shotSpeed){
		this.shotSpeed = shotSpeed;
	}

	public synchronized float getFlagRadius(){
		return flagRadius;
	}

	public synchronized void setFlagRadius(float flagRadius){
		this.flagRadius = flagRadius;
	}

	public synchronized float getExplodeTime(){
		return explodeTime;
	}

	public synchronized void setExplodeTime(float explodeTime){
		this.explodeTime = explodeTime;
	}

	public synchronized float getTruePositive(){
		return truePositive;
	}

	public synchronized void setTruePositive(float truePositive){
		this.truePositive = truePositive;
	}

	public synchronized float getTrueNegative(){
		return trueNegative;
	}

	public synchronized void setTrueNegative(float trueNegative){
		this.trueNegative = trueNegative;
	}

	public synchronized float getTimeElapsed(){
		return timeElapsed;
	}

	public synchronized void setTimeElapsed(float timeElapsed){
		this.timeElapsed = timeElapsed;
	}

	public synchronized float getTimeLimit(){
		return timeLimit;
	}

	public synchronized void setTimeLimit(float timeLimit){
		this.timeLimit = timeLimit;
	}

	public float getHertz(){
		return hertz;
	}

	public void setHertz(float hertz){
		this.hertz = hertz;
	}

	public synchronized Team getTeam(){
		return team;
	}

	public synchronized void setTeam(Team team){
		this.team = team;
	}

	public synchronized List<Team> getTeams(){
		return teams;
	}

	public synchronized void setTeams(List<Team> teams){
		this.teams = teams;
	}

	public synchronized List<Obstacle> getObstacles(){
		return obstacles;
	}

	public synchronized void setObstacles(List<Obstacle> obstacles){
		this.obstacles = obstacles;
	}

	public synchronized String getHost(){
		return host;
	}

	public synchronized void setHost(String host){
		this.host = host;
	}

	public synchronized int getPort(){
		return port;
	}

	public synchronized void setPort(int port){
		this.port = port;
	}
}
