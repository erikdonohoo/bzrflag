package com.murphysean.bzrflag.agents;

import com.murphysean.bzrflag.commanders.PFEvolutionCommander;
import com.murphysean.bzrflag.controllers.PIDController;
import com.murphysean.bzrflag.events.BZRFlagEvent;
import com.murphysean.bzrflag.interfaces.Commander;
import com.murphysean.bzrflag.models.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class PFAgent extends AbstractAgent{
	@JsonIgnore
	protected transient Game game;
	protected PIDController angleController;
	protected PFGene gene;

	protected String goal;
	protected PotentialField attractor;

	protected transient Date startTime;
	protected boolean retHome;

	public PFAgent(){
		//The commander give us one later, for now make sure I've actually got one
		gene = PFGene.generateRandomPFGene();
		goal = "wait";
		retHome = false;
		type = "pf";
		angleController = new PIDController();
		//I always desire my tank to be 0 degrees from my target rotation
		angleController.setSetPoint(0.0f);
	}

	@JsonIgnore
	public synchronized void setGame(Game game){
		this.game = game;
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

	private String decrepedGene = null;
	public synchronized Point evaluate(float x, float y, String type){
		//If I have a goal to wait, my vector will be a zero point
		if(goal.equals("wait"))
			return new Point(0f,0f);

		//If I've been gone above the threshold, set return home flag
		if(startTime != null && (new Date().getTime() - startTime.getTime() > 280000)){
			//Get a gene that works...
			decrepedGene = gene.getGene();
			gene = PFGene.getKnownWorkingPFGene();
			retHome = true;
			PotentialField attractor = new PotentialField();
			attractor.setType("attractor");
			attractor.setPoint(game.getTeam().getBase().getCenterPoint());
			attractor.setRadius(gene.getAttRadius());
			attractor.setSpread(gene.getAttSpread());
			attractor.setStrength(gene.getAttStrength());
			this.attractor = attractor;
		}


		if(retHome){
			//Calculate distance from me to my flag, if I'm home restart
			float distance = (float) Math.sqrt(Math.pow(game.getTeam().getBase().getCenterPoint().getX() - position.getX(), 2.0d) +
					Math.pow(game.getTeam().getBase().getCenterPoint().getY() - position.getY(), 2.0d));

			if(distance < game.getTeam().getBase().getRadius()){
				retHome = false;
				//Notify commander of my lack of awesomeness through event
				if(game.getTeam() instanceof Commander){
					String note = game.getTeam().getColor() + "->" + goal + " TIMEOUT";
					PFEvolutionCommander.PFTankEvent event = new PFEvolutionCommander.PFTankEvent(this, decrepedGene, 280000l, note);
					((Commander)game.getTeam()).sendBZRFlagEvent(event);
					decrepedGene = null;
				}
			}
		}


		Point vector = new Point(0.0f, 0.0f);

		//I've just lost the flag (By returning it to my base) and I still have my base as my attractor
		if(!retHome && flag.equals("-") && attractor != null && attractor.getPoint().equals(game.getTeam().getFlag().getPoint())){
			attractor = null;
		}

		//I don't have a flag, and I don't have an attractor, I need something to go get
		if(!retHome && flag.equals("-") && this.attractor == null && goal != null){
			Date now = new Date();
			//Notify commander of last time to get flag, Ask the commander for a new gene to test?
			if(game.getTeam() instanceof Commander && (now.getTime() - startTime.getTime()) >= 500){
				String note = game.getTeam().getColor() + "->" + goal;
				PFEvolutionCommander.PFTankEvent event = new PFEvolutionCommander.PFTankEvent(this, gene.getGene(), now.getTime() - startTime.getTime(), note);
				((Commander)game.getTeam()).sendBZRFlagEvent(event);
			}
			PotentialField attractor = new PotentialField();
			attractor.setType("attractor");
			attractor.setPoint(game.findTeamByColor(goal).getBase().getCenterPoint());
			attractor.setRadius(gene.getAttRadius());
			attractor.setSpread(gene.getAttSpread());
			attractor.setStrength(gene.getAttStrength());
			this.attractor = attractor;
		}

		//I've managed to nab a flag, but my attractor is currently set to the flag that I have just now captured
		if(!retHome && !flag.equals("-") && attractor != null && attractor.getPoint().equals(game.findTeamByColor(goal).getBase().getCenterPoint())){
			attractor = null;
		}

		//I have a flag, and nowhere to go with it, send me home
		if(!retHome && !flag.equals("-") && attractor == null){
			attractor = null;
			PotentialField attractor = new PotentialField();
			attractor.setType("attractor");
			attractor.setPoint(game.getTeam().getBase().getCenterPoint());
			attractor.setRadius(gene.getAttRadius());
			attractor.setSpread(gene.getAttSpread());
			attractor.setStrength(gene.getAttStrength());
			this.attractor = attractor;
		}

		if(type.equals("attractors") || type.equals("all")){
			Point vec = evaluateAttractor(x, y, this.attractor);
			vector.setX(vector.getX() + vec.getX());
			vector.setY(vector.getY() + vec.getY());
		}

		PotentialField rejectorPotentialField = new PotentialField();
		rejectorPotentialField.setType("rejector");
		rejectorPotentialField.setStrength(gene.getRejStrength());
		PotentialField tangentialPotentialField = new PotentialField();
		tangentialPotentialField.setType("tangential");
		tangentialPotentialField.setStrength(gene.getRejStrength());
		for(Obstacle obstacle : game.getObstacles()){
			if(type.contains("rejectors") || type.equals("all")){
				rejectorPotentialField.setPoint(obstacle.getCenterPoint());
				rejectorPotentialField.setRadius(obstacle.getRadius() * gene.getRejRadius());
				rejectorPotentialField.setSpread(obstacle.getRadius() * gene.getRejSpread());
				Point vec = evaluateRejector(x, y, rejectorPotentialField);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}
			if(type.contains("tangentials") || type.equals("all")){
				tangentialPotentialField.setPoint(obstacle.getCenterPoint());
				tangentialPotentialField.setRadius(obstacle.getRadius() * gene.getTanRadius());
				tangentialPotentialField.setSpread(obstacle.getRadius() * gene.getTanSpread());
				Point vec = evaluateTangential(x, y, tangentialPotentialField);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}
		}

		//Put tangentials on enemy tanks with a close radius (to avoid getting stuck)
		PotentialField tankField = new PotentialField();
		tankField.setRadius(game.getTankRadius());
		tankField.setSpread(game.getTankRadius() / 2);
		tankField.setStrength(1f);
		tankField.setType("tangential");
		for(Team team : game.getTeams()){
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
		//if(timeToReload <= 0.0f)
		//	return true;
		return false;
	}

	public PIDController getAngleController(){
		return angleController;
	}

	public void setAngleController(PIDController angleController){
		this.angleController = angleController;
	}

	public PFGene getGene(){
		return gene;
	}

	public void setGene(PFGene gene){
		startTime = new Date();
		this.gene = gene;
	}

	public String getGoal(){
		return goal;
	}

	public void setGoal(String goal){
		this.goal = goal;
	}

	public PotentialField getAttractor(){
		return attractor;
	}

	public void setAttractor(PotentialField attractor){
		this.attractor = attractor;
	}

	public Date getStartTime(){
		return startTime;
	}

	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

	public boolean isRetHome(){
		return retHome;
	}

	public void setRetHome(boolean retHome){
		this.retHome = retHome;
	}
}