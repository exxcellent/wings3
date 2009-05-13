package org.wings.comet;

import org.wings.SFrame;
import org.wings.session.Session;

import javax.servlet.http.HttpServlet;

public class Comet {

    public static final int NO_COMET_CONNECTION_MANAGER = 0;
    public static final int SESSION_COMET_CONNECTION_MANAGER = 1;
    public static final int DEFAULT_COMET_CONNECTION_MANAGER = 2;

    private static final String COMET_UPDATE_CONNECT = "connect";
    private static final String COMET_UPDATE_DISCONNECT = "disconnect";
    static final String COMET_UPDATE_HANGING = "switchToHanging";

    private final Session session;
    private final Pushable pushable;

    private CometConnectionManager connectionManager = null;

    private boolean cometActive;

    private SFrame frame = null;
    private boolean binding = false;

    private long longPollingTimeout = 1000*30;
    private long periodicPollingInterval = 1000*2;

    public Comet(Session session, HttpServlet servlet) {
        this.session = session;
        cometActive = false;
        this.setConnectionManager(1);

        if (servlet instanceof JettyCometWingServlet) {
            pushable = new JettyPushable(session);
        } else if (servlet instanceof GlassfishCometWingServlet) {
            pushable = new GlassfishPushable(session);
        } else {
            pushable = new TomcatPushable(session);
        }
    }

    SFrame getBindedFrame() {
        return frame;
    }

    public void bind(SFrame frame) {
        binding = true;
        this.frame = frame;
    }

    boolean isCometActive() {
        return cometActive;
    }

    public long getLongPollingTimeout() {
        return longPollingTimeout;
    }

    public void setLongPollingTimeout(long longPollingTimeout) {
        this.longPollingTimeout = longPollingTimeout;
    }

    public long getPeriodicPollingInterval() {
        return periodicPollingInterval;
    }

    public void setPeriodicPollingInterval(long periodicPollingInterval) {
        this.periodicPollingInterval = periodicPollingInterval;
    }

    public void connect() {
        cometActive = true;
        if (frame != null) {
            frame.setCometUpdate(COMET_UPDATE_CONNECT);
        }
    }

    public void disconnect() { 
        cometActive = false;
        //if (pushable == null) return;
        synchronized (pushable) {
            if (pushable.isValid()) {
                pushable.disconnect();
            }
        }
        frame.setCometUpdate(COMET_UPDATE_DISCONNECT);
    }

    public boolean isCometEnabled(SFrame frame) {
        if (!binding) {
            bind(session.getRootFrame());
        }
        return session.isCometWingServletEnabled() && cometActive && (this.frame == frame);
    }

    public Pushable getPushable() {
        return pushable;
    }

    public void setConnectionManager(int cometConnectionManager) {
        switch (cometConnectionManager) {
            case NO_COMET_CONNECTION_MANAGER: this.connectionManager = null; break;
            case SESSION_COMET_CONNECTION_MANAGER: this.connectionManager = new SessionCometConnectionManager(); break;
            case DEFAULT_COMET_CONNECTION_MANAGER: this.connectionManager = new DefaultCometConnectionManager(); break;
            default: this.connectionManager = new DefaultCometConnectionManager();
        }
    }

    public CometConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
