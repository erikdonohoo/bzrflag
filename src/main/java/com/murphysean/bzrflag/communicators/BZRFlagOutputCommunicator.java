package com.murphysean.bzrflag.communicators;

import com.murphysean.bzrflag.controllers.GameController;
import com.murphysean.bzrflag.interfaces.Agent;
import com.murphysean.bzrflag.models.Game;
import com.murphysean.bzrflag.models.Tank;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BZRFlagOutputCommunicator implements Runnable{

	private Game game;
	private GameController gameController;
	private BufferedWriter bufferedWriter;

	private BlockingQueue<String> blockingQueue;

	public BZRFlagOutputCommunicator(GameController gameController){
		this.gameController = gameController;
		bufferedWriter = gameController.getBufferedWriter();
		this.game = gameController.getGame();

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Override
	public void run(){
		while(game.getState().equals("playing")){
			try{
				String command = blockingQueue.take();
				bufferedWriter.write(command + "\n");
				bufferedWriter.flush();
			}catch(InterruptedException e){
				game.setState("interrupted");
			}catch(IOException e){
				game.setState("errored");
			}
		}
	}

	public void requestMyTanks(){
		blockingQueue.offer("mytanks");
	}

	public void requestShots(){
		blockingQueue.offer("shots");
	}

	public void requestOtherTanks(){
		blockingQueue.offer("othertanks");
	}

	public void requestFlags(){
		blockingQueue.offer("flags");
	}

	public void requestTime(){
		blockingQueue.offer("timer");
	}

	public void requestOccGrids(){
		for(int i = 0; i < game.getTeam().getTanks().size(); i++){
			blockingQueue.offer("occgrid " + i);
		}
	}

	public void updateMyTeam(){
		for(Tank tank : game.getTeam().getTanks()){
			if(tank instanceof Agent){
				writeSpeed(tank.getId(), ((Agent) tank).getDesiredSpeed());
				writeAngVel(tank.getId(), ((Agent) tank).getDesiredAngularVelocity());
				if(((Agent) tank).getDesiredTriggerStatus())
					writeShoot(tank.getId());
			}
		}
	}

	public void writeTaunt(String taunt){
		blockingQueue.offer("taunt " + taunt);
	}

	public void writeSpeed(int tankIndex, float speed){
		if(Float.isInfinite(speed))
			return;
		if(Float.isNaN(speed))
			return;

		blockingQueue.offer("speed " + tankIndex + " " + speed);
	}

	public void writeAngVel(int tankIndex, float angvel){
		if(Float.isInfinite(angvel))
			return;
		if(Float.isNaN(angvel))
			return;

		blockingQueue.offer("angvel " + tankIndex + " " + angvel);
	}

	public void writeShoot(int tankIndex){
		blockingQueue.offer("shoot " + tankIndex);
	}

	public void writeQuit(){
		blockingQueue.offer("quit");
	}

	public void requestEndGame(){
		blockingQueue.offer("endgame");
	}
}
