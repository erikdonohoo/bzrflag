package com.murphysean.bzrflag;

import com.murphysean.bzrflag.agents.DumbAgent;
import com.murphysean.bzrflag.agents.PFAgent;
import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.Obstacle;
import com.murphysean.bzrflag.models.Point;
import com.murphysean.bzrflag.models.Tank;
import com.murphysean.bzrflag.models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Main{

    public static void main(String[] args) throws Exception{
		BZRServer bzrServer = new BZRServer("localhost", 39163);

		initializeMyTeam(bzrServer, "pfagent");

		int its = 0;
		Date startTime = new Date();
		while(bzrServer.getGameState().equals("playing")){
			//Read MyTanks
			bzrServer.readMyTanks();
			//Read OccGrids
			//Read Shots
			bzrServer.readShots();
			//Allow PID Controllers to start processing tank movements (Possibly allow pid controllers to submit movement instructions async)
			//Read Flags
			bzrServer.readFlags();
			//Read Other Tanks
			bzrServer.readOtherTanks();
			//AI Processing

			//Write Tank Movements, Shots (Could be done async, see above)
			bzrServer.updateMyTeam();

			its++;
			if(its % 100 == 0)
				System.out.println("FPS: " + its / ((new Date().getTime() - startTime.getTime()) / 1000l));
		}
		Date endTime = new Date();

		System.out.println("FPS: " + its / ((endTime.getTime() - startTime.getTime()) / 1000l));
    }

	public static void initializeMyTeam(BZRServer server, String type){
		server.team.getTanks().clear();
		for(int i = 0; i < server.team.getPlayerCount(); i++){
			Agent agent = null;
			switch(type){
				case "dumbagent":
					agent = new DumbAgent();
					break;
				case "pfagent":
					PFAgent.PFGenerator generator = new PFAgent.PFGenerator();
					Iterator<Team> iterator = server.teams.iterator();
					while(iterator.hasNext()){
						Team team = iterator.next();
						if(team.equals(server.team)){
							generator.setMyTeam(team);
							continue;
						}
						generator.addOtherTeam(team);
					}
					for(Obstacle obstacle : server.getObstacles()){
						generator.addObstacle(obstacle);
					}
					agent = new PFAgent(generator);
					break;
				default:
					throw new RuntimeException("Invalid Agent Type");

			}
			((Tank)agent).setId(i);
			((Tank)agent).setCallsign(server.team.getColor() + i);
			((Tank)agent).setTeamColor(server.team.getColor());
			server.team.getTanks().add((Tank) agent);
		}
	}
}
