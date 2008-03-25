package org.wings.test;

import org.wings.test.DummyHttpSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Enumeration;

/**
 * A dummy HttpServletRequest where the most methods return null. The dummy is required for unit testing.
 * @see WingsTestCase
 * @author Rene Doering
 */
public class DummyHttpServletRequest implements HttpServletRequest {
    java.lang.String BASIC_AUTH = "BASIC";
    java.lang.String FORM_AUTH = "FORM";
    java.lang.String CLIENT_CERT_AUTH = "CLIENT_CERT";
    java.lang.String DIGEST_AUTH = "DIGEST";

    public String getAuthType(){
        return null;
    }

    public Cookie[] getCookies( ){
        return null;
    }

    public long getDateHeader(java.lang.String s){
        return 0;
    }

    public String getHeader(java.lang.String s){
        return null;
    }

    public Enumeration getHeaders(java.lang.String s){
        return null;
    }

    public Enumeration getHeaderNames(){
        return null;
    }

    public int getIntHeader(java.lang.String s){
        return 0;
    }

    public String getMethod(){
        return null;
    }

    public String getPathInfo(){
        return "/";
    }

    public String getPathTranslated(){
        return null;
    }

    public String getContextPath(){
        return null;
    }

    public String getQueryString(){
        return null;
    }

    public String getRemoteUser(){
        return null;
    }

    public boolean isUserInRole(java.lang.String s){
        return false;
    }

    public Principal getUserPrincipal(){
        return null;
    }

    public String getRequestedSessionId(){
        return null;
    }

    public String getRequestURI(){
        return null;
    }

    public StringBuffer getRequestURL(){
        return null;
    }

    public String getServletPath(){
        return null;
    }

    public HttpSession getSession(boolean b){
        return new DummyHttpSession();
    }

    public HttpSession getSession(){
        return new DummyHttpSession();
    }

    public boolean isRequestedSessionIdValid(){
        return false;
    }

    public boolean isRequestedSessionIdFromCookie(){
        return false;
    }

    public boolean isRequestedSessionIdFromURL(){
        return false;
    }

    /**
     * @deprecated
     */
    public boolean isRequestedSessionIdFromUrl(){
        return false;
    }



    public Object getAttribute(java.lang.String s){
        return null;
    }

    public Enumeration getAttributeNames(){
        return null;
    }

    public String getCharacterEncoding(){
        return null;
    }

    public void setCharacterEncoding(java.lang.String s) throws java.io.UnsupportedEncodingException{
    }

    public int getContentLength(){
        return 0;
    }

    public java.lang.String getContentType(){
        return null;
    }

    public javax.servlet.ServletInputStream getInputStream() throws java.io.IOException{
        return null;
    }

    public java.lang.String getParameter(java.lang.String s){
        return null;
    }

    public java.util.Enumeration getParameterNames(){
        return null;
    }

    public java.lang.String[] getParameterValues(java.lang.String s){
        return null;
    }

    public java.util.Map getParameterMap(){
        return null;
    }

    public java.lang.String getProtocol(){
        return null;
    }

    public java.lang.String getScheme(){
        return null;
    }

    public java.lang.String getServerName(){
        return null;
    }

    public int getServerPort(){
        return 0;
    }

    public java.io.BufferedReader getReader() throws java.io.IOException{
        return null;
    }

    public java.lang.String getRemoteAddr(){
        return null;
    }

    public java.lang.String getRemoteHost(){
        return null;
    }

    public void setAttribute(java.lang.String s, java.lang.Object o){ }

    public void removeAttribute(java.lang.String s){ }

    public java.util.Locale getLocale(){
        return null;
    }

    public java.util.Enumeration getLocales(){
        return null;
    }

    public boolean isSecure(){
        return false;
    }

    public javax.servlet.RequestDispatcher getRequestDispatcher(java.lang.String s){
        return null;
    }

    /**
     * @deprecated
     */
    public java.lang.String getRealPath(java.lang.String s){
        return null;
    }
}
