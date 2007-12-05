package org.wings.portlet.filter;

import java.io.IOException;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.portlet.Const;

/**
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 */
public class WingS2PortletResponseFilter implements Filter {
	
	private final transient static Log log = LogFactory.getLog(WingS2PortletResponseFilter.class); 

	public void init(FilterConfig config) throws ServletException {	
	}

	/** 
	 * Filters the response from the wings servlet. replaces the relative urls 
	 * with absolut urls using the contextPath from the renderRequest and 
	 * the servlet URL, both given through the request from the portlet.
	 */
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain filterChain) 
		throws IOException, ServletException {
		
		log.debug("start doFilter()");
		
		// create response wrapper to get the response content             
		final WingS2PortletResponseWrapper wrapper = 
			new WingS2PortletResponseWrapper((HttpServletResponse) response);
            
		// do the filter chain      
	    filterChain.doFilter(request, wrapper);
	      
	    // get the response stream as string
	    String responseContent = wrapper.toString();
	                     
	    // get the renderRequest and the renderResponse out of the request
	    RenderRequest renderRequest = (RenderRequest) request.getAttribute(Const.REQUEST_ATTR_RENDER_REQUEST);
	    RenderResponse renderResponse = (RenderResponse) request.getAttribute(Const.REQUEST_ATTR_RENDER_RESPONSE);	  
	    
	    // get the contextPath from the request and encode it with the response encoder
	    String contextPathEncoded = "";
	    if(renderRequest!=null) {
	    	String contextPath = renderRequest.getContextPath();
	    	if(renderResponse!=null) {
	    		contextPathEncoded = renderResponse.encodeURL(contextPath);
	    	}
	    	else {
		    	log.error("the renderResponse from the response attribute " 
		    			+ Const.REQUEST_ATTR_RENDER_RESPONSE + " is null");
	    	}
	    }
	    else {
	    	log.error("the renderRequest from the request attribute " 
	    			+ Const.REQUEST_ATTR_RENDER_REQUEST + " is null");
	    }

	    // get the path to the wing servlet from the request
	    String wingSServletURL = "";    
	    wingSServletURL = (String) request.getAttribute(Const.REQUEST_ATTR_WINGS_SERVLET_URL);	    
	    if(wingSServletURL==null) {
	    	log.error("the wingsServletURL from the request attribute " 
	    			+ Const.REQUEST_ATTR_WINGS_SERVLET_URL + " is null");
	    }
	    
	    // replace relative urls 
	    responseContent = responseContent.replace("-org/wings/", contextPathEncoded + wingSServletURL + "-org/wings/");
	    responseContent = responseContent.replace("../dwr/", contextPathEncoded + "/dwr/");
	    
	    log.debug("response filtered");
	    
	    // write the response stream            
	    response.setContentLength(responseContent.length());
	    response.getOutputStream().print(responseContent);     
	    
	    log.debug("end doFilter()");
		
	}

	public void destroy() {		
	}

}
