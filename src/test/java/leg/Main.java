package leg;

import com.murphysean.bzrflag.agents.AbstractAgent;
import com.murphysean.bzrflag.agents.DumbAgent;
import com.murphysean.bzrflag.agents.PFAgent;
import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.*;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class Main{

    public static void main(String[] args){
		try(Game bzrServer = new Game(UUID.randomUUID().toString(), "localhost", 58928)){
			initializeMyTeam(bzrServer, "pfagent");

			long its = 1;
			Date startTime = new Date();
			while(bzrServer.getGameState().equals("playing")){
				//Read MyTanks
				bzrServer.requestMyTanks();
				//Read OccGrids
				//Read Shots
				bzrServer.requestShots();
				//Allow PID Controllers to start processing tank movements (Possibly allow pid controllers to submit movement instructions async)
				//Read Flags
				if(its % 15 == 0)
					bzrServer.requestFlags();
				//Read Other Tanks
				if(its % 5 == 0)
					bzrServer.requestOtherTanks();

				bzrServer.requestTime();
				//AI Processing

				//Write Tank Movements, Shots (Could be done async, see above)
				bzrServer.updateMyTeam();

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

	public static void initializeMyTeam(Game server, String type){
		server.getTeam().getTanks().clear();
		for(int i = 0; i < server.getTeam().getPlayerCount(); i++){
			AbstractAgent agent = null;
			switch(type){
				case "dumbagent":
					agent = new DumbAgent();
					break;
				case "pfagent":
					PFAgent pfAgent = new PFAgent();
					Iterator<Team> iterator = server.getTeams().iterator();
					while(iterator.hasNext()){
						Team team = iterator.next();
						if(team.getColor().equals(server.getTeam().getColor())){
							pfAgent.setMyTeam(team);
							continue;
						}
						pfAgent.addOtherTeam(team);
					}
					for(Obstacle obstacle : server.getObstacles()){
						pfAgent.addObstacle(obstacle);
					}
					agent = pfAgent;
					break;
				default:
					throw new RuntimeException("Invalid Agent Type");

			}
			agent.setId(i);
			agent.setCallsign(server.getTeam().getColor() + i);
			agent.setTeamColor(server.getTeam().getColor());
			server.getTeam().getTanks().add(agent);
		}
	}
}
