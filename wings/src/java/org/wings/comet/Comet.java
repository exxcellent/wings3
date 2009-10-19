package org.wings.comet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.session.ScriptManager;
import org.wings.session.Session;

import javax.servlet.http.HttpServlet;

public class Comet {

    public static final int NO_COMET_CONNECTION_MANAGER = 0;
    public static final int SESSION_COMET_CONNECTION_MANAGER = 1;
    public static final int DEFAULT_COMET_CONNECTION_MANAGER = 2;

    private final Session session;
    private final Pushable pushable;

    private CometConnectionManager connectionManager = null;

    private boolean cometActive;

    private long longPollingTimeout = 1000*30;
    private long periodicPollingInterval = 1000*2;
    
    private static final transient Log log = LogFactory.getLog(Comet.class);

    public Comet(Session session, HttpServlet servlet) {
        this.session = session;
        cometActive = false;
        this.setConnectionManager( DEFAULT_COMET_CONNECTION_MANAGER );

        if (servlet instanceof JettyCometWingServlet) {
            pushable = new JettyPushable(session);
        } else if (servlet instanceof GlassfishCometWingServlet) {
            pushable = new GlassfishPushable(session);
        } else {
            pushable = new TomcatPushable(session);
        }
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

    
    public synchronized void activateComet() {
    	if (cometActive) {
			log.warn("Could not activate Comet. Comet is already active.");
			return;
		}
		cometActive = true;
		ScriptManager.getInstance().addScriptListener( new CometScript(CometScript.COMET_CONNECT) );
	}

    public synchronized void deactivateComet() { 
    	if (!cometActive) {
    		log.warn("Could not deactivate Comet. Comet isn't active.");
			return;
		}
        cometActive = false;

        synchronized (pushable) {
            if (pushable.isValid()) {
                pushable.disconnect();
            }
        }
        ScriptManager.getInstance().addScriptListener( new CometScript(CometScript.COMET_DISCONNECT));
    }

    public boolean isCometEnabled() {
        return session.isCometWingServletEnabled() && cometActive;
    }
    
    public void switchToHanging() {
    	ScriptManager.getInstance().addScriptListener( new CometScript(CometScript.COMET_SWITCH_TO_HANGING) );
    }


    public Pushable getPushable() {
        return pushable;
    }

    public void setConnectionManager(int cometConnectionManager) {
        switch (cometConnectionManager) {
            case NO_COMET_CONNECTION_MANAGER: this.connectionManager = new NullCometConnectionManager(); break;
            case SESSION_COMET_CONNECTION_MANAGER: this.connectionManager = new SessionCometConnectionManager(); break;
            case DEFAULT_COMET_CONNECTION_MANAGER: this.connectionManager = new DefaultCometConnectionManager(); break;
            default: this.connectionManager = new DefaultCometConnectionManager();
        }
    }

    public CometConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
