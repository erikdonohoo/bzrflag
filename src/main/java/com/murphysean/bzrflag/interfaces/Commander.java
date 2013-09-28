package com.murphysean.bzrflag.interfaces;

import java.awt.image.BufferedImage;

public interface Commander{
	public void updateOccGrid(int x, int y, int reading);
	public BufferedImage getImage();
}
