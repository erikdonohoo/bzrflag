package com.murphysean.bzrflag.commanders;

import com.murphysean.bzrflag.events.OccGridEvent;
import com.murphysean.bzrflag.interfaces.Commander;
import com.murphysean.bzrflag.models.Team;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommander extends Team implements Commander{
	protected int gameWidth;
	protected int gameHeight;
	protected List<List<Float>> occGrid;

	public AbstractCommander(int gameWidth, int gameHeight){
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		occGrid = new ArrayList<>();
		for(int i = 0; i < gameWidth; i++){
			occGrid.add(new ArrayList<Float>());
			for(int j = 0; j < gameHeight; j++){
				occGrid.get(i).add(0f);
			}
		}
	}

	@Override
	public void sendOccGridEvent(OccGridEvent event){
		//Handle event immediate, overriding classes should probably throw this into a message queue to prevent heavy processing/blocking on the communication thread
		for(int i = 0; i < event.getLine().length(); i++){
			updateOccGrid(Math.round(event.getPosition().getX()), Math.round(event.getPosition().getY() + i), Integer.valueOf(event.getLine().charAt(i)));
		}
	}

	@Override
	public void updateOccGrid(int x, int y, int reading){
		occGrid.get(x - (gameWidth / 2)).set(y - (gameHeight / 2), (float)reading);
	}

	@Override
	public BufferedImage getImage(){
		BufferedImage bufferedImage = new BufferedImage(gameWidth, gameHeight, BufferedImage.TYPE_BYTE_GRAY);
		for(int i = 0; i < gameWidth; i++){
			for(int j = 0; j < gameHeight; j++){
				//TODO Scale probability into a single byte for image
				int gs = Math.round(occGrid.get(i).get(j));
				bufferedImage.setRGB(i,j,gs);
			}
		}

		return bufferedImage;
	}
}