package org.wings.comet;

import javax.naming.*;
import java.util.HashSet;

class DefaultCometConnectionManager extends CometConnectionManager {

    private Context jndiContext;
    private final HashSet<String> connectionSet;

    public DefaultCometConnectionManager() {
        try {
            jndiContext = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        synchronized (jndiContext) {
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
        try {
            return (HashSet<String>) jndiContext.lookup(NAME);
        } catch (NamingException e) {
            //e.printStackTrace();
            final HashSet<String> connectionSet = new HashSet<String>();
            try {
                jndiContext.bind(NAME, connectionSet);
            } catch (NamingException e1) {
                e1.printStackTrace();
            }
            return connectionSet;
        }
    }
}
