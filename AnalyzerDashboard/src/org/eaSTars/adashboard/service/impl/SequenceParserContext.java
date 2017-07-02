package org.eaSTars.adashboard.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.eaSTars.sca.service.AssemblyParserContext;

import com.github.javaparser.ast.type.Type;

public class SequenceParserContext extends AssemblyParserContext {

	private LinkedList<Map<String, Type>> declarationstack = new LinkedList<Map<String, Type>>();
	
	public SequenceParserContext() {
		pushNewDeclarationFrame();
	}
	
	public void pushNewDeclarationFrame() {
		declarationstack.push(new HashMap<String, Type>());
	}
	
	public void popDeclarationFrame() {
		declarationstack.pop();
	}
	
	public void registerDeclaration(String name, Type type) {
		declarationstack.peek().put(name, type);
	}
	
	public Type findDeclaration(String name) {
		Type result = null;
		 Iterator<Map<String, Type>> iterator = declarationstack.descendingIterator();
		 while (iterator.hasNext()) {
			 result = iterator.next().get(name);
			 if (result != null) {
				 break;
			 }
		 }
		 return result;
	}
}
