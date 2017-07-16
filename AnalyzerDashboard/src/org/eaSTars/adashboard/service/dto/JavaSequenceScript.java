package org.eaSTars.adashboard.service.dto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eaSTars.sca.model.JavaAssemblyModel;

public class JavaSequenceScript {
	
	private static final List<String> NAMELIST = Arrays.asList("Controller", "Facade", "Service", "Util","Dao");
	
	private class AliasEntry {
		private int count;

		private String alias;

		private JavaAssemblyModel javaAssembly;
	}

	private Stack<Integer> methodCallStack = new Stack<Integer>();
	
	private boolean sorted = false;
	
	private int uniquecounter = 0;

	private List<JavaAssemblyModel> participant = new Vector<JavaAssemblyModel>();

	private Map<String, AliasEntry> aliasmap = new HashMap<String, AliasEntry>();

	private StringBuffer content = new StringBuffer();

	public String addParticipant(String fullname, JavaAssemblyModel assemblyModel) {
		AliasEntry aliansentry = aliasmap.get(fullname);

		if (aliansentry == null) {
			aliansentry = new AliasEntry();
			aliansentry.count = uniquecounter;
			aliansentry.alias = "seqalias" + uniquecounter++;
			aliansentry.javaAssembly = assemblyModel;

			aliasmap.put(fullname, aliansentry);
			participant.add(assemblyModel);
		}

		return aliansentry.alias;
	}

	public void appendContent(String cnt) {
		content.append(cnt);
	}

	public String buildString() {
		return String.format("@startuml\n%s%s@enduml\n",
				getSortedParticipants().collect(Collectors.joining())
				, content.toString());
	}

	private Stream<String> getSortedParticipants() {
		return aliasmap.entrySet().stream()
				.sorted((o1, o2) -> compareAliasEntries(o2.getValue(), o1.getValue()))
				.map(a -> String.format("participant %s as %s\n", a.getKey(), a.getValue().alias));
	}

	private int compareAliasEntries(AliasEntry entry1, AliasEntry entry2) {
		int result = 0;
		if (sorted) {
			result = Integer.compare(entry1.javaAssembly.getJavaModuleID(), entry2.javaAssembly.getJavaModuleID());
			if (result == 0) {
				int idx1 = getNameIndex(entry1.javaAssembly.getName());
				int idx2 = getNameIndex(entry2.javaAssembly.getName());
				result = Integer.compare(idx2, idx1);
			}
		}
		if (result == 0) {
			result = Integer.compare(entry2.count, entry1.count);
		}
		return result;
	}
	
	private int getNameIndex(String name) {
		int result = -1;
		for (int i = 0; i < NAMELIST.size(); ++i) {
			if (name.endsWith(NAMELIST.get(i))) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	public boolean pushToCallStack(Integer methodPK) {
		boolean result = methodCallStack.contains(methodPK);
		
		if (!result) {
			methodCallStack.push(methodPK);
		}
		
		return result;
	}

	public int popFromCallStack() {
		return methodCallStack.pop();
	}
	
	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}
}
