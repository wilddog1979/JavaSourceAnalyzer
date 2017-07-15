package org.eaSTars.adashboard.service.dto;

public class JavaSequenceScript {

	private StringBuffer content = new StringBuffer();
	
	public void appendContent(String cnt) {
		content.append(cnt);
	}
	
	public String buildString() {
		return String.format("@startuml\n%s@enduml\n", content.toString());
	}
}
