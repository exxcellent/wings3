package org.wings.comet;

import org.wings.script.ScriptListener;

/**
 * JavaScripts which advise the browser to send hangingGet, periodicPolling or no more such requests
 */
public class CometScript implements ScriptListener {
	
	static final String COMET_CONNECT = "connect";
    static final String COMET_DISCONNECT = "disconnect";
    static final String COMET_SWITCH_TO_HANGING = "switchToHanging";
    
    private String task;

	public CometScript(String task) {
		if (task == null
				|| (!task.equals(COMET_CONNECT) && !task.equals(COMET_DISCONNECT) && !task
						.equals(COMET_SWITCH_TO_HANGING)))
			throw new IllegalArgumentException();
		this.task = task;
	}

	public String getCode() {
		return null;
	}

	public String getEvent() {
		return null;
	}

	public int getPriority() {
		return ScriptListener.DEFAULT_PRIORITY;
	}

	public String getScript() {
		return "wingS.comet." + task + "();";
	}


}
