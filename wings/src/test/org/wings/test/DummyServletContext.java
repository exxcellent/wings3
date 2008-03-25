package org.wings.test;

import javax.servlet.ServletContext;
import java.util.Properties;

/**
 * A dummy ServletContext where the most methods return null. The dummy is required for unit testing.
 * @see WingsTestCase
 * @author Rene Doering
 */
public class DummyServletContext implements ServletContext {
    private final Properties initParameters = new Properties();


    public javax.servlet.ServletContext getContext(java.lang.String s){ return null; }

    public int getMajorVersion(){ return 0; }

    public int getMinorVersion(){ return 0; }

    public java.lang.String getMimeType(java.lang.String s){ return null; }

    public java.util.Set getResourcePaths(java.lang.String s){ return null; }

    public java.net.URL getResource(java.lang.String s) throws java.net.MalformedURLException{ return null; }

    public java.io.InputStream getResourceAsStream(java.lang.String s){ return null; }

    public javax.servlet.RequestDispatcher getRequestDispatcher(java.lang.String s){ return null; }

    public javax.servlet.RequestDispatcher getNamedDispatcher(java.lang.String s){ return null; }

    /**
     * @deprecated
     */
    public javax.servlet.Servlet getServlet(java.lang.String s) throws javax.servlet.ServletException{ return null; }

    /**
     * @deprecated
     */
    public java.util.Enumeration getServlets(){ return null; }

    /**
     * @deprecated
     */
    public java.util.Enumeration getServletNames(){ return null; }

    public void log(java.lang.String s){  }

    /**
     * @deprecated
     */
    public void log(java.lang.Exception e, java.lang.String s){ ; }

    public void log(java.lang.String s, java.lang.Throwable throwable){  }

    public java.lang.String getRealPath(java.lang.String s){ return null; }

    public java.lang.String getServerInfo(){ return null; }

    public java.lang.String getInitParameter(java.lang.String s){
        return initParameters.getProperty(s);
    }

    public java.util.Enumeration getInitParameterNames(){
        return initParameters.keys();
    }

    public java.lang.Object getAttribute(java.lang.String s){ return null; }

    public java.util.Enumeration getAttributeNames(){ return null; }

    public void setAttribute(java.lang.String s, java.lang.Object o){  }

    public void removeAttribute(java.lang.String s){  }

    public java.lang.String getServletContextName(){ return null; }

}
