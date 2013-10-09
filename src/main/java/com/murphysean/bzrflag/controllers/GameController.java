package com.murphysean.bzrflag.controllers;

import com.murphysean.bzrflag.agents.AbstractAgent;
import com.murphysean.bzrflag.agents.DumbAgent;
import com.murphysean.bzrflag.agents.PFAgent;
import com.murphysean.bzrflag.commanders.PFEvolutionCommander;
import com.murphysean.bzrflag.communicators.BZRFlagInputCommunicator;
import com.murphysean.bzrflag.communicators.BZRFlagOutputCommunicator;
import com.murphysean.bzrflag.models.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class GameController implements Runnable, AutoCloseable{
	protected String gameId;
	protected String host;
	protected Integer port;
	protected Game game;

	protected Socket socket;
	protected BufferedReader bufferedReader;
	protected BufferedWriter bufferedWriter;


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

		game = new Game(gameId, host, port);
	}

	@Override
	public void close() throws Exception{
		game.setState("closing");
		bufferedWriter.close();
		bufferedReader.close();
		socket.close();
		game.setState("closed");
	}

	@Override
	public void run(){
		if(host == null || port == null)
			return;

		try{
			game.setState("connecting");
			//Open a socket
			socket = new Socket(host, port);
			//Handshake
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			//Make one time calls to initialize the agent
			//These need to be syncronous calls
			game.setState("initializing");
			handshake();
			readConstants();
			readTeams();
			readObstacles();
			readBases();
			readFlags();

			//Make sure the threads start
			game.setState("playing");

			//Spin out threads to handle async
			BZRFlagInputCommunicator inputCommunicator = new BZRFlagInputCommunicator(this);
			Thread inputThread = new Thread(inputCommunicator, "bzrflagInputCommunicator");
			inputThread.start();
			BZRFlagOutputCommunicator outputCommunicator = new BZRFlagOutputCommunicator(this);
			Thread outputThread = new Thread(outputCommunicator, "bzrflagOutputCommunicator");
			outputThread.start();

			PFEvolutionCommander commander = new PFEvolutionCommander();
			commander.setGame(game);

			long its = 1;
			Date lastLoop = new Date();
			while(game.getState().equals("playing")){
				//Slow it down to about 100 iterations a second
				Date now = new Date();
				long timeDiff = now.getTime() - lastLoop.getTime();
				if(timeDiff < 10)
					Thread.sleep(10 - timeDiff);

				//TODO Take a little time off if the bufferes are full
				//while(socket.getInputStream().available() > 2048)
				//	Thread.sleep(10);

				//Send off requests for information
				outputCommunicator.requestMyTanks();
				outputCommunicator.requestShots();
				//Read Flags
				if(its % 50 == 0){
					//TODO outputCommunicator.requestOccGrids();
					outputCommunicator.requestFlags();
				}
				//Read Other Tanks
				if(its % 2 == 0)
					outputCommunicator.requestOtherTanks();

				outputCommunicator.requestTime();

				//Write Tank Movements, Shots (Could be done async, see above)
				outputCommunicator.updateMyTeam();

				its++;
				now = new Date();
				timeDiff = now.getTime() - lastLoop.getTime();
				game.setHertz(1 / (timeDiff / 1000f));
				lastLoop = now;
			}

			close();
		}catch(Exception e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	protected void initializeMyTeam(Game game, String type){
		game.getTeam().getTanks().clear();
		for(int i = 0; i < game.getTeam().getPlayerCount(); i++){
			AbstractAgent agent = null;


			game.getTeam().getTanks().add(agent);
		}
	}

	protected void handshake(){
		try{
			String serverMessage = bufferedReader.readLine();
			if(!serverMessage.startsWith("bzrobots"))
				throw new RuntimeException("Invalid Server Message");

			game.setVersion(serverMessage.split(" ")[1]);
			bufferedWriter.write("agent " + game.getVersion() + "\n");
			bufferedWriter.flush();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	protected void readConstants(){
		try{
			bufferedWriter.write("constants\n");
			bufferedWriter.flush();

			//Eat up the ack
			String response = bufferedReader.readLine();
			if(!response.startsWith("ack"))
				throw new RuntimeException("Missing Ack");
			response = bufferedReader.readLine();
			if(response.equals("begin")){
				response = bufferedReader.readLine();
				while(!response.equals("end")){
					String[] parts = response.split("\\s+");
					switch(parts[1]){
						case "team":
							game.setTeamColor(parts[2]);
							break;
						case "worldsize":
							game.setWorldSize(Integer.valueOf(parts[2]));
							break;
						case "tankangvel":
							game.setTankAngVel(Float.valueOf(parts[2]));
							break;
						case "tanklength":
							game.setTankLength(Float.valueOf(parts[2]));
							break;
						case "tankwidth":
							game.setTankWidth(Float.valueOf(parts[2]));
							break;
						case "tankradius":
							game.setTankRadius(Float.valueOf(parts[2]));
							break;
						case "tankspeed":
							game.setTankSpeed(Float.valueOf(parts[2]));
							break;
						case "tankalive":
							game.setTankAlive(parts[2]);
							break;
						case "tankdead":
							game.setTankDead(parts[2]);
							break;
						case "linearaccel":
							game.setLinearAccel(Float.valueOf(parts[2]));
							break;
						case "angularaccel":
							game.setAngularAccel(Float.valueOf(parts[2]));
							break;
						case "shotradius":
							game.setShotRadius(Float.valueOf(parts[2]));
							break;
						case "shotrange":
							game.setShotRange(Float.valueOf(parts[2]));
							break;
						case "shotspeed":
							game.setShotSpeed(Float.valueOf(parts[2]));
							break;
						case "flagradius":
							game.setFlagRadius(Float.valueOf(parts[2]));
							break;
						case "explodetime":
							game.setExplodeTime(Float.valueOf(parts[2]));
							break;
						case "truepositive":
							game.setTruePositive(Float.valueOf(parts[2]));
							break;
						case "truenegative":
							game.setTrueNegative(Float.valueOf(parts[2]));
							break;
						default:
							throw new RuntimeException("Invalid Constant Value");
					}
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	protected void readTeams(){
		try{
			bufferedWriter.write("teams\n");
			bufferedWriter.flush();

			//Eat up the ack
			String response = bufferedReader.readLine();
			if(!response.startsWith("ack"))
				throw new RuntimeException("Missing Ack");
			response = bufferedReader.readLine();
			if(response.equals("begin")){
				response = bufferedReader.readLine();
				while(!response.equals("end")){
					Team team = new Team(response);
					if(team.getColor().equals(game.getTeamColor())){
						game.setTeam(team);
					}else{
						game.getTeams().add(team);
					}
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	protected void readObstacles(){
		try{
			bufferedWriter.write("obstacles\n");
			bufferedWriter.flush();

			//Eat up the ack
			String response = bufferedReader.readLine();
			if(!response.startsWith("ack"))
				throw new RuntimeException("Missing Ack");
			response = bufferedReader.readLine();
			if(response.equals("begin")){
				response = bufferedReader.readLine();
				while(!response.equals("end")){
					Obstacle obstacle = new Obstacle(response);
					game.getObstacles().add(obstacle);
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	protected void readBases(){
		try{
			bufferedWriter.write("bases\n");
			bufferedWriter.flush();

			//Eat up the ack
			String response = bufferedReader.readLine();
			if(!response.startsWith("ack"))
				throw new RuntimeException("Missing Ack");
			response = bufferedReader.readLine();
			if(response.equals("begin")){
				response = bufferedReader.readLine();
				while(!response.equals("end")){
					Base base = new Base(response);
					assignBaseToTeam(base);
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	protected void assignBaseToTeam(Base base){
		if(game.getTeam().getColor().equals(base.getTeamColor()))
			game.getTeam().setBase(base);
		for(Team team : game.getTeams()){
			if(team.getColor().equals(base.getTeamColor()))
				team.setBase(base);
		}
	}

	public void readFlags(){
		try{
			bufferedWriter.write("flags\n");
			bufferedWriter.flush();

			//Eat up the ack
			String response = bufferedReader.readLine();
			if(!response.startsWith("ack"))
				throw new RuntimeException("Missing Ack");
			response = bufferedReader.readLine();
			if(response.equals("begin")){
				response = bufferedReader.readLine();
				while(!response.equals("end")){
					assignFlagToTeam(response);
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			game.setState("errored");
			throw new RuntimeException(e);
		}
	}

	protected void assignFlagToTeam(String serverString){
		String[] parts = serverString.split("\\s+");
		if(game.getTeam().getColor().equals(parts[1])){
			game.getTeam().getFlag().setPossessingTeamColor(parts[2]);
			game.getTeam().getFlag().getPoint().setX(Float.valueOf(parts[3]));
			game.getTeam().getFlag().getPoint().setY(Float.valueOf(parts[4]));
		}
		for(Team team : game.getTeams()){
			if(team.getColor().equals(parts[1])){
				team.getFlag().setPossessingTeamColor(parts[2]);
				team.getFlag().getPoint().setX(Float.valueOf(parts[3]));
				team.getFlag().getPoint().setY(Float.valueOf(parts[4]));
			}
		}
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

	public Game getGame(){
		return game;
	}

	public Socket getSocket(){
		return socket;
	}

	public BufferedReader getBufferedReader(){
		return bufferedReader;
	}

	public BufferedWriter getBufferedWriter(){
		return bufferedWriter;
	}
}
