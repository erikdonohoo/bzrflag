package com.murphysean.bzrflag.commanders;

import com.murphysean.bzrflag.events.BZRFlagEvent;
import com.murphysean.bzrflag.events.OccGridEvent;
import com.murphysean.bzrflag.models.Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class OccGridCommander extends AbstractCommander{
	protected List<List<Float>> occGrid;

	public OccGridCommander(Game game){
		super(game);
		occGrid = new ArrayList<>();
		for(int i = 0; i < game.getWorldSize(); i++){
			occGrid.add(new ArrayList<Float>());
			for(int j = 0; j < game.getWorldSize(); j++){
				occGrid.get(i).add(0f);
			}
		}
	}

	public void sendOccGridEvent(OccGridEvent event){
		//Handle event immediate, overriding classes should probably throw this into a message queue to prevent heavy processing/blocking on the communication thread
		for(int i = 0; i < event.getLine().length(); i++){
			updateOccGrid(Math.round(event.getPosition().getX()),Math.round(event.getPosition().getY() + i),Integer.valueOf(event.getLine().charAt(i)));
		}
	}

	public void updateOccGrid(int x, int y, int reading){
		occGrid.get(x - (game.getWorldSize() / 2)).set(y - (game.getWorldSize() / 2),(float)reading);
	}

	public BufferedImage getImage(){
		BufferedImage bufferedImage = new BufferedImage(game.getWorldSize(),game.getWorldSize(),BufferedImage.TYPE_BYTE_GRAY);
		for(int i = 0; i < game.getWorldSize(); i++){
			for(int j = 0; j < game.getWorldSize(); j++){
				//TODO Scale probability into a single byte for image
				int gs = Math.round(occGrid.get(i).get(j));
				bufferedImage.setRGB(i,j,gs);
			}
		}

		return bufferedImage;
	}

	@Override
	public boolean isOccGridRequired(){
		return true;
	}

	@Override
	public void sendBZRFlagEvent(BZRFlagEvent event){
		if(event instanceof OccGridEvent)
			sendOccGridEvent((OccGridEvent)event);
	}
}