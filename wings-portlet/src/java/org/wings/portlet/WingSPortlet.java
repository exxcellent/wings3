package org.wings.portlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 */
public class WingSPortlet extends GenericPortlet {

	private final transient static Log log = LogFactory
			.getLog(WingSPortlet.class);

	private static final String CONFIG_WINGS_SERVLET_URL = "WingSServletURL";

	private static final String CONFIG_VIEW_CLASS = "ViewClass";

	private static final String CONFIG_EDIT_CLASS = "EditClass";

	private static final String CONFIG_HELP_CLASS = "HelpClass";

	private String wingSServletURL;

	private String viewClass;

	private String editClass;

	private String helpClass;

	public void init(PortletConfig config) throws PortletException {

		log
				.debug("WingS-Portlet-Bridge: start init(PortletConfig) for Portlet "
						+ config.getPortletName());
		super.init(config);

		wingSServletURL = config.getInitParameter(CONFIG_WINGS_SERVLET_URL);
		viewClass = config.getInitParameter(CONFIG_VIEW_CLASS);
		editClass = config.getInitParameter(CONFIG_EDIT_CLASS);
		helpClass = config.getInitParameter(CONFIG_HELP_CLASS);

		if (viewClass == null) {
			log
					.error("WingS-Portlet-Bridge: the viewClass is null. Must be set in the portlet.xml");
			throw new PortletException(
					"WingS-Portlet-Bridge: wings-class for the view mode is null.");
		}

		if (wingSServletURL == null) {
			log
					.error("WingS-Portlet-Bridge: the wingServletURL is null. Must be set in the portlet.xml");
			throw new PortletException(
					"WingS-Portlet-Bridge: wingSServletURL is null.");
		}

		log.debug("WingS-Portlet-Bridge: using the url " + wingSServletURL
				+ " for wingSServletURL");
		log.debug("WingS-Portlet-Bridge: using the class " + viewClass
				+ " for view-mode");
		log.debug("WingS-Portlet-Bridge: using the class " + editClass
				+ " for edit-mode");
		log.debug("WingS-Portlet-Bridge: using the class " + helpClass
				+ " for help-mode");
		log.debug("WingS-Portlet-Bridge: ended init(PortletConfig)");
	}

	protected void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		log.debug("WingS-Portlet-Bridge: start doEdit() "
				+ response.getNamespace());

		processRendering(request, response);

		log.debug("WingS-Portlet-Bridge: ended doEdit() "
				+ response.getNamespace());

	}

	protected void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		log.debug("WingS-Portlet-Bridge: start doHelp() "
				+ response.getNamespace());

		processRendering(request, response);

		log.debug("WingS-Portlet-Bridge: ended doHelp() "
				+ response.getNamespace());

	}

	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		log.debug("WingS-Portlet-Bridge: start doView() "
				+ response.getNamespace());

		processRendering(request, response);

		log.debug("WingS-Portlet-Bridge: ended doView() "
				+ response.getNamespace());

	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

		log.debug("WingS-Portlet-Bridge: start with processAction()");

		// store the parameters
		PortletSession portletSession = request.getPortletSession();
		Map parameters = request.getParameterMap();
		String portletMode = request.getPortletMode().toString();
		portletSession.setAttribute(portletMode + ":"
				+ Const.SESSION_ATTR_PARAMETERS, parameters,
				PortletSession.PORTLET_SCOPE);
		log.debug("WingS-Portlet-Bridge: stored parameters in sesseion under "
				+ portletMode + ":" + Const.SESSION_ATTR_PARAMETERS);

		log.debug("WingS-Portlet-Bridge: ended processAction()");
	}

	protected void processRendering(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		// get the parameters out of the session
		// parameters are set into the session through the processAction
		// TODO: maybe get the params directly out of the session in the servlet
		PortletSession portletSession = request.getPortletSession();
		String portletMode = request.getPortletMode().toString();
		String parameterAttrName = portletMode + ":"
				+ Const.SESSION_ATTR_PARAMETERS;
		Map sessionParameters = (Map) portletSession.getAttribute(
				parameterAttrName, PortletSession.PORTLET_SCOPE);
		log.debug("WingS-Portlet-Bridge: loaded parameters in session from "
				+ portletMode + ":" + Const.SESSION_ATTR_PARAMETERS);
		portletSession.removeAttribute(parameterAttrName,
				PortletSession.PORTLET_SCOPE);

		// and get the Parameters directly from the request
		Map requestParameters = request.getParameterMap();

		// store all parameters in the request for the PortletSessionServlet
		Map allParameters = new HashMap();
		if (sessionParameters != null) {
			allParameters.putAll(sessionParameters);
		}
		if (requestParameters != null) {
			allParameters.putAll(requestParameters);
		}
		request.setAttribute(Const.REQUEST_ATTR_PARAMETERS_FROM_ACTION_MAP,
				allParameters);

		// set the request and the response for the filter and the wings
		// servlets
		request.setAttribute(Const.REQUEST_ATTR_RENDER_REQUEST, request);
		request.setAttribute(Const.REQUEST_ATTR_RENDER_RESPONSE, response);

		// set the url of the wings servlet for the filter
		request.setAttribute(Const.REQUEST_ATTR_WINGS_SERVLET_URL,
				wingSServletURL);

		selectWingSClass(request);

		// dispatch to the wings servlet
		PortletContext portletContext;
		PortletRequestDispatcher requestDispatcher;

		portletContext = getPortletContext();
		requestDispatcher = portletContext
				.getRequestDispatcher(wingSServletURL);
		requestDispatcher.include(request, response);

	}

	protected void selectWingSClass(RenderRequest request) {

		String portletMode = request.getPortletMode().toString();

		if (portletMode.equals(PortletMode.EDIT.toString())
				&& editClass != null)
			request.setAttribute(Const.REQUEST_ATTR_WINGS_CLASS, editClass);
		else if (portletMode.equals(PortletMode.HELP.toString())
				&& helpClass != null)
			request.setAttribute(Const.REQUEST_ATTR_WINGS_CLASS, helpClass);
		else
			request.setAttribute(Const.REQUEST_ATTR_WINGS_CLASS, viewClass);
	}

}
