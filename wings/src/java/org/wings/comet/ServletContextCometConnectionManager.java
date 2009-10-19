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

    public boolean canAddHangingGet() {
        synchronized (connectionSet) {
            return !connectionSet.contains(getBrowserId());
        }
    }

    public boolean addHangingGet() {
        synchronized (connectionSet) {
            final boolean result = !connectionSet.contains(getBrowserId());
            connectionSet.add(getBrowserId());
            return result;
        }
    }

    public void removeHangingGet() {
		synchronized (connectionSet) {
			connectionSet.remove(getBrowserId());
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
