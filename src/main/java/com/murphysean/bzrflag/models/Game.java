package com.murphysean.bzrflag.models;

import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Game implements AutoCloseable{
	//connecting, initializing, playing, quitting, finished
	protected String id;
	protected String gameState;

	//Protocol Version
	protected String version;

	//Constants
	protected String teamColor;
	protected Integer worldSize;
	protected Float tankAngVel;
	protected Float tankLength;
	protected Float tankRadius;
	protected Float tankSpeed;
	protected String tankAlive;
	protected String tankDead;
	protected Float linearAccel;
	protected Float angularAccel;
	protected Float tankWidth;
	protected Float shotRadius;
	protected Float shotRange;
	protected Float shotSpeed;
	protected Float flagRadius;
	protected Float explodeTime;
	protected Float truePositive;
	protected Float trueNegative;

	//Game Time
	protected Float timeElapsed;
	protected Float timeLimit;

	//Internal Objects
	protected Team team;
	protected Set<Team> teams;
	protected List<Obstacle> obstacles;

	//Communication Objects
	protected String host;
	protected Integer port;
	protected transient Socket socket;
	protected transient BufferedReader bufferedReader;
	protected transient BufferedWriter bufferedWriter;

	public Game(){
		gameState = "instantiated";
	}

	public Game(String host, int port){
		this.host = host;
		this.port = port;
		try{
			gameState = "connecting";
			//Open a socket
			socket = new Socket(host, port);
			//Handshake
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			throw new RuntimeException(e);
		}

		teams = new HashSet<>();
		obstacles = new ArrayList<>();

		//Make one time calls to initialize the agent
		//These need to be syncronous calls
		gameState = "initializing";
		handshake();
		readConstants();
		readTeams();
		readObstacles();
		readBases();
		readFlags();

		//Initialize state
		gameState = "playing";

		//Start up a thread specifically for reading
		Thread thread = new Thread(readThread, "bzrserverreader");
		thread.start();
	}

	public void endGame(){
		//This should kill any current game loops iterating on a "playing" state
		gameState = "closing";
	}

	@Override
	public void close() throws Exception{
		bufferedWriter.close();
		bufferedReader.close();
		socket.close();
	}

	protected void handshake(){
		try{
			String serverMessage = bufferedReader.readLine();
			if(!serverMessage.startsWith("bzrobots"))
				throw new RuntimeException("Invalid Server Message");

			version = serverMessage.split(" ")[1];
			bufferedWriter.write("agent " + version + "\n");
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
							teamColor = parts[2];
							break;
						case "worldsize":
							worldSize = Integer.valueOf(parts[2]);
							break;
						case "tankangvel":
							tankAngVel = Float.valueOf(parts[2]);
							break;
						case "tanklength":
							tankLength = Float.valueOf(parts[2]);
							break;
						case "tankwidth":
							tankWidth = Float.valueOf(parts[2]);
							break;
						case "tankradius":
							tankRadius = Float.valueOf(parts[2]);
							break;
						case "tankspeed":
							tankSpeed = Float.valueOf(parts[2]);
							break;
						case "tankalive":
							tankAlive = parts[2];
							break;
						case "tankdead":
							tankDead = parts[2];
							break;
						case "linearaccel":
							linearAccel = Float.valueOf(parts[2]);
							break;
						case "angularaccel":
							angularAccel = Float.valueOf(parts[2]);
							break;
						case "shotradius":
							shotRadius = Float.valueOf(parts[2]);
							break;
						case "shotrange":
							shotRange = Float.valueOf(parts[2]);
							break;
						case "shotspeed":
							shotSpeed = Float.valueOf(parts[2]);
							break;
						case "flagradius":
							flagRadius = Float.valueOf(parts[2]);
							break;
						case "explodetime":
							explodeTime = Float.valueOf(parts[2]);
							break;
						case "truepositive":
							truePositive = Float.valueOf(parts[2]);
							break;
						case "truenegative":
							trueNegative = Float.valueOf(parts[2]);
							break;
						default:
							throw new RuntimeException("Invalid Constant Value");
					}
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			gameState = "errored";
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
					if(team.getColor().equals(teamColor)){
						this.team = team;
					}
					teams.add(team);
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			gameState = "errored";
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
					obstacles.add(obstacle);
					response = bufferedReader.readLine();
				}
			}
		}catch(IOException e){
			gameState = "errored";
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
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	protected void assignBaseToTeam(Base base){
		for(Team team : teams){
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
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	protected void assignFlagToTeam(String serverString){
		String[] parts = serverString.split("\\s+");
		for(Team team : teams){
			if(team.getColor().equals(parts[1])){
				team.getFlag().setPossessingTeamColor(parts[2]);
				team.getFlag().getPoint().setX(Float.valueOf(parts[3]));
				team.getFlag().getPoint().setY(Float.valueOf(parts[4]));
			}
		}
	}

	public void requestMyTanks(){
		try{
			bufferedWriter.write("mytanks\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void requestShots(){
		try{
			bufferedWriter.write("shots\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void requestOtherTanks(){
		try{
			bufferedWriter.write("othertanks\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void requestFlags(){
		try{
			bufferedWriter.write("flags\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void requestTime(){
		try{
			bufferedWriter.write("timer\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void requestEndGame(){
		try{
			bufferedWriter.write("endgame\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void updateMyTeam(){
		for(Tank tank : team.getTanks()){
			if(tank instanceof Agent){
				writeSpeed(tank.getId(), ((Agent) tank).getDesiredSpeed());
				writeAngVel(tank.getId(), ((Agent) tank).getDesiredAngularVelocity());
				if(((Agent) tank).getDesiredTriggerStatus())
					writeShoot(tank.getId());
			}
		}
	}

	public void writeTaunt(String taunt){
		try{
			bufferedWriter.write("taunt " + taunt + "\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void writeSpeed(int tankIndex, float speed){
		if(Float.isInfinite(speed))
			return;
		if(Float.isNaN(speed))
			return;
		try{
			bufferedWriter.write("speed " + tankIndex + " " + speed + "\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void writeAngVel(int tankIndex, float angvel){
		if(Float.isInfinite(angvel))
			return;
		if(Float.isNaN(angvel))
			return;
		try{
			bufferedWriter.write("angvel " + tankIndex + " " + angvel + "\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void writeShoot(int tankIndex){
		try{
			bufferedWriter.write("shoot " + tankIndex + "\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public void writeQuit(){
		try{
			bufferedWriter.write("quit\n");
			bufferedWriter.flush();
		}catch(IOException e){
			gameState = "errored";
			throw new RuntimeException(e);
		}
	}

	public transient Runnable readThread = new Runnable(){
		@Override
		public void run(){
			try{
				while(gameState == "playing"){
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

				bufferedReader.close();
			}catch(Exception e){
				gameState = "errored";
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
					timeElapsed = Float.valueOf(parts[1]);
					timeLimit = Float.valueOf(parts[2]);
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

		public void readFlag(String teamColor, String possessingTeamColor, Float x, Float y){
			for(Team team : teams){
				if(team.getColor().equals(teamColor)){
					team.getFlag().setPossessingTeamColor(possessingTeamColor);
					team.getFlag().getPoint().setX(Float.valueOf(x));
					team.getFlag().getPoint().setY(Float.valueOf(y));
				}

			}
		}

		public void readScore(String teamColor, String otherTeamColor, Integer score){
			for(Team team : teams){
				if(team.getColor().equals(teamColor)){
					team.setScore(otherTeamColor, score);
				}
			}
		}

		public void readShot(Float x, Float y, Float vx, Float vy){
			Shot shot = new Shot();
			shot.setPoint(new Point(x, y));
			shot.setVelocity(new Point(vx, vy));

			//TODO Notify tanks of this shot, or maybe notify team
			//Someone will need to process the shots and determine if any of them are a risk to tank/team
			//I could keep track of shot from frame to frame based on it's trajectory or something, however this seems like a waste

			//TODO The reaction to shots should be reflexive, meaning that it should be fast and not blocked by other 'thoughts', 'actions'
		}

		public void readMyTank(Integer index, String callsign, String status, Integer shotsAvailable, Float timeToReload, String flag, Float x, Float y, Float angle, Float vx, Float vy, Float anglevel){
			Tank tank = team.getTanks().get(index);
			tank.update(status, shotsAvailable, timeToReload, flag, x, y, vx, vy, angle, anglevel);
		}

		public void readOtherTank(String callsign, String color, String status, String flag, Float x, Float y, Float angle){
			for(Team team : teams){
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
					teamColor = value;
					break;
				case "worldsize":
					worldSize = Integer.valueOf(value);
					break;
				case "tankangvel":
					tankAngVel = Float.valueOf(value);
					break;
				case "tanklength":
					tankLength = Float.valueOf(value);
					break;
				case "tankwidth":
					tankWidth = Float.valueOf(value);
					break;
				case "tankradius":
					tankRadius = Float.valueOf(value);
					break;
				case "tankspeed":
					tankSpeed = Float.valueOf(value);
					break;
				case "tankalive":
					tankAlive = value;
					break;
				case "tankdead":
					tankDead = value;
					break;
				case "linearaccel":
					linearAccel = Float.valueOf(value);
					break;
				case "angularaccel":
					angularAccel = Float.valueOf(value);
					break;
				case "shotradius":
					shotRadius = Float.valueOf(value);
					break;
				case "shotrange":
					shotRange = Float.valueOf(value);
					break;
				case "shotspeed":
					shotSpeed = Float.valueOf(value);
					break;
				case "flagradius":
					flagRadius = Float.valueOf(value);
					break;
				case "explodetime":
					explodeTime = Float.valueOf(value);
					break;
				case "truepositive":
					truePositive = Float.valueOf(value);
					break;
				case "truenegative":
					trueNegative = Float.valueOf(value);
					break;
				default:
					throw new RuntimeException("Invalid Constant Value");
			}
		}
	};

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getGameState(){
		return gameState;
	}

	public String getVersion(){
		return version;
	}

	public String getTeamColor(){
		return teamColor;
	}

	public Integer getWorldSize(){
		return worldSize;
	}

	public Float getTankAngVel(){
		return tankAngVel;
	}

	public Float getTankLength(){
		return tankLength;
	}

	public Float getTankRadius(){
		return tankRadius;
	}

	public Float getTankSpeed(){
		return tankSpeed;
	}

	public String getTankAlive(){
		return tankAlive;
	}

	public String getTankDead(){
		return tankDead;
	}

	public Float getLinearAccel(){
		return linearAccel;
	}

	public Float getAngularAccel(){
		return angularAccel;
	}

	public Float getTankWidth(){
		return tankWidth;
	}

	public Float getShotRadius(){
		return shotRadius;
	}

	public Float getShotRange(){
		return shotRange;
	}

	public Float getShotSpeed(){
		return shotSpeed;
	}

	public Float getFlagRadius(){
		return flagRadius;
	}

	public Float getExplodeTime(){
		return explodeTime;
	}

	public Float getTruePositive(){
		return truePositive;
	}

	public Float getTrueNegative(){
		return trueNegative;
	}

	public Float getTimeElapsed(){
		return timeElapsed;
	}

	public Float getTimeLimit(){
		return timeLimit;
	}

	public Team getTeam(){
		return team;
	}

	public Set<Team> getTeams(){
		return teams;
	}

	public List<Obstacle> getObstacles(){
		return obstacles;
	}

	public String getHost(){
		return host;
	}

	public Integer getPort(){
		return port;
	}
}
