package com.murphysean.bzrflag.singletons;

import com.murphysean.bzrflag.controllers.GameController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
		GameController gameController = new GameController(key,host,port);
		gameControllerMap.put(key,gameController);
		return gameController;
	}

	public synchronized void removeGameController(String key){
		if(gameControllerMap.containsKey(key)){
			GameController gameController = gameControllerMap.get(key);
			try{
				gameController.close();
			}catch(Exception e){
				throw new RuntimeException(e);
			}
			gameControllerMap.remove(key);
		}
	}
}