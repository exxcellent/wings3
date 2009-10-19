package org.wings.comet;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;
import org.wings.session.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class TomcatCometWingServlet
        extends CometWingServlet implements CometProcessor {

    private void beginEvent(CometEvent cometEvent) throws IOException, ServletException {
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

    private void handleError(CometEvent cometEvent) throws IOException, ServletException {
        HttpServletRequest request = cometEvent.getHttpServletRequest();

        log.error("ERROR: " + request.getSession() + "\n");

        if(cometEvent.getEventSubType() == null) {
            cometEvent.close();
            return;
        }

        switch(cometEvent.getEventSubType()) {
            case TIMEOUT:
                log.debug("---Timeout: " + request.getSession() + "\n");

                final Pushable pushable = getPushable(request);
                if (pushable != null) {
                    synchronized (pushable) {
                        pushable.reset();
                        pushable.reconnect();
                    }
                }
            break;
            case CLIENT_DISCONNECT:
                log.debug("---Client_Disconnect: " + request.getSession() + "\n");
            break;
            case IOEXCEPTION:
                log.debug("---IOException: " + request.getSession() + "\n");
            break;
        }

        cometEvent.close();
    }

    private void endEvent(CometEvent cometEvent) throws IOException, ServletException {
        HttpServletRequest request = cometEvent.getHttpServletRequest();

        log.debug("END: " + request.getSession() + "\n");
        final Pushable pushable = getPushable(request);

        if(cometEvent.getEventSubType() == null) {
            if (pushable != null)
                pushable.reset();
            cometEvent.close();
            return;
        }

        switch(cometEvent.getEventSubType()) {
            case WEBAPP_RELOAD:
                log.debug("---Webapp_Reload: " + request.getSession() + "\n");
                break;
            case SESSION_END:
                log.debug("---Session_End: " + request.getSession() + "\n");
                break;
            case SERVER_SHUTDOWN:
                log.debug("---Server_Shutdown: " + request.getSession() + "\n");
                break;
        }

        if (pushable != null)
            pushable.reset();

        cometEvent.close();
    }

    public void event(CometEvent cometEvent) throws IOException, ServletException {
        //cometEvent.setTimeout(TIMEOUT);
        if(cometEvent == null || cometEvent.getEventType() == null)
            return;
        
        switch(cometEvent.getEventType()) {
            case BEGIN:
                beginEvent(cometEvent);
            break;
            case ERROR:
                handleError(cometEvent);
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
