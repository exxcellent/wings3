package org.wings.comet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.servlet.http.HttpEvent;
import org.jboss.servlet.http.HttpEventServlet;
import org.wings.session.Session;

public class JBossWebCometWingServlet
        extends CometWingServlet implements HttpEventServlet {

    private void beginEvent(HttpEvent cometEvent) throws IOException, ServletException {
        HttpServletRequest request = cometEvent.getHttpServletRequest();
        HttpServletResponse response = cometEvent.getHttpServletResponse();

        log.debug("BEGIN: " + request.getSession());

        final Session session = getSession(request);
        if (session == null) {
            this.service(request, response);
            cometEvent.close();
            return;
        }

        final Comet comet = session.getComet();
        final Pushable pushable = comet.getPushable();
        final CometConnectionManager connectionManager = comet.getConnectionManager();

        final String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith(HANGING_PATH)) {
            if (connectionManager.addHangingGet()) {
            	log.debug("---addHangingGet: " + request.getSession() + "\n");
                synchronized (pushable) {
                    cometEvent.setTimeout((int)comet.getLongPollingTimeout());
                    pushable.setPushInfo(response);
                }
            } else {
            	log.debug("---switchToPeriodicPolling: " + request.getSession() + "\n");
                pushable.setPeriodicPolling(response);
                cometEvent.close();
            }
        } else {
            final String param = request.getParameter(PERIODIC_POLLING_PARAM);
            if (param != null) {
            	log.debug("---polling: " + request.getSession() + "\n");
                if (connectionManager.canAddHangingGet()) {
                	log.debug("---switchToHangingGet: " + request.getSession() + "\n");
                    synchronized (pushable) {
                        if (!pushable.isSwitchActive()) {
                            pushable.setSwitchActive(true);
                            pushable.switchToHanging();
                        }
                    }
                }
            }
            this.service(request, response);
            cometEvent.close();
        }
    }

    private void handleTimeout(HttpEvent cometEvent) throws IOException,
			ServletException {
		HttpServletRequest request = cometEvent.getHttpServletRequest();

		log.error("TIMEOUT: " + request.getSession() + "\n");

		final Pushable pushable = getPushable(request);
		if (pushable != null) {
			synchronized (pushable) {
				pushable.reset();
				pushable.reconnect();
			}
		}
		cometEvent.close();
	}

    private void endEvent(HttpEvent cometEvent) throws IOException, ServletException {
        HttpServletRequest request = cometEvent.getHttpServletRequest();

        log.debug("END: " + request.getSession() + "\n");
        final Pushable pushable = getPushable(request);

        if (pushable != null)
            pushable.reset();

        cometEvent.close();
    }

    public void event(HttpEvent cometEvent) throws IOException, ServletException {
        //cometEvent.setTimeout(TIMEOUT);
        if(cometEvent == null || cometEvent.getType() == null)
            return;
        
        switch(cometEvent.getType()) {
            case BEGIN:
                beginEvent(cometEvent);
            break;
            case TIMEOUT:
            	handleTimeout(cometEvent);
            break;
            case END:
                endEvent(cometEvent);
            break;
            default:
                break;
        }
    }

    private Pushable getPushable(HttpServletRequest request) throws ServletException {
        Session session = getSession(request);
        if (session == null)
          return null;
        Comet comet = session.getComet();
        if (comet == null)
          return null;
        return comet.getPushable();
    }
}
