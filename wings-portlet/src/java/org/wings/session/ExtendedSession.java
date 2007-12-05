package org.wings.session;

import javax.portlet.RenderResponse;

import org.wings.portlet.Const;
import org.wings.portlet.PortletParameterProvider;
import org.wings.portlet.WindowStateProvider;

public class ExtendedSession extends Session {

	/**
	 * Creates a unique id including the portlet id.
	 */
	public String createUniqueId() {
		RenderResponse renderResponse = (RenderResponse) getProperty(Const.WINGS_SESSION_PROPERTY_RENDER_RESPONSE);
		String uid = renderResponse.getNamespace();
		uid = uid.replace("_", "");
		return uid + super.createUniqueId();
	}

	/**
	 * Fires events if the portlet window state has changed and if there is
	 * a PortletWindowStateProvider registered in the actual session
	 */
	public synchronized void fireWindowStateEvents() {
		WindowStateProvider pwsp = (WindowStateProvider) getProperty(Const.WINGS_SESSION_PROPERTY_WINDOW_STATE_PROVIDER);
		if (pwsp != null) {
			pwsp.updateWindowState();
		}
	}
	
	/**
	 * Fires events for the new portlet parameters
	 */
	public synchronized void fireNewPortletParameters() {
		PortletParameterProvider ppp = (PortletParameterProvider) getProperty(Const.WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_PROVIDER);
		if (ppp != null) {
			ppp.updateParameters();
		}
	}

}
