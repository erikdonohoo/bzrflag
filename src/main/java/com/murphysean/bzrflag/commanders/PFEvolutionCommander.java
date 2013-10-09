package com.murphysean.bzrflag.commanders;

import com.murphysean.bzrflag.agents.PFAgent;
import com.murphysean.bzrflag.daos.PFGenDAO;
import com.murphysean.bzrflag.events.BZRFlagEvent;
import com.murphysean.bzrflag.models.Game;
import com.murphysean.bzrflag.models.PFGene;
import com.murphysean.bzrflag.models.Tank;
import com.murphysean.bzrflag.models.Team;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PFEvolutionCommander extends AbstractCommander{
	@JsonIgnore
	private transient PFGenDAO pfGenDAO = new PFGenDAO();
	private transient List<PFGene> genes;

	public PFEvolutionCommander(){
		super();
	}

	@Override
	public void setGame(Game game){
		super.setGame(game);

		//Pull in a list of all the current db genes
		genes = pfGenDAO.readPFGenes(1);
		if(genes.size() <= 0)
			generate();

		//Set up my team right here
		for(int i = 0; i < playerCount; i++){
			PFAgent pfAgent = new PFAgent();
			pfAgent.setGame(game);
			pfAgent.setId(i);
			pfAgent.setCallsign(game.getTeam().getColor() + i);
			pfAgent.setTeamColor(game.getTeam().getColor());
			String goal = game.getTeams().get(i % game.getTeams().size()).getColor();
			pfAgent.setGoal(goal);
			int random = (int)Math.floor(Math.random() * 99.9d);
			pfAgent.setGene(genes.get(random));
			tanks.add(pfAgent);
		}
	}

	protected void generate(){
		//I want to come up with a random set of genes that I can begin the process with

		for(int i = 0; i < 100; i++){
			genes.add(pfGenDAO.createPFGene(PFGene.generateRandomPFGene()));
		}
	}

	protected void evolve(){
		//TODO Somehow generate a new generation from the current one using mutations, cross-combinations, and other genetic recombinations of the more healthy genes from the last generation
	}

	@Override
	public void sendBZRFlagEvent(BZRFlagEvent event){
		if(event instanceof PFTankEvent){
			PFTankEvent pfTankEvent = (PFTankEvent)event;

			//Save the gene and time to the database
			pfGenDAO.createPFGeneFitness(((PFTankEvent)event).getGene(), ((PFTankEvent)event).getTime(), "default", ((PFTankEvent)event).getNote());
			//Get the tank a new gene to test
			int random = (int)Math.floor(Math.random() * 99.99d);
			((PFTankEvent)event).getPfAgent().setGene(genes.get(random));
		}
	}

	public static class PFTankEvent extends BZRFlagEvent{
		public static final String PF_TANK_FINISHED = "finished";

		protected PFAgent pfAgent;
		protected String gene;
		protected long time;
		protected String note;

		public PFTankEvent(PFAgent pfAgent, String gene, long time, String note){
			super(PF_TANK_FINISHED);

			this.pfAgent = pfAgent;
			this.gene = gene;
			this.time = time;
			this.note = note;
		}

		public PFAgent getPfAgent(){
			return pfAgent;
		}

		public String getGene(){
			return gene;
		}

		public long getTime(){
			return time;
		}

		public String getNote(){
			return note;
		}
	}
}
