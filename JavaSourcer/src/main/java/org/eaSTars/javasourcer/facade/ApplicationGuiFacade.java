package org.eaSTars.javasourcer.facade;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Optional;

public interface ApplicationGuiFacade {

	public Optional<Point> getWindowLocation();
	
	public void setWindowLocation(Point point);
	
	public Optional<Dimension> getWindowSize();
	
	public void setWindowSize(Dimension dimension);
	
	public Optional<Integer> getDividerLocation();
	
	public void setDividerLocation(Integer dividerlocation);
	
}
