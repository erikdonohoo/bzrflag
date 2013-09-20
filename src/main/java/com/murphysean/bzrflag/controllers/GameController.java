package com.murphysean.bzrflag.controllers;

import com.murphysean.bzrflag.agents.AbstractAgent;
import com.murphysean.bzrflag.agents.DumbAgent;
import com.murphysean.bzrflag.agents.PFAgent;
import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.Game;
import com.murphysean.bzrflag.models.Obstacle;
import com.murphysean.bzrflag.models.Tank;
import com.murphysean.bzrflag.models.Team;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Iterator;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class GameController implements Runnable{
	protected String gameId;
	protected String host;
	protected Integer port;
	protected Game game;

	public GameController(){
		gameId = null;
		host = null;
		port = null;
		game = null;
	}

	public GameController(String gameId, String host, Integer port){
		this.gameId = gameId;
		this.host = host;
		this.port = port;
		game = new Game();
	}

	@Override
	public void run(){
		if(host == null || port == null)
			return;

		try(Game gameRef = new Game(host, port)){
			this.game = gameRef;
			initializeMyTeam(gameRef, "pfagent");

			long its = 1;
			Date startTime = new Date();
			while(gameRef.getGameState().equals("playing")){
				//Read MyTanks
				gameRef.requestMyTanks();
				//Read OccGrids
				//Read Shots
				gameRef.requestShots();
				//Allow PID Controllers to start processing tank movements (Possibly allow pid controllers to submit movement instructions async)
				//Read Flags
				if(its % 15 == 0)
					gameRef.requestFlags();
				//Read Other Tanks
				if(its % 5 == 0)
					gameRef.requestOtherTanks();

				gameRef.requestTime();
				//AI Processing

				//Write Tank Movements, Shots (Could be done async, see above)
				gameRef.updateMyTeam();

				its++;
				if(its % 100 == 0 && its > 0 && (new Date().getTime() - startTime.getTime()) > 1000l)
					System.out.println("FPS: " + its / ((new Date().getTime() - startTime.getTime()) / 1000l));

				//Slow it down to about 100 iterations a second
				Thread.sleep(10);
			}
			Date endTime = new Date();

			System.out.println("FPS: " + its / ((endTime.getTime() - startTime.getTime()) / 1000l));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	protected void initializeMyTeam(Game game, String type){
		game.getTeam().getTanks().clear();
		for(int i = 0; i < game.getTeam().getPlayerCount(); i++){
			AbstractAgent agent = null;
			switch(type){
				case "dumbagent":
					agent = new DumbAgent();
					break;
				case "pfagent":
					PFAgent pfAgent = new PFAgent();
					Iterator<Team> iterator = game.getTeams().iterator();
					while(iterator.hasNext()){
						Team team = iterator.next();
						if(team.getColor().equals(game.getTeam().getColor())){
							pfAgent.setMyTeam(team);
							continue;
						}
						pfAgent.addOtherTeam(team);
					}
					for(Obstacle obstacle : game.getObstacles()){
						pfAgent.addObstacle(obstacle);
					}
					agent = pfAgent;
					break;
				default:
					throw new RuntimeException("Invalid Agent Type");

			}
			agent.setId(i);
			agent.setCallsign(game.getTeam().getColor() + i);
			agent.setTeamColor(game.getTeam().getColor());
			game.getTeam().getTanks().add(agent);
		}
	}

	public synchronized Game getGame(){
		return game;
	}

	public String getGameId(){
		return gameId;
	}

	public String getHost(){
		return host;
	}

	public Integer getPort(){
		return port;
	}
}
