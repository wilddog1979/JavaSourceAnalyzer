package org.eaSTars.javasourcer.facade.impl;

import static org.eaSTars.javasourcer.configuration.ApplicationResources.Config.*;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Locale;
import java.util.Optional;

import org.eaSTars.javasourcer.facade.ApplicationGuiFacade;
import org.eaSTars.javasourcer.service.ConfiguarionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultApplicationGuiFacade implements ApplicationGuiFacade {
	
	@Autowired
	private ConfiguarionService configurationService;
	
	@Override
	public Optional<Point> getWindowLocation() {
		Integer x = configurationService.getIntegerValue(GUI_X);
		Integer y = configurationService.getIntegerValue(GUI_Y);
		
		if (x != null && y != null) {
			return Optional.of(new Point(x, y));
		}
		
		return Optional.empty();
	}

	@Override
	public void setWindowLocation(Point point) {
		configurationService.setProperty(GUI_X, Integer.toString(point.x));
		configurationService.setProperty(GUI_Y, Integer.toString(point.y));
	}

	@Override
	public Optional<Dimension> getWindowSize() {
		Integer width = configurationService.getIntegerValue(GUI_WIDTH);
		Integer height = configurationService.getIntegerValue(GUI_HEIGHT);
		
		if (width != null && height != null) {
			Optional.of(new Dimension(width, height));
		}
		
		return Optional.empty();
	}

	@Override
	public void setWindowSize(Dimension dimension) {
		configurationService.setProperty(GUI_WIDTH, Integer.toString(dimension.width));
		configurationService.setProperty(GUI_HEIGHT, Integer.toString(dimension.height));
	}

	@Override
	public Optional<Integer> getDividerLocation() {
		return Optional.ofNullable(configurationService.getIntegerValue(GUI_DIVIDER));
	}

	@Override
	public void setDividerLocation(Integer dividerlocation) {
		configurationService.setProperty(GUI_DIVIDER, dividerlocation.toString());
	}
	
	@Override
	public Locale getLocale() {
		String[] localeinfo = configurationService.getValue(GUI_LANGUAGE, DEFAULT_LANGUAGE).split("_");

		if (localeinfo.length == 1) {
			return new Locale(localeinfo[0]);
		} else if (localeinfo.length > 1) {
			return new Locale(localeinfo[0], localeinfo[1]);
		}
		
		return null;
	}

}
