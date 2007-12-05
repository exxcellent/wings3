package org.wings.portlet;

import java.util.EventListener;

/**
 * Listener for window state change events
 * 
 * @author Marc Musch
 *
 */
public interface WindowStateListener extends EventListener{
	
	/**
	 * Fired if the window state has changed
	 * @param e - the event for the change
	 */
	public void windowStateChanged(WindowStateEvent e);

}
