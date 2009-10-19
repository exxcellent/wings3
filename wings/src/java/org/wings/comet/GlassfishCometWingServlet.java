package org.wings.comet;

import com.sun.enterprise.web.connector.grizzly.comet.CometContext;
import com.sun.enterprise.web.connector.grizzly.comet.CometEngine;
import org.wings.session.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class GlassfishCometWingServlet extends CometWingServlet {

    private CometContext context;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        CometEngine engine = CometEngine.getEngine();
        context = engine.register(HANGING_PATH);
        //context.setExpirationDelay(TIMEOUT);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Session session = getSession(request);
        if (session == null) {
            super.doGet(request, response);
            return;
        }

        final Comet comet = session.getComet();
        final Pushable pushable = comet.getPushable();
        final CometConnectionManager connectionManager = session.getComet().getConnectionManager();

        final String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith(HANGING_PATH)) {
            if (connectionManager.addHangingGet()) {
                synchronized (pushable) {
                    context.setExpirationDelay(comet.getLongPollingTimeout());
                    pushable.setPushInfo(response);
                }
            } else {
                pushable.setPeriodicPolling(response);
            }
        } else {
            final String param = request.getParameter(PERIODIC_POLLING_PARAM);
            if (param != null) {
                if (connectionManager.canAddHangingGet()) {
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
    }
}
