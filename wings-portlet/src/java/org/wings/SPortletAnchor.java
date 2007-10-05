package org.wings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SAnchor;
import org.wings.event.SRenderEvent;
import org.wings.event.SRenderListener;
import org.wings.portlet.Const;
import org.wings.portlet.PortletParameterCodec;
import org.wings.session.Session;
import org.wings.session.SessionManager;

/**
 * 
 * This implementation of a SAnchor exists for the wingSPortletBridge. With the
 * SPortletAnchor a link equivalent to a javax.portlet.PortletURL can be
 * created. This link behaves like a SAnchor. Additionally a PortletMode and a
 * WindowState can be associated with the generated PortletURL. The String
 * "kind" in the constructors defines if an action or a render PortletURL should
 * be created. <br>
 * <br>
 * Also parametes can be asscociated with the PortletURL. This parameters are
 * automatically encoded with the {@link PortletParameterCodec}
 * 
 * @see javax.portlet.PortletURL
 * 
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 * 
 */
public class SPortletAnchor extends SAnchor {

	private final transient static Log log = LogFactory
			.getLog(SPortletAnchor.class);

	/**
	 * String used for the constructor parameter "kind" to generate an action
	 * PortletURL.
	 */
	public static final String ACTION_URL = "actionURL";

	/**
	 * String used for the constructor parameter "kind" to generate a render
	 * PortletURL.
	 */
	public static final String RENDER_URL = "renderURL";

	/**
	 * String used for the parameter "portletMode" to set the
	 * PortletMode view in the PortletURL
	 */
	public static final String VIEW_MODE = "view";

	/**
	 * String used for the parameter "portletMode" to set the
	 * PortletMode edit in the PortletURL
	 */
	public static final String EDIT_MODE = "edit";

	/**
	 * String used for the parameter "portletMode" to set the
	 * PortletMode help in the PortletURL
	 */
	public static final String HELP_MODE = "help";

	/**
	 * String used for the parameter "windowState" to set the
	 * WindowState maximized in the PortletURL
	 */
	public static final String MAXIMIZED_WS = "maximized";

	/**
	 * String used for the parameter "windowState" to set the
	 * WindowState minimized in the PortletURL
	 */
	public static final String MINIMIZED_WS = "minimized";

	/**
	 * String used for the parameter "windowState" to set the
	 * WindowState normal in the PortletURL
	 */
	public static final String NORMAL_WS = "normal";

	private PortletURL portletURL;

	private String kind;

	private String portletMode;

	private String windowState;

	private Map<String, String[]> parameters;

	/**
	 * Creates a SPortletAnchor
	 * 
	 * @param kind -
	 *            action or render PortletURL, use the static Strings ACTION_URL
	 *            or RENDER_URL for this parameter.
	 */
	public SPortletAnchor(String kind) {

		this(kind, null);
	}

	/**
	 * 
	 * Creates a SPortletAnchor
	 * 
	 * @param kind -
	 *            action or render PortletURL, use the static Strings ACTION_URL
	 *            or RENDER_URL for this parameter.
	 * @param portletMode -
	 *            sets the Portlet Mode in the PortletURL, use the static
	 *            Strings VIEW_MODE, EDIT_MODE or HELP_MODE for this parameter.
	 *            Also a custom Portlet Mode can be set. If the Portlet Mode is
	 *            not supported, no Portlet Mode is set.
	 */
	public SPortletAnchor(String kind, String portletMode) {

		this(kind, portletMode, null);

	}

	/**
	 * 
	 * Creates a SPortletAnchor
	 * 
	 * @param kind -
	 *            action or render PortletURL, use the static Strings ACTION_URL
	 *            or RENDER_URL for this parameter.
	 * @param portletMode -
	 *            sets the Portlet Mode in the PortletURL, use the static
	 *            Strings VIEW_MODE, EDIT_MODE or HELP_MODE for this parameter.
	 *            Also a custom Portlet Mode can be set. If the Portlet Mode is
	 *            not supported, no Portlet Mode is set.
	 * @param windowState -
	 *            sets the Window State in the PortletURL, use the static
	 *            Strings MAXIMIZED_WS, MINIMIZED_WS or NORMAL_WS for this
	 *            parameter. Also custom Window States can be set. If the Window
	 *            State is not supported, no Window State is set.
	 */
	public SPortletAnchor(String kind, String portletMode, String windowState) {

		this(kind, portletMode, windowState, null);

	}

