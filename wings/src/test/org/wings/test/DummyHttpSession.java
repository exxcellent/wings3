package org.wings.test;

import javax.servlet.http.HttpSession;


public class DummyHttpSession implements HttpSession {
    public long getCreationTime(){
        return 0;
    }

    public java.lang.String getId(){ return null; }

    public long getLastAccessedTime(){ return 0; }

    public javax.servlet.ServletContext getServletContext(){
        return new DummyServletContext();
    }

    public void setMaxInactiveInterval(int i){ }

    public int getMaxInactiveInterval(){ return 0; }

    /**
     * @deprecated
     */
    public javax.servlet.http.HttpSessionContext getSessionContext(){ return null; }

    public java.lang.Object getAttribute(java.lang.String s){ return null; }

    /**
     * @deprecated
     */
    public java.lang.Object getValue(java.lang.String s){ return null; }

    public java.util.Enumeration getAttributeNames(){ return null; }

    /**
     * @deprecated
     */
    public java.lang.String[] getValueNames(){ return null; }

    public void setAttribute(java.lang.String s, java.lang.Object o){ }

    /**
     * @deprecated
     */
    public void putValue(java.lang.String s, java.lang.Object o){ }

    public void removeAttribute(java.lang.String s){  }

    /**
     * @deprecated
     */
    public void removeValue(java.lang.String s){  }

    public void invalidate(){  }

    public boolean isNew(){ return false; }
}
