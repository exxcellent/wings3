package org.wings.portlet;

import java.util.EventObject;

/**
 * Event for window state changes, includes the new window state
 * 
 * @author Marc Musch
 *
 */
public class WindowStateEvent extends EventObject {

	private String windowState;
	
	/**
	 * @param source - the source of the event
	 * @param windowState - the new window state
	 */
	public WindowStateEvent(Object source, String windowState) {
		super(source);
		this.windowState = windowState;

	}
	
	/**
	 * Returns the new window state
	 * 
	 * @return the new window state
	 */
	public String getWindowState() {
		return windowState;
	}

}
