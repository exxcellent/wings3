package org.wings.plaf.css.script;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.wings.SComponent;
import org.wings.script.ScriptListener;

public class LayoutScript implements ScriptListener {

	private Map<SComponent, String> layoutCandidates;
	
	public LayoutScript( TreeMap<SComponent, String> layoutCandidates){
		this.layoutCandidates = layoutCandidates;
	}
	
	@Override
	public String getCode() {
		return null;
	}

	@Override
	public String getEvent() {
		return null;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public String getScript() {
		StringBuilder result = new StringBuilder();
		for (Entry<SComponent, String> entry  : layoutCandidates.entrySet()) {
			result.append("wingS.layout.").append(entry.getValue()).
			append("('").append(entry.getKey().getName()).append("');\n");
		}
		return result.toString();
	}

}
