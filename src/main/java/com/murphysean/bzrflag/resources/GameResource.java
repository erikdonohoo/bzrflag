package com.murphysean.bzrflag.resources;

import com.murphysean.bzrflag.controllers.GameController;
import com.murphysean.bzrflag.models.Game;
import com.murphysean.bzrflag.singletons.GameControllerSingleton;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/games")
public class GameResource{

	@GET
	public Response getGames(){
		List<Game> games = new ArrayList<>();
		for(Map.Entry<String, GameController> entry : GameControllerSingleton.getInstance().getGameControllers()){
			games.add(entry.getValue().getGame());
		}
		return Response.ok(games).build();
	}

	@GET
	@Path("/{gameId}")
	public Response getGame(@PathParam(value="gameId") String gameId){
		GameController gameController = GameControllerSingleton.getInstance().getGameController(gameId);

		if(gameController == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		return Response.ok(gameController.getGame()).build();
	}

	@POST
	public Response startGame(Game game){
		GameController gameController = GameControllerSingleton.getInstance().addGameController(game.getHost(), game.getPort());
		Thread thread = new Thread(gameController, gameController.getGameId());
		thread.start();

		return Response.created(URI.create(gameController.getGameId())).build();
	}

	@DELETE
	@Path("/{gameId}")
	public Response endGame(@PathParam(value="gameId") String gameId){
		GameControllerSingleton.getInstance().removeGameController(gameId);
		return Response.noContent().build();
	}
}
