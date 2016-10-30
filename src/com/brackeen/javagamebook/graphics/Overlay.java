package com.brackeen.javagamebook.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

public interface Overlay {
	 /**
	 Updates this overlay with the specified amount of
	 elapsed time since the last update.
	 */
	 public void update(long elapsedTime);
	 /**
	 Draws an overlay onto a frame. The ViewWindow specifies
	 the bounds of the view window (usually, the entire
	 screen). The screen bounds can be retrieved by calling
	 g.getDeviceConfiguration().getBounds();
	 */
	 //public void draw(Graphics2D g, ViewWindow viewWindow);
	 /**
	 Returns true if this overlay is enabled (should be drawn).
	 */
	 public boolean isEnabled();
	}
