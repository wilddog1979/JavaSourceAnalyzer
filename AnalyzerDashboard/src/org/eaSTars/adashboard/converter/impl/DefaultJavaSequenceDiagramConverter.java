package org.eaSTars.adashboard.converter.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;
import org.eaSTars.adashboard.service.dto.JavaSequenceScript;

public class DefaultJavaSequenceDiagramConverter extends AbstractJavaSequenceDiagramConverter<JavaSequenceDiagramView> {

	private static final Logger LOGGER = LogManager.getLogger(DefaultJavaSequenceDiagramConverter.class);
	
	@Override
	public JavaSequenceDiagramView convert(JavaSequenceScript source) {
		JavaSequenceDiagramView target = new JavaSequenceDiagramView();
		
		ByteArrayOutputStream bo = getOutputStream(source);
		if (bo != null) {
			try {
				BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bo.toByteArray()));
				target.setContentImage(bi);
			} catch (IOException e) {
				LOGGER.error("Error while writing PNG image to the view", e);
			}
		}
		
		return target;
	}

}
