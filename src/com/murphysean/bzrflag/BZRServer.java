package com.murphysean.bzrflag;

import com.murphysean.bzrflag.agents.AbstractAgent;
import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BZRServer{
	//connecting, initializing, playing, quitting, finished
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
	protected List<Shot> shots;

	//Communication Objects
	protected Socket socket;
	protected BufferedReader bufferedReader;
	protected BufferedWriter bufferedWriter;

	public BZRServer(String host, int port) throws Exception{
		setGameState("connecting");
		//Open a socket
		socket = new Socket(host, port);
		//Handshake
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		teams = new HashSet<Team>();
		obstacles = new ArrayList<>();
		shots = new ArrayList<>();

		//Make one time calls to initialize the agent
		setGameState("initializing");
		handshake();
		readConstants();
		readTeams();
		readObstacles();
		readBases();
		readFlags();

		//Initialize state
		setGameState("playing");
	}

	protected void handshake() throws Exception{
		String serverMessage = bufferedReader.readLine();
		if(!serverMessage.startsWith("bzrobots"))
			throw new Exception("Invalid Server Message");

		version = serverMessage.split(" ")[1];
		bufferedWriter.write("agent " + version + "\n");
		bufferedWriter.flush();
	}

	protected void readConstants() throws Exception{
		bufferedWriter.write("constants\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
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
						throw new Exception("Invalid Constant Value");
				}
				response = bufferedReader.readLine();
			}
		}
	}

	protected void readTeams() throws Exception{
		bufferedWriter.write("teams\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
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
	}

	protected void readObstacles() throws Exception{
		bufferedWriter.write("obstacles\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		if(response.equals("begin")){
			response = bufferedReader.readLine();
			while(!response.equals("end")){
				Obstacle obstacle = new Obstacle(response);
				obstacles.add(obstacle);
				response = bufferedReader.readLine();
			}
		}
	}

	protected void readBases() throws Exception{
		bufferedWriter.write("bases\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		if(response.equals("begin")){
			response = bufferedReader.readLine();
			while(!response.equals("end")){
				Base base = new Base(response);
				assignBaseToTeam(base);
				response = bufferedReader.readLine();
			}
		}
	}

	protected void assignBaseToTeam(Base base){
		for(Team team : teams){
			if(team.getColor().equals(base.getTeamColor()))
				team.setBase(base);
		}
	}

	public void readTime() throws Exception{
		bufferedWriter.write("timer\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		String[] parts = response.split("\\s+");
		timeElapsed = Float.valueOf(parts[1]);
		timeLimit = Float.valueOf(parts[2]);
	}

	public void readFlags() throws Exception{
		bufferedWriter.write("flags\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		if(response.equals("begin")){
			response = bufferedReader.readLine();
			while(!response.equals("end")){
				assignFlagToTeam(response);
				response = bufferedReader.readLine();
			}
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

	public void readShots() throws Exception{
		shots.clear();
		bufferedWriter.write("shots\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		if(response.equals("begin")){
			response = bufferedReader.readLine();
			while(!response.equals("end")){
				Shot shot = new Shot(response);
				shots.add(shot);
				response = bufferedReader.readLine();
			}
		}
	}

	public void readMyTanks() throws Exception{
		bufferedWriter.write("mytanks\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		if(response.equals("begin")){
			response = bufferedReader.readLine();
			while(!response.equals("end")){
				updateMyTanks(response);
				response = bufferedReader.readLine();
			}
		}
	}

	protected void updateMyTanks(String serverString){
		String[] parts = serverString.split("\\s+");
		int index = Integer.valueOf(parts[1]);
		Tank tank = team.getTanks().get(index);
		tank.setStatus(parts[3]);
		tank.setShotsAvailable(Integer.valueOf(parts[4]));
		tank.setTimeToReload(Float.valueOf(parts[5]));
		tank.setFlag(parts[6]);
		tank.setPosition(Float.valueOf(parts[7]), Float.valueOf(parts[8]));
		tank.setAngle(Float.valueOf(parts[9]));
		tank.setVelocity(Float.valueOf(parts[10]), Float.valueOf(parts[11]));
		tank.setAngleVelocity(Float.valueOf(parts[12]));
		if(tank instanceof AbstractAgent){
			((AbstractAgent)tank).setPositionAngle(Float.valueOf(parts[7]), Float.valueOf(parts[8]), Float.valueOf(parts[9]));
		}
	}

	public void readOtherTanks() throws Exception{
		bufferedWriter.write("othertanks\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		response = bufferedReader.readLine();
		if(response.equals("begin")){
			response = bufferedReader.readLine();
			while(!response.equals("end")){
				updateOtherTanks(response);
				response = bufferedReader.readLine();
			}
		}
	}

	protected void updateOtherTanks(String serverString){
		String[] parts = serverString.split("\\s+");
		String color = parts[2];
		for(Team team : teams){
			if(team.getColor().equals(color)){
				int index = Integer.valueOf(parts[1].replaceAll("\\D", ""));
				Tank tank = team.getTanks().get(index);
				tank.setStatus(parts[3]);
				tank.setFlag(parts[4]);
				tank.setPosition(Float.valueOf(parts[5]), Float.valueOf(parts[6]));
				tank.setAngle(Float.valueOf(parts[7]));
				break;
			}
		}
	}

	public void readOccGrid(int tankIndex){

	}

	public void updateMyTeam() throws Exception{
		for(Tank tank : team.getTanks()){
			if(tank instanceof Agent){
				writeSpeed(tank.getId(), ((Agent) tank).getDesiredSpeed());
				writeAngVel(tank.getId(), ((Agent) tank).getDesiredAngularVelocity());
				if(((Agent) tank).getDesiredTriggerStatus())
					writeShoot(tank.getId());
			}
		}
	}

	public void writeSpeed(int tankIndex, float speed) throws Exception{
		bufferedWriter.write("speed " + tankIndex + " " + speed + "\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		//Eat up the ok
		bufferedReader.readLine();
	}

	public void writeAngVel(int tankIndex, float angvel) throws Exception{
		bufferedWriter.write("angvel " + tankIndex + " " + angvel + "\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		bufferedReader.readLine();
	}

	public void writeShoot(int tankIndex) throws Exception{
		bufferedWriter.write("shoot " + tankIndex + "\n");
		bufferedWriter.flush();

		//Eat up the ack
		String response = bufferedReader.readLine();
		if(!response.startsWith("ack"))
			throw new Exception("Missing Ack");
		bufferedReader.readLine();
	}

	public String getGameState(){
		return gameState;
	}

	protected void setGameState(String gameState){
		System.out.println("Game State: " + gameState);
		this.gameState = gameState;
	}

	public List<Obstacle> getObstacles(){
		return obstacles;
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
}
