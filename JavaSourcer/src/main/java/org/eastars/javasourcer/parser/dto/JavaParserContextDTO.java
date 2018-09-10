package org.eastars.javasourcer.parser.dto;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

public class JavaParserContextDTO {

	private JavaParserFacade javaParserFacade;

	/**
	 * @return the javaParserFacade
	 */
	public JavaParserFacade getJavaParserFacade() {
		return javaParserFacade;
	}

	/**
	 * @param javaParserFacade the javaParserFacade to set
	 */
	public void setJavaParserFacade(JavaParserFacade javaParserFacade) {
		this.javaParserFacade = javaParserFacade;
	}
	
}
