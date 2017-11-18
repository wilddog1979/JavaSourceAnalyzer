package org.eaSTars.adashboard.converter.impl;

import java.io.ByteArrayOutputStream;

import org.eaSTars.adashboard.service.dto.JavaSequenceScript;

public class DefaultJavaSequenceOutputConverter extends AbstractJavaSequenceDiagramConverter<ByteArrayOutputStream> {

	@Override
	public ByteArrayOutputStream convert(JavaSequenceScript source) {
		return getOutputStream(source);
	}

}
