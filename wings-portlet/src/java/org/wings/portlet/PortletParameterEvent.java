package org.wings.portlet;

import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Event for portlet parameters, from this event you can get all the parameters
 * from a PortletURL or a SPortletAnchor, see {@link PortletParameterProvider}
 * 
 * @author Marc Musch
 * 
 */
public class PortletParameterEvent extends EventObject {

	private Map<String, String[]> portletParameters;

	/**
	 * @param source -
	 *            the source of the event
	 * @param windowState -
	 *            the new window state
	 */
	public PortletParameterEvent(Object source,
			Map<String, String[]> portletParameters) {
		super(source);
		this.portletParameters = portletParameters;

	}

	/**
	 * {@link javax.portlet.PortletRequest#getParameter(String)}
	 * 
	 * @param name -
	 *            a String specifying the name of the parameter
	 * @return a String representing the single value of the parameter
	 * @throws java.lang.IllegalArgumentException -
	 *             if name is null.
	 * @see javax.portlet.PortletRequest#getParameter(String)
	 */
	public String getParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"parameter name must not be null");
		}
		if (portletParameters != null && portletParameters.containsKey(name)) {
			return portletParameters.get(name)[0];
		}
		return null;
	}

	/**
	 * {@link javax.portlet.PortletRequest#getParameterValues(String)}
	 * 
	 * @param name -
	 *            a String containing the name of the parameter the value of
	 *            which is requested
	 * @return an array of String objects containing the parameter values.
	 * @throws java.lang.IllegalArgumentException -
	 *             if name is null.
	 * @see javax.portlet.PortletRequest#getParameterValues(String)
	 */
	public String[] getParameterValues(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"parameter name must not be null");
		}
		if (portletParameters != null && portletParameters.containsKey(name)) {
			String[] values = portletParameters.get(name);
			return values;
		}
		return null;
	}

	/**
	 * 
	 * {@link javax.portlet.PortletRequest#getParameterNames()}
	 * 
	 * @return a Iterator of String objects, each String containing the name of
	 *         a request parameter; or an empty Iterator if the request has no
	 *         parameters.
	 * @see javax.portlet.PortletRequest#getParameterNames()
	 */
	public Iterator<String> getParameterNames() {
		if (portletParameters == null) {
			return new HashSet<String>().iterator();
		}
		Set<String> names = portletParameters.keySet();
		return names.iterator();

	}

	/**
	 * 
	 * {@link javax.portlet.PortletRequest#getParameterMap()}
	 * 
	 * @return an immutable Map containing parameter names as keys and parameter
	 *         values as map values, or an empty Map if no parameters exist. The
	 *         keys in the parameter map are of type String. The values in the
	 *         parameter map are of type String array (String[]).
	 * @see javax.portlet.PortletRequest#getParameterMap()
	 * 
	 */
	public Map<String, String[]> getParameterMap() {
		return this.portletParameters;
	}

}
