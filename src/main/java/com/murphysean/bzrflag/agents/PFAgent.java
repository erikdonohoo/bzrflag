package com.murphysean.bzrflag.agents;

import com.murphysean.bzrflag.controllers.PIDController;
import com.murphysean.bzrflag.models.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class PFAgent extends AbstractAgent{
	private transient Team myTeam;
	private transient List<Team> otherTeams;
	protected PotentialField attractor;
	protected List<PotentialField> potentialFields;

	protected PIDController angleController;

	//The only state allowed is whether I have a flag or not, which can be garnered from the parent tank object

	public PFAgent(){
		type = "pf";
		angleController = new PIDController();
		//I always desire my tank to be 0 degrees from my target rotation
		angleController.setSetPoint(0.0f);

		otherTeams = new ArrayList<>();
		potentialFields = new ArrayList<>();
	}

	public synchronized void setMyTeam(Team myTeam){
		this.myTeam = myTeam;
	}

	public synchronized void addOtherTeam(Team otherTeam){
		otherTeams.add(otherTeam);
	}

	public synchronized void addObstacle(Obstacle obstacle){
		//Turn this into a rejector
		PotentialField rejector = new PotentialField();
		rejector.setId(UUID.randomUUID().toString());
		rejector.setType("rejector");
		rejector.setPoint(obstacle.getCenterPoint());
		rejector.setRadius(obstacle.getRadius());
		rejector.setSpread(30f);
		rejector.setStrength(1f);
		potentialFields.add(rejector);

		//Also create a tangential
		PotentialField tangential = new PotentialField();
		tangential.setId(UUID.randomUUID().toString());
		tangential.setType("tangential");
		tangential.setPoint(obstacle.getCenterPoint());
		tangential.setRadius(obstacle.getRadius() + obstacle.getRadius() * 0.1f);
		tangential.setSpread(obstacle.getRadius() + obstacle.getRadius() * 0.3f);
		tangential.setStrength(0.9f);
		potentialFields.add(tangential);
	}

	private static Point evaluateAttractor(float agentX, float agentY, PotentialField attractor){
		//Find the distance between the agent and the goal
		float distance = (float) Math.sqrt(Math.pow(attractor.getPoint().getX() - agentX, 2.0d) +
				Math.pow(attractor.getPoint().getY() - agentY, 2.0d));
		float angle = (float) Math.atan2(attractor.getPoint().getY() - agentY, attractor.getPoint().getX() - agentX);

		//Whoa, you're there
		if(distance < attractor.getRadius())
			return new Point(0.0f, 0.0f);
		//You're somewhere out there, just come in at the attractors strength
		if(distance > (attractor.getSpread() + attractor.getRadius()))
			return new Point((float) (attractor.getStrength() * Math.cos(angle)), (float) (attractor.getStrength() * Math.sin(angle)));
		//You're coming in, slowly fall off to 0
		return new Point((float) (attractor.getStrength() * ((distance - attractor.getRadius()) / attractor.getSpread()) * Math.cos(angle)), (float) (attractor.getStrength() * ((distance - attractor.getRadius()) / attractor.getSpread()) * Math.sin(angle)));
	}

	private static Point evaluateRejector(float agentX, float agentY, PotentialField rejector){
		float distance = (float) Math.sqrt(Math.pow(rejector.getPoint().getX() - agentX, 2.0d) +
				Math.pow(rejector.getPoint().getY() - agentY, 2.0d));
		float angle = (float) Math.atan2(rejector.getPoint().getY() - agentY, rejector.getPoint().getX() - agentX);

		//Get the freak outta here
		if(distance < rejector.getRadius())
			return new Point((float) (-1.0f * rejector.getStrength() * Math.cos(angle)), (float) (-1.0f * rejector.getStrength() * Math.sin(angle)));
		//Don't worry about me, you're somewhere out there
		if(distance > (rejector.getSpread() + rejector.getRadius()))
			return new Point(0.0f, 0.0f);
		//You should feel more pressure the closer you get
		return new Point((float) (-1.0f * rejector.getStrength() * ((rejector.getSpread() + rejector.getRadius() - distance) / rejector.getSpread()) * Math.cos(angle)), (float) (-1.0f * rejector.getStrength() * ((rejector.getSpread() + rejector.getRadius() - distance) / rejector.getSpread()) * Math.sin(angle)));
	}

	private static Point evaluateTangential(float agentX, float agentY, PotentialField tang){
		float distance = (float) Math.sqrt(Math.pow(tang.getPoint().getX() - agentX, 2.0d) +
				Math.pow(tang.getPoint().getY() - agentY, 2.0d));

		// Same as rejector, but modify angle by 90 degrees
		float ninety = 1.57079633f; // add = counterclockwise, subtract = clockwise
		float angle = (float) Math.atan2(tang.getPoint().getY() - agentY, tang.getPoint().getX() - agentX);
		angle -= ninety;
//		int degrees = (int)((angle * 180f)/Math.PI);
//		
//		// Get degrees between 0 and 360
//		while (degrees > 360)
//			degrees -= 360;
//		while (degrees < 0)
//			degrees += 360;
//		
//		// Calculate quadrant
//		if (degrees >= 0 && degrees < 45 ||
//				degrees >= 90 && degrees < 135 ||
//				degrees >= 180 && degrees < 225 ||
//				degrees >= 270 && degrees <= 360) {
//			angle += ninety;
//		} else {
//			angle -= ninety;
//		}

		//Get the freak outta here
		if(distance < tang.getRadius())
			return new Point((float) (-1.0f * tang.getStrength() * Math.cos(angle)), (float) (-1.0f * tang.getStrength() * Math.sin(angle)));
		//Don't worry about me, you're somewhere out there
		if(distance > (tang.getSpread() + tang.getRadius()))
			return new Point(0.0f, 0.0f);
		//You should feel more pressure the closer you get
		return new Point((float) (-1.0f * tang.getStrength() * ((tang.getSpread() + tang.getRadius() - distance) / tang.getSpread()) * Math.cos(angle)), (float) (-1.0f * tang.getStrength() * ((tang.getSpread() + tang.getRadius() - distance) / tang.getSpread()) * Math.sin(angle)));
	}

	protected Point evaluate(){
		return evaluate(position.getX(), position.getY(), "all");
	}

	public synchronized Point evaluate(float x, float y, String type){
		Point vector = new Point(0.0f, 0.0f);

		if(flag.equals("-") && attractor != null && attractor.getPoint().equals(myTeam.getFlag().getPoint())){
			attractor = null;
		}

		if(flag.equals("-") && this.attractor == null){
			//TODO Instead of looking at a random team, how about the team with the most points (besides myself)
			int randomTeam = (int) Math.round(Math.random() * (otherTeams.size() - 1));
			PotentialField attractor = new PotentialField();
			attractor.setId(UUID.randomUUID().toString());
			attractor.setType("attractor");
			attractor.setPoint(otherTeams.get(randomTeam).getFlag().getPoint());
			attractor.setRadius(1.0f);
			attractor.setSpread(25f);
			attractor.setStrength(1.0f);
			this.attractor = attractor;
		}

		if(!flag.equals("-")){
			attractor = null;
			PotentialField attractor = new PotentialField();
			//TODO Shouldn't look at flag point as that may move as an enemy captures it
			attractor.setId(UUID.randomUUID().toString());
			attractor.setType("attractor");
			attractor.setPoint(myTeam.getFlag().getPoint());
			attractor.setRadius(1.0f);
			attractor.setSpread(25f);
			attractor.setStrength(1.0f);
			this.attractor = attractor;
		}

		if(type.equals("attractors") || type.equals("all")){
			Point vec = evaluateAttractor(x, y, this.attractor);
			vector.setX(vector.getX() + vec.getX());
			vector.setY(vector.getY() + vec.getY());
		}

		for(PotentialField potentialField : potentialFields){
			if(potentialField.getType().equals("attractor") && (type.contains("attractors") || type.equals("all"))){
				Point vec = evaluateAttractor(x, y, potentialField);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}else if(potentialField.getType().equals("rejector") && (type.contains("rejectors") || type.equals("all"))){
				Point vec = evaluateRejector(x, y, potentialField);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}else if(potentialField.getType().equals("tangential") && (type.contains("tangentials") || type.equals("all"))){
				Point vec = evaluateTangential(x, y, potentialField);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}
		}

		//TODO Put tangentials on enemy tanks with a close radius (to avoid getting stuck)
		PotentialField tankField = new PotentialField();
		tankField.setRadius(5f);
		tankField.setSpread(5f);
		tankField.setStrength(1f);
		tankField.setType("tangential");
		for(Team team : otherTeams){
			for(Tank tank : team.getTanks()){
				tankField.setPoint(tank.getPosition());
				Point vec = evaluateTangential(x, y, tankField);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}
		}

		return vector;
	}

	/**
	 * Allows all updates to be applied atomically, also think of this as an event handler
	 */
	@Override
	public synchronized void update(String status, int shotsAvailable, float timeToReload, String flag, float positionX, float positionY, float velocityX, float velocityY, float angle, float angleVelocity){
		//Invoke the default behavior
		super.update(status, shotsAvailable, timeToReload, flag, positionX, positionY, velocityX, velocityY, angle, angleVelocity);

		Point vector = evaluate();

		//Probably don't need a speed controller, speed will be derived from magnitude of PF measure: speedController.calculate(distanceToTarget)

		//Calculate the magnitude of the vector to determine how fast the potential fields ask me to be
		float magnitude = (float) Math.sqrt(Math.pow(vector.getX(), 2f) + Math.pow(vector.getY(), 2f));
		desiredSpeed = magnitude;

		//Calculate what the potential fields ask my angle to be, and what kind of difference there is between that and where I am
		float ang = (float) Math.atan2(vector.getY() - 0.0f, vector.getX() - 0.0f);
		float diff = (float) Math.atan2(Math.sin(ang - angle), Math.cos(ang - angle));
		desiredAngularVelocity = angleController.calculate(diff * -1f);
	}

	@Override
	public synchronized boolean getDesiredTriggerStatus(){
		//TODO Don't shoot my team mates
		if(timeToReload <= 0.0f)
			return true;
		return false;
	}

	public synchronized PotentialField getAttractor(){
		return attractor;
	}

	public synchronized void setAttractor(PotentialField attractor){
		this.attractor = attractor;
	}

	public synchronized List<PotentialField> getPotentialFields(){
		return potentialFields;
	}

	public synchronized void setPotentialFields(List<PotentialField> potentialFields){
		this.potentialFields = potentialFields;
	}

	public synchronized PIDController getAngleController(){
		return angleController;
	}

	public synchronized void setAngleController(PIDController angleController){
		this.angleController = angleController;
	}
}
