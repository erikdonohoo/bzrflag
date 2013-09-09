package com.murphysean.bzrflag.models;

public class Tank{
	protected Integer id;
	protected String callsign;
	protected String teamColor;
	protected String status;
	protected Integer shotsAvailable;
	protected Float timeToReload;
	protected String flag;
	protected Point position;
	protected Point velocity;
	protected Float angle;
	protected Float angleVelocity;

	public Tank(){
		position = new Point();
		velocity = new Point();
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getCallsign(){
		return callsign;
	}

	public void setCallsign(String callsign){
		this.callsign = callsign;
	}

	public String getTeamColor(){
		return teamColor;
	}

	public void setTeamColor(String teamColor){
		this.teamColor = teamColor;
	}

	public String getStatus(){
		return status;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public Integer getShotsAvailable(){
		return shotsAvailable;
	}

	public void setShotsAvailable(Integer shotsAvailable){
		this.shotsAvailable = shotsAvailable;
	}

	public Float getTimeToReload(){
		return timeToReload;
	}

	public void setTimeToReload(Float timeToReload){
		this.timeToReload = timeToReload;
	}

	public String getFlag(){
		return flag;
	}

	public void setFlag(String flag){
		this.flag = flag;
	}

	public Point getPosition(){
		return position;
	}

	public void setPosition(Point position){
		this.position = position;
	}

	public void setPosition(Float x, Float y){
		this.position.setX(x);
		this.position.setY(y);
	}

	public Point getVelocity(){
		return velocity;
	}

	public void setVelocity(Point velocity){
		this.velocity = velocity;
	}

	public void setVelocity(Float x, Float y){
		this.velocity.setX(x);
		this.velocity.setY(y);
	}

	public Float getAngle(){
		return angle;
	}

	public void setAngle(Float angle){
		this.angle = angle;
	}

	public Float getAngleVelocity(){
		return angleVelocity;
	}

	public void setAngleVelocity(Float angleVelocity){
		this.angleVelocity = angleVelocity;
	}
}
