package com.murphysean.bzrflag.resources;

import com.murphysean.bzrflag.controllers.GameController;
import com.murphysean.bzrflag.models.Team;
import com.murphysean.bzrflag.singletons.GameControllerSingleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/games/{gameId}/teams")
public class TeamResource{
	@GET
	public Response getTeams(@PathParam(value="gameId") String gameId){
		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(gameController.getGame().getTeams()).build();
	}

	@GET
	@Path("/{teamId}")
	public Response getTeam(@PathParam(value="gameId") String gameId, @PathParam(value="teamId") String teamId){
		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		for(Team team : gameController.getGame().getTeams()){
			if(team.getColor().equals(teamId))
				return Response.ok(team).build();
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	@Path("/me")
	public Response getTeam(@PathParam(value="gameId") String gameId){
		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(gameController.getGame().getTeam()).build();
	}
}
