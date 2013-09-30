package com.murphysean.bzrflag.interfaces;

import com.murphysean.bzrflag.events.OccGridEvent;

import java.awt.image.BufferedImage;

public interface Commander{
	public void sendOccGridEvent(OccGridEvent event);
	public void updateOccGrid(int x, int y, int reading);
	public BufferedImage getImage();
}
