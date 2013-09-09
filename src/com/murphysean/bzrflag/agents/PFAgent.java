package com.murphysean.bzrflag.agents;

import com.murphysean.bzrflag.controllers.PIDController;
import com.murphysean.bzrflag.models.Obstacle;
import com.murphysean.bzrflag.models.Point;
import com.murphysean.bzrflag.models.Team;

import java.util.ArrayList;
import java.util.List;

public class PFAgent extends AbstractAgent{
	PFGenerator generator;
	PIDController angleController;

	//The only state allowed is whether I have a flag or not, which can be garnered from the parent tank object

	public PFAgent(PFGenerator generator){
		this.generator = generator;
		//speedController = new PIDController();
		angleController = new PIDController();
		//I always desire my tank to be 0 degrees from my target rotation
		angleController.setSetPoint(0.0f);
	}

	@Override
	public void setPositionAngle(Float positionX, Float positionY, Float angle){
		Point vector = generator.evaluate(this);

		//Probably don't need a speed controller, speed will be derived from magnitude of PF measure: speedController.calculate(distanceToTarget)

		//Calculate the magnitude of the vector to determine how fast the potential fields ask me to be
		float magnitude = (float)Math.sqrt(Math.pow(vector.getX(), 2f) + Math.pow(vector.getY(), 2f));
		desiredSpeed = magnitude;

		//Calculate what the potential fields ask my angle to be, and what kind of difference there is between that and where I am
		float ang = (float)Math.atan2(vector.getY() - 0.0f, vector.getX() - 0.0f);
		float diff = (float)Math.atan2(Math.sin(ang-angle), Math.cos(ang-angle));
		desiredAngularVelocity = angleController.calculate(diff * -1f);
	}

	@Override
	public Boolean getDesiredTriggerStatus(){
		if(getTimeToReload() <= 0.0f)
			return true;
		return false;
	}

	public static class PFGenerator{
		protected Team myTeam;
		protected List<Team> otherTeams;
		protected List<PotentialField> attractors;
		protected List<PotentialField> rejectors;

		public PFGenerator(){
			otherTeams = new ArrayList<>();
			attractors = new ArrayList<>();
			rejectors = new ArrayList<>();
		}

		public void setMyTeam(Team myTeam){
			this.myTeam = myTeam;
		}

		public void addOtherTeam(Team otherTeam){
			otherTeams.add(otherTeam);
		}

		public void addObstacle(Obstacle obstacle){
			//Turn this into a rejector
			PotentialField rejector = new PotentialField();
			rejector.point = obstacle.getCenterPoint();
			rejector.radius = obstacle.getRadius();
			rejector.spread = obstacle.getRadius() * 1.5f;
			rejector.strength = 2.0f;
			rejectors.add(rejector);
		}

		private Point evaluateAttractor(PFAgent agent, PotentialField attractor){
			//Find the distance between the agent and the goal
			float distance = (float)Math.sqrt(Math.pow(attractor.point.getX() - agent.getPosition().getX(), 2.0d) +
					Math.pow(attractor.point.getY() - agent.getPosition().getY(), 2.0d));
			float angle = (float)Math.atan2(attractor.point.getY() - agent.getPosition().getY(), attractor.point.getX() - agent.getPosition().getX());

			if(distance < attractor.radius)
				return new Point(0.0f, 0.0f);
			if(distance > (attractor.spread + attractor.radius))
				return new Point((float)(attractor.strength * attractor.spread * Math.cos(angle)),(float)(attractor.strength * attractor.spread * Math.sin(angle)));
			return new Point((float)(attractor.strength * (distance - attractor.radius) * Math.cos(angle)), (float)(attractor.strength * (distance - attractor.radius) * Math.sin(angle)));
		}

		private Point evaluateRejector(PFAgent agent, PotentialField rejector){
			float distance = (float)Math.sqrt(Math.pow(rejector.point.getX() - agent.getPosition().getX(), 2.0d) +
					Math.pow(rejector.point.getY() - agent.getPosition().getY(), 2.0d));
			float angle = (float)Math.atan2(rejector.point.getY() - agent.getPosition().getY(), rejector.point.getX() - agent.getPosition().getX());

			//TODO make sure the below is right by figuring out how to make sense of -sign(cos(angle)) * infinity (ie, instead of infinity, I used a large value to make sure the tank wants to stay away)
			if(distance < rejector.radius)
				return new Point((float)( -1.0f * Math.cos(angle) * 100), (float)(-1.0f * Math.sin(angle) * 100));
			if(distance > (rejector.spread + rejector.radius))
				return new Point(0.0f, 0.0f);
			return new Point((float)(-1.0f * rejector.strength * (rejector.spread + rejector.radius - distance) * Math.cos(angle)), (float)(-1.0f * rejector.strength * (rejector.spread + rejector.radius - distance) * Math.sin(angle)));
		}

		public Point evaluate(PFAgent agent){
			Point vector = new Point(0.0f, 0.0f);

			if(agent.getFlag().equals("-") && attractors.size() > 0 && attractors.get(0).point == myTeam.getFlag().getPoint()){
				attractors.clear();
			}

			if(agent.getFlag().equals("-") && attractors.size() == 0){
				//TODO Instead of looking at a random team, how about the team with the most points (besides myself)
				int randomTeam = (int)Math.round(Math.random() * (otherTeams.size() - 1));
				PotentialField attractor = new PotentialField();
				attractor.point = otherTeams.get(randomTeam).getFlag().getPoint();
				attractor.radius = 1.0f;
				attractor.spread = 50f;
				attractor.strength = 2f;
				attractors.add(attractor);
			}

			if(!agent.getFlag().equals("-")){
				attractors.clear();
				PotentialField attractor = new PotentialField();
				//TODO Shouldn't look at flag point as that may move as an enemy captures it
				attractor.point = myTeam.getFlag().getPoint();
				attractor.radius = 1.0f;
				attractor.spread = 50f;
				attractor.strength = 2f;
				attractors.add(attractor);
			}

			for(PotentialField attractor : attractors){
				Point vec = evaluateAttractor(agent, attractor);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}

			for(PotentialField rejector : rejectors){
				Point vec = evaluateRejector(agent, rejector);
				vector.setX(vector.getX() + vec.getX());
				vector.setY(vector.getY() + vec.getY());
			}

			//TODO Tangers

			return vector;
		}

		public class PotentialField{
			public Point point;
			public Float radius;
			public Float spread;
			public Float strength;
		}
	}
}
