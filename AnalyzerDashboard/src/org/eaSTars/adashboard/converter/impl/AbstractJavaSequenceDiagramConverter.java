package org.eaSTars.adashboard.converter.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.adashboard.service.dto.JavaSequenceScript;
import org.springframework.core.convert.converter.Converter;

import net.sourceforge.plantuml.BlockUmlBuilder;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.preproc.Defines;

public abstract class AbstractJavaSequenceDiagramConverter<TARGET> implements Converter<JavaSequenceScript, TARGET> {

	private static final Logger LOGGER = LogManager.getLogger(AbstractJavaSequenceDiagramConverter.class);
	
	private static final String HEADLESS_KEY = "java.awt.headless";
	
	private static final String PLANTUML_IMAGE_LIMIT_KEY = "PLANTUML_LIMIT_SIZE";
	
	protected ByteArrayOutputStream getOutputStream(JavaSequenceScript source) {
		String headlessOriginal = System.getProperty(HEADLESS_KEY);
		String plantUMLLimitSize = System.getProperty(PLANTUML_IMAGE_LIMIT_KEY);
		System.setProperty(HEADLESS_KEY, "true");
		System.setProperty(PLANTUML_IMAGE_LIMIT_KEY, "32768");

		Reader reader = new InputStreamReader(new ByteArrayInputStream(source.buildString().getBytes()));

		try {
			BlockUmlBuilder builder = new BlockUmlBuilder(new ArrayList<String>(), null, Defines.createEmpty(), reader);

			return builder.getBlockUmls().stream()
					.findFirst()
					.map(blockUml -> {
						Diagram system = null;
						try {
							system = blockUml.getDiagram();

							ByteArrayOutputStream bo = new ByteArrayOutputStream();
							system.exportDiagram(bo, 0, new FileFormatOption(FileFormat.PNG));
							return bo;
						} catch (Throwable t) {
						}
						return null;
					}).orElseGet(() -> null);
		} catch (IOException e) {
			LOGGER.error("Error while converting PlantUML script to PNG", e);
			return null;
		} finally {
			System.setProperty(HEADLESS_KEY, headlessOriginal == null ? "false" : headlessOriginal);
			System.setProperty(PLANTUML_IMAGE_LIMIT_KEY, plantUMLLimitSize == null ? "4096" : plantUMLLimitSize);
		}
	}
}
