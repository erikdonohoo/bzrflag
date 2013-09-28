package com.murphysean.bzrflag.resources;

import com.murphysean.bzrflag.singletons.GameControllerSingleton;
import com.murphysean.bzrflag.agents.AbstractAgent;
import com.murphysean.bzrflag.agents.PFAgent;
import com.murphysean.bzrflag.controllers.GameController;
import com.murphysean.bzrflag.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

@Path("/games/{gameId}/teams/{teamId}/tanks")
public class TankResource{

	@GET
	public Response getTanks(@PathParam(value="gameId") String gameId, @PathParam(value="teamId") String teamId){
		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		Team team = null;
		if(teamId.equals("me")){
			team = gameController.getGame().getTeam();
		}else{
			for(Team t : gameController.getGame().getTeams()){
				if(t.getColor().equals(teamId))
					team = t;
			}
		}

		if(team == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(team.getTanks()).build();
	}

	@GET
	@Path("/{tankId}")
	public Response getTank(@PathParam(value="gameId") String gameId, @PathParam(value="teamId") String teamId, @PathParam(value="tankId") Integer tankId){
		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		Team team = null;
		if(teamId.equals("me")){
			team = gameController.getGame().getTeam();
		}else{
			for(Team t : gameController.getGame().getTeams()){
				if(t.getColor().equals(teamId))
					team = t;
			}
		}

		if(team == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(team.getTanks().get(tankId)).build();
	}

	@GET
	@Path("/{tankId}.gpi")
	@Produces("text/plain")
	public Response getPotentialFieldGraphFileForTank(@PathParam(value="gameId") String gameId,
													  @PathParam(value="teamId") String teamId,
													  @PathParam(value="tankId") Integer tankId,
													  @DefaultValue(value="20") @QueryParam(value="numSteps") Integer numSteps,
													  @DefaultValue(value="Potential Field") @QueryParam(value="title") String title,
													  @DefaultValue(value="all") @QueryParam(value="type") String type,
													  @DefaultValue(value="1") @QueryParam(value="visualMultiplier") Float visualMultiplier){
		if(!teamId.equals("me"))
			return Response.status(Response.Status.FORBIDDEN).build();

		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		Team team = gameController.getGame().getTeam();
		if(tankId >= team.getTanks().size())
			return Response.status(Response.Status.NOT_FOUND).build();
		Tank tank = team.getTanks().get(tankId);
		if(!(tank instanceof PFAgent))
			return Response.status(Response.Status.BAD_REQUEST).build();
		PFAgent agent = (PFAgent)tank;

		return Response.ok(createPotentialFieldGNUPlotString(gameController, agent, numSteps, title, type, visualMultiplier)).build();
	}

	@GET
	@Path("/{tankId}.png")
	@Produces("image/png")
	public Response getPotentialFieldImageForTank(@PathParam(value="gameId") String gameId,
													  @PathParam(value="teamId") String teamId,
													  @PathParam(value="tankId") Integer tankId,
													  @DefaultValue(value="20") @QueryParam(value="numSteps") Integer numSteps,
													  @DefaultValue(value="Potential Field") @QueryParam(value="title") String title,
													  @DefaultValue(value="all") @QueryParam(value="type") String type,
													  @DefaultValue(value="1") @QueryParam(value="visualMultiplier") Float visualMultiplier){
		if(!teamId.equals("me"))
			return Response.status(Response.Status.FORBIDDEN).build();

		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		Team team = gameController.getGame().getTeam();
		if(tankId >= team.getTanks().size())
			return Response.status(Response.Status.NOT_FOUND).build();
		Tank tank = team.getTanks().get(tankId);
		if(!(tank instanceof PFAgent))
			return Response.status(Response.Status.BAD_REQUEST).build();
		PFAgent agent = (PFAgent)tank;

		String gnuplot = createPotentialFieldGNUPlotString(gameController, agent, numSteps, title, type, visualMultiplier);

		//Take this gnuplot string, and shuffle it to a file, then pass the file into gnuplot as an argument
		try{
			String uuid = UUID.randomUUID().toString();
			File tmpGNUPlotFile = File.createTempFile(uuid, ".gpi");
			FileWriter fw = new FileWriter(tmpGNUPlotFile.getAbsoluteFile());
			fw.write(gnuplot);
			fw.flush();
			fw.close();

			File tmpGNUPlotImage = new File(System.getProperty("java.io.tmpdir") + "/" + uuid + ".png");

			//gnuplot -e "set term png;set output 'fields.png'" test.gpi
			ProcessBuilder processBuilder = new ProcessBuilder("gnuplot", "-e", "set term png;set output '" + tmpGNUPlotImage.getAbsolutePath() + "'", tmpGNUPlotFile.getAbsolutePath());
			Process p = processBuilder.start();
			p.waitFor();

			return Response.ok(tmpGNUPlotImage).build();
		}catch(IOException e){
			throw new RuntimeException(e);
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
	}

	private String createPotentialFieldGNUPlotString(GameController gameController, PFAgent agent, int numSteps, String title, String type, float visualMultiplier){
		StringWriter stringWriter = new StringWriter();

		stringWriter.write("#Auto Generated PF Field GNUPlot file\n");
		stringWriter.write("#Generated on " + new Date().toGMTString() + "\n");
		stringWriter.write("set title \"" + title + "\"\n");

		//Lay down ranges
		Integer worldSize = gameController.getGame().getWorldSize();
		Integer worldSizeHalf = worldSize / 2;
		stringWriter.write("set xrange [-" + worldSizeHalf + ":"+ worldSizeHalf + "]\n");
		stringWriter.write("set yrange [-" + worldSizeHalf + ":"+ worldSizeHalf + "]\n");
		stringWriter.write("unset key\n");
		stringWriter.write("set size square\n");

		//Draw Obstacles
		stringWriter.write("unset arrow\n");
		for(Obstacle obstacle : gameController.getGame().getObstacles()){
			for(int i = 0; i < obstacle.getPoints().size(); i++){
				Point one = obstacle.getPoints().get(i);
				Point two = obstacle.getPoints().get((i+1) % obstacle.getPoints().size());
				stringWriter.write("set arrow from " + one.getX() + ", " + one.getY() + " to " + two.getX() + ", " + two.getY() + " nohead lt 3\n");
			}
		}

		//Write in some metadata about the agent
		stringWriter.write("#Agent is @: " + agent.getPosition().getX() + ", " + agent.getPosition().getY() + " with flag: " + agent.getFlag() + "\n");
		Point realVector = agent.evaluate(agent.getPosition().getX(), agent.getPosition().getY(), type);
		stringWriter.write("#Agent is experiencing: " + realVector.getX() + ", " + realVector.getY() + "\n");

		stringWriter.write("plot '-' with vectors head\n");
		Integer stepSize = worldSize / numSteps;
		//TODO Break up the area into discrete points and run the vector for each point against the vector generator for the tank
		for(int i = numSteps - 1; i >= 0; i--){
			for(int j = 0; j < numSteps; j++){
				//Going to go from this point to this point plus the vector for this point
				//This point

				float x = ((stepSize * j) + (stepSize / 2)) - worldSizeHalf;
				float y = ((stepSize * i) + (stepSize / 2)) - worldSizeHalf;
				stringWriter.write("#FromPoint: " + x + ", " + y + "\n");
				//The vector
				Point p = agent.evaluate(x,y,type);
				stringWriter.write("#Vector: " + p.getX() + ", " + p.getY() + "\n");
				float vx = x + p.getX();
				float vy = y + p.getY();
				stringWriter.write("#To Point: " + vx + ", " + vy + "\n");

				stringWriter.write(x + " " + y + " " + " " + (p.getX() * visualMultiplier) + " " + (p.getY() * visualMultiplier) + "\n");
			}
		}
		stringWriter.write("e\n");

		return stringWriter.toString();
	}

	@PUT
	@Path("/{tankId}")
	public Response updateTank(@PathParam(value="gameId") String gameId, @PathParam(value="teamId") String teamId, @PathParam(value="tankId") Integer tankId, AbstractAgent agent){
		//TODO Make sure this is for one of my tanks... and that everything is ok?
		if(!teamId.equals("me"))
			return Response.status(Response.Status.FORBIDDEN).build();

		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);
		Game game = gameController.getGame();

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		Team team = gameController.getGame().getTeam();
		Tank tank = team.getTanks().get(tankId);
		//Ensure that the tank picks up where the last one left off
		agent.setPosition(tank.getPosition());
		agent.setAngle(tank.getAngle());
		agent.setFlag(tank.getFlag());

		//Bootstrap the pf agent by setting some of the transient properties
		if(agent instanceof PFAgent){
			PFAgent pfAgent = (PFAgent)agent;

			Iterator<Team> iterator = game.getTeams().iterator();
			while(iterator.hasNext()){
				Team otherTeam = iterator.next();
				if(otherTeam.getColor().equals(game.getTeam().getColor())){
					pfAgent.setMyTeam(otherTeam);
					continue;
				}
				pfAgent.addOtherTeam(otherTeam);
			}
			//TODO Figure out if it would be helpful to add in the obstacles here
			//Might be useful if we just want to switch the type of the tank and so we would be hoping the server would do this
			//Wouldn't be useful if we are trying to change some of the obstacles from the client to have different properties
			/*for(Obstacle obstacle : game.getObstacles()){
				pfAgent.addObstacle(obstacle);
			}*/
		}

		team.getTanks().set(tankId, agent);
		return Response.ok(agent).build();
	}
}
