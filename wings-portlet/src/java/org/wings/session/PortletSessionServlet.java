/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.session;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.*;
import org.wings.script.JavaScriptListener;
import org.wings.event.ExitVetoException;
import org.wings.event.SRequestEvent;
import org.wings.externalizer.ExternalizeManager;
import org.wings.externalizer.ExternalizedResource;
import org.wings.io.*;
import org.wings.portlet.Const;
import org.wings.portlet.PortletParameterCodec;
import org.wings.resource.*;
import org.wings.util.SStringBuilder;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * The servlet engine creates for each user a new HttpSession. This
 * HttpSession can be accessed by all Serlvets running in the engine. A
 * WingServlet creates one wings SessionServlet per HTTPSession and stores
 * it in its context.
 * <p>As the SessionServlets acts as Wrapper for the WingsServlet, you can
 * access from there as used the  ServletContext and the HttpSession.
 * Additionally the SessionServlet containts also the wingS-Session with
 * all important services and the superordinated SFrame. To this SFrame all
 * wings-Components and hence the complete application state is attached.
 * The developer can access from any place via the SessionManager a
 * reference to the wingS-Session. Additionally the SessionServlet
 * provides access to the all containing HttpSession.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 */
final class PortletSessionServlet
        extends HttpServlet
        implements HttpSessionBindingListener {
    private final transient static Log log = LogFactory.getLog(PortletSessionServlet.class);

    /**
     * The parent {@link PortletWingServlet}
     */
    protected transient HttpServlet parent = this;

    /**
     * The session.
     */
    private transient /* --- ATTENTION! This disable session serialization! */ ExtendedSession session;

    private boolean firstRequest = true;

    /** Refer to comment in {@link #doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)} */
    private String exitSessionWorkaround;

    /**
     * Default constructor.
     */
    protected PortletSessionServlet() {
    }

    /**
     * Sets the parent servlet contianint this wings session
     * servlet (WingsServlet, delegating its requests to the SessionServlet).
     */
    protected final void setParent(HttpServlet p) {
        if (p != null) parent = p;
    }

    public final Session getSession() {
        return session;
    }

    /**
     * Overrides the session set for setLocaleFromHeader by a request parameter.
     * Hence you can force the wings session to adopt the clients Locale.
     */
    public final void setLocaleFromHeader(String[] args) {
        if (args == null)
            return;

        for (int i = 0; i < args.length; i++) {
            try {
                getSession().setLocaleFromHeader(Boolean.valueOf(args[i]).booleanValue());
            }
            catch (Exception e) {
                log.error("setLocaleFromHeader", e);
            }
        }
    }

    /**
     * The Locale of the current wings session servlet is determined by
     * the locale transmitted by the browser. The request parameter
     * <PRE>LocaleFromHeader</PRE> can override the behaviour
     * of a wings session servlet to adopt the clients browsers Locale.
     *
     * @param req The request to determine the local from.
     */
    protected final void handleLocale(HttpServletRequest req) {
        setLocaleFromHeader(req.getParameterValues("LocaleFromHeader"));

        if (getSession().getLocaleFromHeader())
            getSession().determineLocale();
    }

    // jetzt kommen alle Servlet Methoden, die an den parent deligiert
    // werden


    public ServletContext getServletContext() {
        if (parent != this)
            return parent.getServletContext();
        else
            return super.getServletContext();
    }


    public String getInitParameter(String name) {
        if (parent != this)
            return parent.getInitParameter(name);
        else
            return super.getInitParameter(name);
    }


    public Enumeration getInitParameterNames() {
        if (parent != this)
            return parent.getInitParameterNames();
        else
            return super.getInitParameterNames();
    }

    /**
     * Delegates log messages to the according WingsServlet or alternativly
     * to the HttpServlet logger.
     *
     * @param msg The logmessage
     */
    public void log(String msg) {
        if (parent != this)
            parent.log(msg);
        else
            super.log(msg);
    }


    public String getServletInfo() {
        if (parent != this)
            return parent.getServletInfo();
        else
            return super.getServletInfo();
    }


    public ServletConfig getServletConfig() {
        if (parent != this)
            return parent.getServletConfig();
        else
            return super.getServletConfig();
    }

    // bis hierhin

    /**
     * init
     */
    public final void init(ServletConfig config,
                           HttpServletRequest request,
                           HttpServletResponse response) throws ServletException {
        try {
        	// wingS-Portlet-Bridge: changed form Session to PortletSession
            session = new ExtendedSession();
            SessionManager.setSession(session);

            // set request.url in session, if used in constructor of wings main classs
            //if (request.isRequestedSessionIdValid()) {               
        		// get the RenderResponse out of the request
        		RenderResponse renderResponse = (RenderResponse)request.getAttribute(Const.REQUEST_ATTR_RENDER_RESPONSE);
        		if(renderResponse==null) {
        			log.error("WingS-Portlet-Bridge: cant get the request attribute " + 
        					Const.REQUEST_ATTR_RENDER_RESPONSE);
        		}
        		PortletURL actionURL = renderResponse.createActionURL();
            	// this will fire an event, if the encoding has changed ..
                session.setProperty("request.url", 
                		new PortletRequestURL(actionURL.toString(), response.encodeURL(actionURL.toString())));
                log.debug("WingS-Portlet-Bridge: created while init PortletRequestURL " + actionURL.toString());
                session.setProperty(Const.WINGS_SESSION_PROPERTY_RENDER_RESPONSE,
                        renderResponse);
                
                // get the RenderRequest
                RenderRequest renderRequest = (RenderRequest)request.getAttribute(Const.REQUEST_ATTR_RENDER_REQUEST);
                if(renderRequest==null) {
                	log.error("WingS-Portlet-Bridge: cant get the request attribute " + 
                			Const.REQUEST_ATTR_RENDER_REQUEST);
                }
                session.setProperty(Const.WINGS_SESSION_PROPERTY_RENDER_REQUEST,
                		renderRequest);
           
                
            //}

            session.init(config, request, response);


            try {
            	// WingS-Portlet-Bridge: load of class for current mode
            	String mainClassName = (String)request.getAttribute(Const.REQUEST_ATTR_WINGS_CLASS);            	
//            	String mainClassName = config.getInitParameter("wings.mainclass");
            	log.info("WingS-Portlet-Bridge: loaded mainclass " + mainClassName + " for PortletSessionServlet");
                Class mainClass = null;
                try {
                    mainClass = Class.forName(mainClassName, true,
                            Thread.currentThread()
                            .getContextClassLoader());
                } catch (ClassNotFoundException e) {
                    // fallback, in case the servlet container fails to set the
                    // context class loader.
                    mainClass = Class.forName(mainClassName);
                }
                mainClass.newInstance();
            } catch (Exception ex) {
                log.fatal("could not load wings.mainclass: " +
                		request.getAttribute(Const.REQUEST_ATTR_WINGS_CLASS), ex);
                throw new ServletException(ex);
            }
        } finally {
            // The session was set by the constructor. After init we
            // expect that only doPost/doGet is called, which set the
            // session also. So remove it here.
            SessionManager.removeSession();
        }
    }

    /**
     * this method references to
     * {@link #doGet(HttpServletRequest, HttpServletResponse)}
     */
    public final void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        //value chosen to limit denial of service
        if (req.getContentLength() > getSession().getMaxContentLength() * 1024) {
            res.setContentType("text/html");
            ServletOutputStream out = res.getOutputStream();
            // WingS-Portlet-Bridge: removed unsupported tags
            out.println("<div><h1>Error - content length &gt; " +
                    getSession().getMaxContentLength() + "k");
            out.println("</h1></div>");
        } else {
            doGet(req, res);
        }
        // sollte man den obigen Block nicht durch folgende Zeile ersetzen?
        //throw new RuntimeException("this method must never be called!");
        // bsc: Wieso?
    }


    /**
     * Verarbeitet Informationen vom Browser:
     * <UL>
     * <LI> setzt Locale
     * <LI> Dispatch Get Parameter
     * <LI> feuert Form Events
     * </UL>
     * Ist synchronized, damit nur ein Frame gleichzeitig bearbeitet
     * werden kann.
     */
    public final synchronized void doGet(HttpServletRequest req,
                                         HttpServletResponse response) {
        // Special case: You double clicked i.e. a "logout button"
        // First request arrives, second is on hold. First invalidates session and sends redirect as response,
        // but browser ignores and expects response in second request. But second request has longer a valid session.
        if (session == null) {
            try {
                response.sendRedirect(exitSessionWorkaround != null ? exitSessionWorkaround : "");
                return;
            } catch (IOException e) {
                log.info("Session exit workaround failed to to IOException (triple click?)");
            }
        }

        SessionManager.setSession(session);
        session.setServletRequest(req);
        session.setServletResponse(response);

        session.fireRequestEvent(SRequestEvent.REQUEST_START);

        // in case, the previous thread did not clean up.
        SForm.clearArmedComponents();

        Device outputDevice = null;

        ReloadManager reloadManager = session.getReloadManager();

        try {
            /*
             * The tomcat 3.x has a bug, in that it does not encode the URL
             * sometimes. It does so, when there is a cookie, containing some
             * tomcat sessionid but that is invalid (because, for instance,
             * we restarted the tomcat in-between).
             * [I can't think of this being the correct behaviour, so I assume
             *  it is a bug. ]
             *
             * So we have to workaround this here: if we actually got the
             * session id from a cookie, but it is not valid, we don't do
             * the encodeURL() here: we just leave the requestURL as it is
             * in the properties .. and this is url-encoded, since
             * we had set it up in the very beginning of this session
             * with URL-encoding on  (see WingServlet::newSession()).
             *
             * Vice versa: if the requestedSessionId is valid, then we can
             * do the encoding (which then does URL-encoding or not, depending
             * whether the servlet engine detected a cookie).
             * (hen)
             */
            RequestURL portletRequestURL = null;
            // get the renderResponse
        	RenderResponse renderResponse = (RenderResponse)req.getAttribute(Const.REQUEST_ATTR_RENDER_RESPONSE);
            if(renderResponse==null) {
            	log.error("WingS-Portlet-Bridge: cant get the request attribute " + 
            			Const.REQUEST_ATTR_RENDER_RESPONSE);
            }
        	PortletURL actionURL = renderResponse.createActionURL();
            if (req.isRequestedSessionIdValid()) {
                portletRequestURL = new PortletRequestURL(
                		actionURL.toString(), response.encodeURL(actionURL.toString()));
                log.debug("WingS-Portlet-Bridge: created PortletRequestURL " + actionURL.toString());
                // this will fire an event, if the encoding has changed ..
                session.setProperty("request.url",
                        portletRequestURL);
                session.setProperty(Const.WINGS_SESSION_PROPERTY_RENDER_RESPONSE,
                        renderResponse);
                
                // get the RenderRequest
                RenderRequest renderRequest = (RenderRequest)req.getAttribute(Const.REQUEST_ATTR_RENDER_REQUEST);
                if(renderRequest==null) {
                	log.error("WingS-Portlet-Bridge: cant get the request attribute " + 
                			Const.REQUEST_ATTR_RENDER_REQUEST);
                }
                session.setProperty(Const.WINGS_SESSION_PROPERTY_RENDER_REQUEST,
                		renderRequest);
            }

            if (log.isDebugEnabled()) {
                log.debug("Request URL: " + portletRequestURL);
                log.debug("HTTP header:");
                for (Enumeration en = req.getHeaderNames(); en.hasMoreElements();) {
                    String header = (String) en.nextElement();
                    log.debug("    " + header + ": " + req.getHeader(header));
                }
            }
            handleLocale(req);

            // WingS-Portlet-Bridge: get the Parameter from the map in the request
            // set by the portlet
            Map params = (Map) req.getAttribute(Const.REQUEST_ATTR_PARAMETERS_FROM_ACTION_MAP);
            
            // The externalizer is able to handle static and dynamic resources
            ExternalizeManager extManager = getSession().getExternalizeManager();
            //WingS-Portlet-Bridge:
            //String pathInfo = req.getPathInfo();                    // Note: Websphere returns <code>null</code> here!
            String pathInfo = null;
            if(params!=null) {
            	String[] path = (String[]) params.get(Const.REQUEST_PARAM_RESOURCE_AS_PARAM);
            	if(path!=null)
            		pathInfo = path[0];
            }
            
            if (pathInfo != null && pathInfo.length() > 0) {
                // strip of leading /
            	// WingS-Portlet-Bridge:
            	// pathInfo = pathInfo.substring(1);
            }

            log.info("WingS-Portlet-Bridge: pathInfo: " + pathInfo);

            // If we have no path info, or the special '_' path info (that should be explained
            // somewhere, Holger), then we deliver the top-level frame of this application.
            String externalizeIdentifier = null;
            if (pathInfo == null || pathInfo.length() == 0 || "_".equals(pathInfo) || firstRequest) {
                externalizeIdentifier = retrieveCurrentRootFrameResource().getId();
                firstRequest = false;
            } else {
                externalizeIdentifier = pathInfo;
            }

            // Retrieve externalized resource
            ExternalizedResource extInfo = extManager.getExternalizedResource(externalizeIdentifier);

            // Special case handling: We request a .html resource of a session which is not accessible.
            // This happens some times and leads to a 404, though it should not be possible.
            if (extInfo == null && pathInfo != null && pathInfo.endsWith(".html")) {
                log.info("Found a request to an invalid .html during a valid session. Redirecting to root frame.");
                response.sendRedirect(retrieveCurrentRootFrameResource().getURL().toString());
                return;
            }

            if (extInfo != null && extInfo.getObject() instanceof UpdateResource) {
                reloadManager.setUpdateMode(true);
            } else {
                reloadManager.setUpdateMode(false);
            }

            // Prior to dispatching the actual events we have to detect
            // their epoch and inform the dispatcher which will then be
            // able to check if the request is valid and processed. If
            // this is not the case, we force a complete page reload.
            String ee = "";
            if(params!=null) {
            	String[] eeArray = (String[]) params.get("event_epoch");
            	if(eeArray!=null)
            		ee = eeArray[0];
            }
            session.getDispatcher().setEventEpoch(ee);

            // WingS-Portlet-Bridge: Map for the parameters 
            // set by a SPortletAnchor or set in the Portlet
            Map portletParameters = new HashMap();
            
            
            // Enumeration en = req.getParameterNames();
            if(params!=null) {
	            Set paramNames = params.keySet();
	            Iterator paramNamesIter = paramNames.iterator();
            
	            Cookie[] cookies = req.getCookies();

	            // are there parameters/low level events to dispatch
	            if (paramNamesIter.hasNext()) {
	            	// only fire DISPATCH_START if we have parameters to dispatch
	            	session.fireRequestEvent(SRequestEvent.DISPATCH_START);

	            	if (cookies != null) {
	            		//dispatch cookies
	                    for (int i = 0; i < cookies.length; i++) {
	                        Cookie cookie = cookies[i];
	                        String paramName = cookie.getName();
	                        String value = cookie.getValue();
	
	                        if (log.isDebugEnabled())
	                            log.debug("dispatching cookie " + paramName + " = " + value);
	
	                        session.getDispatcher().dispatch(paramName, new String[] { value });
	                    }
	            	}

	                if (log.isDebugEnabled()) {
	                    log.debug("Parameters:");
	                    for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
	                        String paramName = (String) e.nextElement();
	                        SStringBuilder param = new SStringBuilder();
	                        param.append("    ").append(paramName).append(": ");
	                        final String[] values = req.getParameterValues(paramName);
	                        param.append(values != null ? Arrays.toString(values) : "null");
	                        log.debug(param);
	                    }
	                }

	                while (paramNamesIter.hasNext()) {
	                    String paramName = (String) paramNamesIter.next();
	                    String[] values = (String[])params.get(paramName);
	
	                    // We do not need to dispatch the event epoch and the XHR request ID
	                    if (paramName.equals("event_epoch") || paramName.equals("_xhrID")) {
	                        continue;
	                    }
	
	                    String value = values[0];
	
	                    // Split the values of the event trigger
	                    if (paramName.equals("event_trigger")) {
	                        int pos = value.indexOf('|');
	                        paramName = value.substring(0, pos);
	                        values = new String[] { value.substring(pos +1) };
	                    }
	
	                    // Handle form submit via default button
	                    if (paramName.equals("default_button")) {
	                        if (value.equals("undefined")) {
	                            continue;
	                        } else {
	                            paramName = values[0];
	                            values = new String[] { "1" };
	                        }
	                    }
	                    
	                    // WingS-Portlet-Bridge: get the portlet parameters
	                    if(paramName.startsWith(Const.WINGS_PORTLET_URL_CODE_STRING)) {
	                    	log.info("WingS-Portlet-Bridge: getting portlet parameter " + paramName + " = " + Arrays.asList(values));
	                    	portletParameters.put(PortletParameterCodec.decode(paramName), values);
	                    }
	                    else {
		                    if (log.isDebugEnabled())
		                        log.debug("dispatching " + paramName + " = " + Arrays.asList(values));	
		                    session.getDispatcher().dispatch(paramName, values);
	                    }
	
	                }

	                SForm.fireEvents();

	                // only fire DISPATCH DONE if we have parameters to dispatch
	                session.fireRequestEvent(SRequestEvent.DISPATCH_DONE);
	            }
            }
            
            //WingS-Portlet-Bridge: store the portlet parameters in the session
            session.setProperty(Const.WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_MAP,
                    portletParameters); 

            session.fireRequestEvent(SRequestEvent.PROCESS_REQUEST);
            session.getDispatcher().invokeRunnables();
            
            // WingS-Portlet-Bridge: fires events if the window state has changed
            session.fireWindowStateEvents();
            // WingS-Portlet-Bridge: fires events for the new portlet parameters
            session.fireNewPortletParameters();

            // if the user chose to exit the session as a reaction on an
            // event, we got an URL to redirect after the session.
            /*
             * where is the right place?
             * The right place is
             *    - _after_ we processed the events
             *        (e.g. the 'Pressed Exit-Button'-event or gave
             *         the user the chance to exit this session in the custom
             *         processRequest())
             *    - but _before_ the rendering of the page,
             *      because otherwise an redirect won't work, since we must
             *      not have sent anything to the output stream).
             */
            if (session.getExitAddress() != null) {

                try {
                    session.firePrepareExit();
                    session.fireRequestEvent(SRequestEvent.REQUEST_END);

                    String redirectAddress;
                    if (session.getExitAddress().length() > 0) {
                        // redirect to user requested URL.
                        redirectAddress = session.getExitAddress();
                    } else {
                        // redirect to a fresh session.
                        redirectAddress = req.getRequestURL().toString();
                    }
                    req.getSession().invalidate(); // calls destroy implicitly
                    response.sendRedirect(redirectAddress);
                    exitSessionWorkaround = redirectAddress;
                    return;
                } catch (ExitVetoException ex) {
                    session.exit(null);
                } // end of try-catch
            }

            if (session.getRedirectAddress() != null) {
                handleRedirect(response);
                return;
            }

            reloadManager.notifyCGs();
            reloadManager.invalidateFrames();

            // TODO ResourceMapper
            ResourceMapper mapper = session.getResourceMapper();
            if (extInfo == null && mapper != null) {
            	//wings-Portlet-Bridge:
//                Resource res = mapper.mapResource(req.getPathInfo());
            	 Resource res = mapper.mapResource(pathInfo);
                if (res != null) {
                    extInfo = extManager.getExternalizedResource(res.getId());
                }
            }

            if (extInfo != null) {
                outputDevice = DeviceFactory.createDevice(extInfo);
                session.fireRequestEvent(SRequestEvent.DELIVER_START, extInfo);

                long startTime = System.currentTimeMillis();
                extManager.deliver(extInfo, response, outputDevice);
                long endTime = System.currentTimeMillis();
                log.debug("------------------------- Time needed for rendering: " +
                        (endTime - startTime) + " ms -------------------------\n");

                session.fireRequestEvent(SRequestEvent.DELIVER_DONE, extInfo);
            } else {
                handleUnknownResourceRequested(req, response);
            }

        }
        catch (Throwable e) {
            log.error("Uncaught Exception", e);
            handleException(response, e);
        }
        finally {
            if (session != null) {
                session.fireRequestEvent(SRequestEvent.REQUEST_END);
            }

            if (outputDevice != null) {
                try {
                    outputDevice.close();
                } catch (Exception e) {
                }
            }

            /*
             * the session might be null due to destroy().
             */
            if (session != null) {
                reloadManager.clear();
                session.setServletRequest(null);
                session.setServletResponse(null);
            }

            // make sure that the session association to the thread is removed
            // from the SessionManager
            SessionManager.removeSession();
            SForm.clearArmedComponents();
        }
    }

    /**
     * Searches the current session for the root HTML frame and returns the Resource
     * representing this root HTML frame (i.e. for you to retrieve the externalizer id
     * via <code>getId()</code>-method).
     * @return Resource of the root HTML frame
     */
    private Resource retrieveCurrentRootFrameResource() throws ServletException {
        log.debug("delivering default frame");

        if (session.getFrames().size() == 0)
            throw new ServletException("no frame visible");

        // get the first frame from the set and walk up the hierarchy.
        // this should work in most cases. if there are more than one
        // toplevel frames, the developer has to care about the resource
        // ids anyway ..
        SFrame defaultFrame = (SFrame) session.getFrames().iterator().next();
        while (defaultFrame.getParent() != null)
            defaultFrame = (SFrame) defaultFrame.getParent();

        return defaultFrame.getDynamicResource(ReloadResource.class);
    }

    private void handleRedirect(HttpServletResponse response) throws IOException {
        try {
            ReloadManager reloadManager = session.getReloadManager();
            if (reloadManager.isUpdateMode()) {
                String script = "wingS.request.sendRedirect(\"" + session.getRedirectAddress() + "\");";
                session.getScriptManager().addScriptListener(new JavaScriptListener(null, null, script));
                /*
                Resource root = retrieveCurrentRootFrameResource();
                ExternalizedResource externalizedResource = session.getExternalizeManager().getExternalizedResource(root.getId());
                session.fireRequestEvent(SRequestEvent.DELIVER_START, externalizedResource);

                String encoding = session.getCharacterEncoding();
                response.setContentType("text/xml; charset=" + encoding);
                ServletOutputStream out = response.getOutputStream();
                Device outputDevice = new ServletDevice(out);
                UpdateResource.writeHeader(outputDevice);
                UpdateResource.writeUpdate(outputDevice, "wingS.request.sendRedirect(\"" + session.getRedirectAddress() + "\");");
                UpdateResource.writeFooter(outputDevice);
                out.flush();

                session.fireRequestEvent(SRequestEvent.DELIVER_DONE, externalizedResource);
                session.fireRequestEvent(SRequestEvent.REQUEST_END);

                reloadManager.clear();
                session.setServletRequest(null);
                session.setServletResponse(null);
                SessionManager.removeSession();
                SForm.clearArmedComponents();
                */
            }
            else {
                response.sendRedirect(session.getRedirectAddress());
            }
        }
        catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        finally {
            session.setRedirectAddress(null);
        }
    }

    /**
     * In case of an error, display an error page to the user. This is only
     * done if there is a properties <code>wings.error.handler</code> defined
     * in the web.xml file. If the property is present, the following steps
     * are performed:
     * <li> Load the class named by the value of that property, using the
     *      current thread's context class loader,
     * <li> Instantiate that class using its zero-argument constructor,
     * <li> Cast the instance to ExceptionHandler,
     * <li> Invoke the handler's <tt>handle</tt> method, passing it the
     *      <tt>thrown</tt> argument that was passed to this method.
     * </ol>
     *
     * @see DefaultExceptionHandler
     * @param response the HTTP Response to use
     * @param thrown the Exception to report
     */
    protected void handleException(HttpServletResponse response, Throwable thrown) {
        try {
            String className = (String)session.getProperty("wings.error.handler");
            if (className == null)
                className = DefaultExceptionHandler.class.getName();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class clazz = classLoader.loadClass(className);
            ExceptionHandler exceptionHandler = (ExceptionHandler)clazz.newInstance();

            StringBuilderDevice device = new StringBuilderDevice();
            exceptionHandler.handle(device, thrown);
            Resource resource = new StringResource(device.toString(), "html", "text/html");
            String url = session.getExternalizeManager().externalize(resource);

            ReloadManager reloadManager = session.getReloadManager();
            reloadManager.notifyCGs();

            session.fireRequestEvent(SRequestEvent.DISPATCH_DONE);
            session.fireRequestEvent(SRequestEvent.PROCESS_REQUEST);

            if (reloadManager.isUpdateMode()) {
                Resource root = retrieveCurrentRootFrameResource();
                ExternalizedResource externalizedResource = session.getExternalizeManager().getExternalizedResource(root.getId());
                session.fireRequestEvent(SRequestEvent.DELIVER_START, externalizedResource);

                String encoding = session.getCharacterEncoding();
                response.setContentType("text/xml; charset=" + encoding);
                ServletOutputStream out = response.getOutputStream();
                Device outputDevice = new ServletDevice(out, encoding);
                UpdateResource.writeHeader(outputDevice);
                UpdateResource.writeUpdate(outputDevice, "wingS.request.sendRedirect(\"" + url + "\");");
                UpdateResource.writeFooter(outputDevice);
                out.flush();

                session.fireRequestEvent(SRequestEvent.DELIVER_DONE, externalizedResource);
                session.fireRequestEvent(SRequestEvent.REQUEST_END);

                reloadManager.clear();
                session.setServletRequest(null);
                session.setServletResponse(null);
                SessionManager.removeSession();
                SForm.clearArmedComponents();
            }
            else {
                // TODO FIXME: This redirect is in most times too late. Redirect works only if no byte
                // has yet been sent to the client (dispatch phase)
                // Won't work if exception occurred during rendering phase
                // Solution: Provide / send javascript code to redirect.
                response.sendRedirect(url);
            }
        }
        catch (Exception e) {
            log.warn(e.getMessage(), thrown);
        }
    }

    /**
     * This method is called, whenever a Resource is requested whose
     * name is not known within this session.
     *
     * @param req the causing HttpServletRequest
     * @param res the HttpServletResponse of this request
     */
    protected void handleUnknownResourceRequested(HttpServletRequest req,
                                                  HttpServletResponse res)
            throws IOException {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.setContentType("text/html");
        res.getOutputStream().println("<h1>404 Not Found</h1>Unknown Resource Requested: " /*+ req.getPathInfo()*/);
    }


    /* HttpSessionBindingListener */
    public void valueBound(HttpSessionBindingEvent event) {
    }


    /* HttpSessionBindingListener */
    public void valueUnbound(HttpSessionBindingEvent event) {
        destroy();
    }

    /**
     * get the Session Encoding, that is appended to each URL.
     * Basically, this is response.encodeURL(""), but unfortuntatly, this
     * empty encoding isn't supported by Tomcat 4.x anymore.
     */
    public static String getSessionEncoding(HttpServletResponse response) {
        if (response == null) return "";
        // encode dummy non-empty URL.
        return response.encodeURL("foo").substring(3);
    }

    /**
     * Destroy and cleanup the session servlet.
     */
    public void destroy() {
        log.info("destroy called");
        try {
            if (session != null) {
                // Session is needed on destroying the session
                SessionManager.setSession(session);
                session.destroy();
            }

            // hint the gc.
            parent = null;
            session = null;
        } catch (Exception ex) {
            log.error("destroy", ex);
        } finally {
            SessionManager.removeSession();
        }
    }

    /**
     * A check if this session servlet seems to be alive or is i.e. invalid because it
     * was deserialized.
     *
     * @return <code>true</code>, if this session servlet seems to be valid and alive.
     */
    public boolean isValid() {
        return session != null && parent != null;
    }
}


