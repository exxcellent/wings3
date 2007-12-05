package org.wings.portlet;

import java.util.EventListener;



/**
 * 
 * Listener for new portlet parameters
 * 
 * @author Marc Musch
 *
 */
public interface PortletParameterListener extends EventListener {
	
	/**
	 * Fired if new portlet parameters have arrived
	 * @param e - the event for the portlet parameters
	 */
	public void newPortletParameters(PortletParameterEvent e);

}