	/**
	 * 
	 * Creates a SPortletAnchor
	 * 
	 * @param kind -
	 *            action or render PortletURL, use the static Strings ACTION_URL
	 *            or RENDER_URL for this parameter.
	 * @param portletMode -
	 *            sets the Portlet Mode in the PortletURL, use the static
	 *            Strings VIEW_MODE, EDIT_MODE or HELP_MODE for this parameter.
	 *            Also a custom Portlet Mode can be set. If the Portlet Mode is
	 *            not supported, no Portlet Mode is set.
	 * @param windowState -
	 *            sets the Window State in the PortletURL, use the static
	 *            Strings MAXIMIZED_WS, MINIMIZED_WS or NORMAL_WS for this
	 *            parameter. Also custom Window States can be set. If the Window
	 *            State is not supported, no Window State is set.
	 * @param parameters -
	 *            set a map with parameters in the PortletURL. This parameters
	 *            will be automatically encoded with the
	 *            {@link PortletParameterCodec}
	 */
	public SPortletAnchor(String kind, String portletMode, String windowState,
			Map<String, String[]> parameters) {

		this.kind = kind;
		this.portletMode = portletMode;
		this.windowState = windowState;
		if (parameters != null) {
			this.parameters = new HashMap<String, String[]>(parameters);
		} else {
			this.parameters = new HashMap<String, String[]>();
		}
		this.setStyle("SAnchor");

		this.addRenderListener(new SRenderListener() {

			public void startRendering(SRenderEvent renderEvent) {

				updatePortletAnchor();
			}

			public void doneRendering(SRenderEvent renderEvent) {
			}

		});

	}

	/**
	 * 
	 * Sets the Portlet Mode in the PortletURL
	 * 
	 * @param portletMode -
	 *            sets the Portlet Mode in the PortletURL, use the static
	 *            Strings VIEW_MODE, EDIT_MODE or HELP_MODE for this parameter.
	 *            Also a custom Portlet Mode can be set. If the Portlet Mode is
	 *            not supported, no Portlet Mode is set.
	 */
	public void setPortletMode(String portletMode) {
		this.portletMode = portletMode;
		updatePortletAnchor();
	}

	/**
	 * Sets the Window State in the PortletURL
	 * 
	 * @param windowState -
	 *            sets the Window State in the PortletURL, use the static
	 *            Strings MAXIMIZED_WS, MINIMIZED_WS or NORMAL_WS for this
	 *            parameter. Also custom Window States can be set. If the Window
	 *            State is not supported, no Window State is set.
	 */
	public void setWindowState(String windowState) {
		this.windowState = windowState;
		updatePortletAnchor();
	}

	/**
	 * 
	 * Adds a single parameter with a single value.
	 * 
	 * @param name -
	 *            the name of the parameter, the name will be automatically
	 *            encoded with the {@link PortletParameterCodec}
	 * @param value -
	 *            the value of the parameter
	 */
	public void setParameter(String name, String value) {
		String[] valueArray = new String[] { value };
		parameters.put(name, valueArray);
		updatePortletAnchor();
	}

	/**
	 * 
	 * Adds a single parameter with an array of values.
	 * 
	 * @param name -
	 *            the name of the parameter, the name will be automatically
	 *            encoded with the {@link PortletParameterCodec}
	 * @param values -
	 *            the values of the parameter
	 */
	public void setParameters(String name, String[] values) {
		parameters.put(name, values);
		updatePortletAnchor();
	}

	/**
	 * 
	 * Sets the parameters, this method will delete all previous parameters.
	 * 
	 * @param parameters -
	 *            the parameter map, the names will be automatically encoded
	 *            with the {@link PortletParameterCodec}
	 */
	public void setParameterMap(Map<String, String[]> parameters) {
		this.parameters = new HashMap<String, String[]>(parameters);
		updatePortletAnchor();
	}

	private void updatePortletAnchor() {

		Session session = SessionManager.getSession();
		RenderResponse renderResponse = (RenderResponse) session
				.getProperty(Const.WINGS_SESSION_PROPERTY_RENDER_RESPONSE);
		if (kind != null && kind.equals(ACTION_URL)) {
			portletURL = renderResponse.createActionURL();
		} else {
			portletURL = renderResponse.createRenderURL();
		}
		if (portletMode != null && !portletMode.equals("")) {
			try {
				portletURL.setPortletMode(new PortletMode(portletMode));
			} catch (PortletModeException e) {
				// create a portletURL without a mode set, so the application
				// doesn't crash, but log a warning.
				log.warn("WingS-Portlet-Bridge: portlet mode \"" + portletMode
						+ "\" not supported");
			}
		}

		if (windowState != null && !windowState.equals("")) {
			try {
				portletURL.setWindowState(new WindowState(windowState));
			} catch (WindowStateException e) {
				// create a portletURL without a window state set, so the
				// application doesn't crash, but log a warning.
				log.warn("WingS-Portlet-Bridge: portlet window state \""
						+ windowState + "\" not supported");
			}
		}

		if (parameters != null && !parameters.isEmpty()) {
			Map<String, String[]> encodedParams = new HashMap<String, String[]>();
			Set params = parameters.keySet();
			Iterator iter = params.iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				String[] value = (String[]) parameters.get(name);
				encodedParams.put(PortletParameterCodec.encode(name), value);
			}
			portletURL.setParameters(encodedParams);
		}

		setURL(portletURL.toString());
		setTarget(null);

	}

}
