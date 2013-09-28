package com.murphysean.bzrflag.communicators;

import com.murphysean.bzrflag.controllers.GameController;
import com.murphysean.bzrflag.models.*;

import java.io.BufferedReader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The BZRFlag Input Communicator will continoulsly read the input from the bzrflag server and update the game world
 * so that it stays in sync with the server. The reading will block on input streams from the server, and upon recieving
 * a message will hand it off to a queue on the gameController. The game controller will then process this queue and
 * update the game world.
 */
public class BZRFlagInputCommunicator implements Runnable{
	private Game game;
	private GameController gameController;
	private BufferedReader bufferedReader;

	public BZRFlagInputCommunicator(GameController gameController){
		this.gameController = gameController;
		bufferedReader = gameController.getBufferedReader();
		this.game = gameController.getGame();
	}

	@Override
	public void run(){
		try{
			while(game.getState() == "playing"){
				String responseLine = bufferedReader.readLine();

				//Parse the line
				if(responseLine.startsWith("ack"))
					continue;
				if(responseLine.startsWith("ok"))
					continue;
				if(responseLine.startsWith("begin"))
					continue;
				if(responseLine.startsWith("end"))
					continue;

				if(responseLine.startsWith("fail"))
					continue;

				readResponse(responseLine);
			}
		}catch(Exception e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	public void readResponse(String responseLine){
		String[] parts = responseLine.split("\\s+");

		switch(parts[0]){
			case "mytank":
				readMyTank(Integer.valueOf(parts[1]), parts[2], parts[3], Integer.valueOf(parts[4]), Float.valueOf(parts[5]), parts[6], Float.valueOf(parts[7]), Float.valueOf(parts[8]), Float.valueOf(parts[9]), Float.valueOf(parts[10]), Float.valueOf(parts[11]), Float.valueOf(parts[12]));
				break;
			case "shot":
				readShot(Float.valueOf(parts[1]), Float.valueOf(parts[2]), Float.valueOf(parts[3]), Float.valueOf(parts[4]));
				break;
			case "othertank":
				readOtherTank(parts[1], parts[2], parts[3], parts[4], Float.valueOf(parts[5]), Float.valueOf(parts[6]), Float.valueOf(parts[7]));
				break;
			case "timer":
				game.setTimeElapsed(Float.valueOf(parts[1]));
				game.setTimeLimit(Float.valueOf(parts[2]));
				break;
			case "score":
				readScore(parts[1], parts[2], Integer.valueOf(parts[3]));
				break;
			case "flag":
				readFlag(parts[1], parts[2], Float.valueOf(parts[3]), Float.valueOf(parts[4]));
				break;
			case "team":
				readTeam(parts[1], Integer.valueOf(parts[2]));
				break;
			case "obstacle":
				readObstacle(responseLine);
				break;
			case "base":
				readBase(responseLine);
				break;
			case "constant":
				readConstant(parts[1], parts[2]);
				break;
			default:
				throw new RuntimeException("Unknown Response");
		}
	}

	public void readTeam(String color, Integer playerCount){
		//Probably won't be making this call async
	}

	public void readObstacle(String responseLine){
		//Probably won't be making this call async
	}

	public void readBase(String responseLine){
		//Probably won't be making this call async
	}

	public void readFlag(String teamColor, String possessingTeamColor, float x, float y){
		for(Team team : game.getTeams()){
			if(team.getColor().equals(teamColor)){
				team.getFlag().setPossessingTeamColor(possessingTeamColor);
				team.getFlag().getPoint().setX(x);
				team.getFlag().getPoint().setY(y);
			}

		}
	}

	public void readScore(String teamColor, String otherTeamColor, int score){
		for(Team team : game.getTeams()){
			if(team.getColor().equals(teamColor)){
				team.setScore(otherTeamColor, score);
			}
		}
	}

	public void readShot(float x, float y, float vx, float vy){
		Shot shot = new Shot();
		shot.setPoint(new Point(x, y));
		shot.setVelocity(new Point(vx, vy));

		//TODO Notify tanks of this shot, or maybe notify team
		//Someone will need to process the shots and determine if any of them are a risk to tank/team
		//I could keep track of shot from frame to frame based on it's trajectory or something, however this seems like a waste

		//TODO The reaction to shots should be reflexive, meaning that it should be fast and not blocked by other 'thoughts', 'actions'
	}

	public void readMyTank(int tankIndex, String callsign, String status, int shotsAvailable, float timeToReload, String flag, float x, float y, float angle, float vx, float vy, float vangle){
		Tank tank = game.getTeam().getTanks().get(tankIndex);
		tank.update(status, shotsAvailable, timeToReload, flag, x, y, vx, vy, angle, vangle);
	}

	public void readOtherTank(String callsign, String color, String status, String flag, float x, float y, float angle){
		for(Team team : game.getTeams()){
			if(team.getColor().equals(color)){
				int index = Integer.valueOf(callsign.replaceAll("\\D", ""));
				Tank tank = team.getTanks().get(index);
				tank.update(status, flag, x, y, angle);
				break;
			}
		}
	}

	public void readConstant(String name, String value){
		switch(name){
			case "team":
				game.setTeamColor(value);
				break;
			case "worldsize":
				game.setWorldSize(Integer.valueOf(value));
				break;
			case "tankangvel":
				game.setTankAngVel(Float.valueOf(value));
				break;
			case "tanklength":
				game.setTankLength(Float.valueOf(value));
				break;
			case "tankwidth":
				game.setTankWidth(Float.valueOf(value));
				break;
			case "tankradius":
				game.setTankRadius(Float.valueOf(value));
				break;
			case "tankspeed":
				game.setTankSpeed(Float.valueOf(value));
				break;
			case "tankalive":
				game.setTankAlive(value);
				break;
			case "tankdead":
				game.setTankDead(value);
				break;
			case "linearaccel":
				game.setLinearAccel(Float.valueOf(value));
				break;
			case "angularaccel":
				game.setAngularAccel(Float.valueOf(value));
				break;
			case "shotradius":
				game.setShotRadius(Float.valueOf(value));
				break;
			case "shotrange":
				game.setShotRange(Float.valueOf(value));
				break;
			case "shotspeed":
				game.setShotSpeed(Float.valueOf(value));
				break;
			case "flagradius":
				game.setFlagRadius(Float.valueOf(value));
				break;
			case "explodetime":
				game.setExplodeTime(Float.valueOf(value));
				break;
			case "truepositive":
				game.setTruePositive(Float.valueOf(value));
				break;
			case "truenegative":
				game.setTrueNegative(Float.valueOf(value));
				break;
			default:
				throw new RuntimeException("Invalid Constant Value");
		}
	}
}
