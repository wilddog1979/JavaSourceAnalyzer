package org.eaSTars.adashboard.converter.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;
import org.springframework.core.convert.converter.Converter;

import net.sourceforge.plantuml.BlockUmlBuilder;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.preproc.Defines;

public class DefaultJavaSequenceDiagramConverter implements Converter<String, JavaSequenceDiagramView> {

	private static final String HEADLESS_KEY = "java.awt.headless";
	
	private static final String PLANTUML_IMAGE_LIMIT_KEY = "PLANTUML_LIMIT_SIZE";
	
	@Override
	public JavaSequenceDiagramView convert(String source) {
		String headlessOriginal = System.getProperty(HEADLESS_KEY);
		String plantUMLLimitSize = System.getProperty(PLANTUML_IMAGE_LIMIT_KEY);
		System.setProperty(HEADLESS_KEY, "true");
		System.setProperty(PLANTUML_IMAGE_LIMIT_KEY, "8192");
		
		JavaSequenceDiagramView target = new JavaSequenceDiagramView();
		
		try {
			Reader reader = new InputStreamReader(new ByteArrayInputStream(source.getBytes()));

			BlockUmlBuilder builder = new BlockUmlBuilder(new ArrayList<String>(), null, Defines.createEmpty(), reader);

			builder.getBlockUmls().stream()
			.forEach(blockUml -> {
				Diagram system = null;
				try {
					system = blockUml.getDiagram();

					ByteArrayOutputStream bo = new ByteArrayOutputStream();
					system.exportDiagram(bo, 0, new FileFormatOption(FileFormat.PNG));
					BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bo.toByteArray()));
					target.setContentImage(bi);
				} catch (Throwable t) {
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.setProperty(HEADLESS_KEY, headlessOriginal == null ? "false" : headlessOriginal);
		System.setProperty(PLANTUML_IMAGE_LIMIT_KEY, plantUMLLimitSize == null ? "4096" : plantUMLLimitSize);
		
		return target;
	}

}
