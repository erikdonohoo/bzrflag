package com.murphysean.bzrflag;

import com.murphysean.bzrflag.controllers.GameController;

import java.util.*;

public class GameControllerSingleton{
	private static GameControllerSingleton instance = new GameControllerSingleton();
	Map<String, GameController> gameControllerMap;

	public static GameControllerSingleton getInstance(){
		return instance;
	}

	private GameControllerSingleton(){
		gameControllerMap = new HashMap<>();
	}

	public synchronized GameController getGameController(String key){
		return gameControllerMap.get(key);
	}

	public synchronized Set<Map.Entry<String, GameController>> getGameControllers(){
		return gameControllerMap.entrySet();
	}

	public synchronized GameController addGameController(String host, Integer port){
		String key = UUID.randomUUID().toString();
		GameController gameController = new GameController(key, host, port);
		gameControllerMap.put(key, gameController);
		return gameController;
	}

	public synchronized void removeGameController(String key){
		if(gameControllerMap.containsKey(key)){
			GameController gameController = gameControllerMap.get(key);
			gameController.getGame().endGame();
			gameControllerMap.remove(key);
		}
	}
}