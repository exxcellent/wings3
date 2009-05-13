package org.wings.comet;

import org.wings.session.Session;

import javax.servlet.ServletContext;
import java.util.HashSet;

class ServletContextCometConnectionManager extends CometConnectionManager {

    private final ServletContext servletContext;
    private final HashSet<String> connectionSet;

    public ServletContextCometConnectionManager(Session session) {
        servletContext = session.getServletContext();
        synchronized (servletContext) {
            connectionSet = getSharedObject();
        }
    }

    public boolean isHangingGetActive() {
        synchronized (connectionSet) {
            return connectionSet.contains(browserId);
        }
    }

    public boolean hangingGetActive(boolean value) {
        synchronized (connectionSet) {
            final boolean oldValue = connectionSet.contains(browserId);
            if (value) {
                connectionSet.add(browserId);
            } else {
                connectionSet.remove(browserId);
            }
            return oldValue;
        }
    }

    public void setHangingGetActive(boolean value) {
        synchronized (connectionSet) {
            if (value) {
                connectionSet.add(browserId);
            } else {
                connectionSet.remove(browserId);
            }
        }
    }

    HashSet<String> getSharedObject() {
        HashSet<String> connectionSet = (HashSet<String>) servletContext.getAttribute(NAME);
        if (connectionSet == null) {
            connectionSet = new HashSet<String>();
            servletContext.setAttribute(NAME, connectionSet);
        }
        return connectionSet;
    }
}
