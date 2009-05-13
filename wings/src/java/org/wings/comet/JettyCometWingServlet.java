package org.wings.comet;

import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;
import org.wings.session.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class JettyCometWingServlet extends CometWingServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Session session = getSession(request);
        if (session == null) {
            super.doGet(request, response);
            return;
        }

        final Comet comet = session.getComet();
        final JettyPushable pushable = (JettyPushable)comet.getPushable();
        final CometConnectionManager connectionManager = comet.getConnectionManager();

        final String pathInfo = request.getPathInfo();

        if (connectionManager != null) {
            if (HANGING_PATH.equals(pathInfo)) {
                final Continuation continuation = ContinuationSupport.getContinuation(request, null);
                boolean resumed;
                synchronized (pushable) {
                    if (!continuation.isPending()) {
                        if (!connectionManager.hangingGetActive(true)) {
                            pushable.setPushInfo(response, continuation);
                            if (!pushable.isPushInfoValid()) return;
                        } else {
                            pushable.setPeriodicPolling(response);
                            return;
                        }
                    }
                    resumed = continuation.suspend(comet.getLongPollingTimeout());
                }

                if (!resumed) {
                    pushable.reconnect();
                }
                pushable.write(response, continuation.getObject());
                pushable.reset();
            } else {
                final String param = request.getParameter(PERIODIC_POLLING_PARAM);
                if (param != null) {
                    if (!connectionManager.isHangingGetActive()) {
                        synchronized (pushable) {
                            if (!pushable.isSwitchActive()) {
                                pushable.setSwitchActive(true);
                                pushable.switchToHanging();
                            }
                        }
                    }
                }
                super.doGet(request, response);
            }    
        } else {
            if (HANGING_PATH.equals(pathInfo)) {
                final Continuation continuation = ContinuationSupport.getContinuation(request, null);
                boolean resumed;
                synchronized (pushable) {
                    if (!continuation.isPending()) {
                        pushable.setPushInfo(response, continuation);
                        if (!pushable.isPushInfoValid()) return;
                    }
                    resumed = continuation.suspend(comet.getLongPollingTimeout());
                }

                if (!resumed) {
                    pushable.reconnect();
                }
                pushable.write(response, continuation.getObject());
                pushable.reset();
            } else {
                super.doGet(request, response);
            }
        }
    }
}
