package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Tank{
	protected volatile int id;
	protected volatile String callsign;
	protected volatile String teamColor;
	protected volatile String status;
	protected volatile int shotsAvailable;
	protected volatile float timeToReload;
	protected volatile String flag;
	protected Point position;
	protected Point velocity;
	protected volatile float angle;
	protected volatile float angleVelocity;

	public Tank(){
		position = new Point();
		velocity = new Point();
	}

	public synchronized void update(String status, String flag, float positionX, float positionY, float angle){
		this.status = status;
		this.flag = flag;
		this.position.setX(positionX);
		this.position.setY(positionY);
		this.angle = angle;
	}

	public synchronized void update(String status, int shotsAvailable, float timeToReload, String flag, float positionX, float positionY, float velocityX, float velocityY, float angle, float angleVelocity){
		this.status = status;
		this.shotsAvailable = shotsAvailable;
		this.timeToReload = timeToReload;
		this.flag = flag;
		this.position.setX(positionX);
		this.position.setY(positionY);
		this.velocity.setX(velocityX);
		this.velocity.setY(velocityY);
		this.angle = angle;
		this.angleVelocity = angleVelocity;
	}

	public synchronized int getId(){
		return id;
	}

	public synchronized void setId(int id){
		this.id = id;
	}

	public synchronized String getCallsign(){
		return callsign;
	}

	public synchronized void setCallsign(String callsign){
		this.callsign = callsign;
	}

	public synchronized String getTeamColor(){
		return teamColor;
	}

	public synchronized void setTeamColor(String teamColor){
		this.teamColor = teamColor;
	}

	public synchronized String getStatus(){
		return status;
	}

	public synchronized void setStatus(String status){
		this.status = status;
	}

	public synchronized int getShotsAvailable(){
		return shotsAvailable;
	}

	public synchronized void setShotsAvailable(int shotsAvailable){
		this.shotsAvailable = shotsAvailable;
	}

	public synchronized float getTimeToReload(){
		return timeToReload;
	}

	public synchronized void setTimeToReload(float timeToReload){
		this.timeToReload = timeToReload;
	}

	public synchronized String getFlag(){
		return flag;
	}

	public synchronized void setFlag(String flag){
		this.flag = flag;
	}

	public synchronized Point getPosition(){
		return position;
	}

	public synchronized void setPosition(Point position){
		this.position = position;
	}

	public synchronized void setPosition(float x, float y){
		this.position.setX(x);
		this.position.setY(y);
	}

	public synchronized Point getVelocity(){
		return velocity;
	}

	public synchronized void setVelocity(Point velocity){
		this.velocity = velocity;
	}

	public synchronized void setVelocity(float x, float y){
		this.velocity.setX(x);
		this.velocity.setY(y);
	}

	public synchronized float getAngle(){
		return angle;
	}

	public synchronized void setAngle(float angle){
		this.angle = angle;
	}

	public synchronized float getAngleVelocity(){
		return angleVelocity;
	}

	public synchronized void setAngleVelocity(float angleVelocity){
		this.angleVelocity = angleVelocity;
	}
}
