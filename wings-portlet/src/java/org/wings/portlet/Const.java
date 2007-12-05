package org.wings.portlet;

/**
 * 
 * Constants for the WingS-Portlet-Bridge
 * 
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 */
public class Const {

	private Const() {
		
	}
	
	/*
	 * Session Attributes
	 */
	public static final String SESSION_ATTR_PARAMETERS = "org.wings.portlet.parameters";

	/*
	 * Request Attributes
	 */

	public static final String REQUEST_ATTR_PARAMETERS_FROM_ACTION_MAP = "org.wings.portlet.parameterMap";

	public static final String REQUEST_ATTR_RENDER_REQUEST = "org.wings.portlet.renderRequest";

	public static final String REQUEST_ATTR_RENDER_RESPONSE = "org.wings.portlet.renderResponse";

	public static final String REQUEST_ATTR_WINGS_SERVLET_URL = "org.wings.portlet.wingSServletURL";

	public static final String REQUEST_ATTR_WINGS_CLASS = "org.wings.portlet.actualWingSClass";

	
	/*
	 * Request Parameters
	 */
	
	public static final String REQUEST_PARAM_RESOURCE_AS_PARAM = "org.wings.portlet.resourceAsParam";
	
	/*
	 * Wings Session Properties
	 */

	public static final String WINGS_SESSION_PROPERTY_RENDER_RESPONSE = "org.wings.portlet.render.response";

	public static final String WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_MAP = "org.wings.portlet.parameter.map";

	public static final String WINGS_SESSION_PROPERTY_RENDER_REQUEST = "org.wings.portlet.render.request";
	
	public static final String WINGS_SESSION_PROPERTY_WINDOW_STATE_PROVIDER = "org.wings.portlet.window.state.provider";

	public static final String WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_PROVIDER = "org.wings.portlet.parameter.provider";
	
	/*
	 * Code/Decode String for Parameters set with the PortletURL
	 */

	public static final String WINGS_PORTLET_URL_CODE_STRING = "portparam.";
}
