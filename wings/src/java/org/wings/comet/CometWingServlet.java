package org.wings.comet;

import org.wings.session.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;

public class CometWingServlet extends WingServlet {

    //static final int TIMEOUT = 1000*60*60*24;

    static final String HANGING_PATH = "/hanging";
    static final String PERIODIC_POLLING_PARAM = "polling";

    Session getSession(HttpServletRequest request) throws ServletException {
        Session session = null;
        final SessionServlet sessionServlet = getSessionServlet(request, null, false);
        if (sessionServlet != null) {
            session = sessionServlet.getSession();
            SessionManager.setSession(session);
        }
        return session;
    }

    /*
    Pushable getPushable(Session session) {
        final Comet comet = session.getComet();
        Pushable pushable = comet.getPushable();
        if (pushable == null) {
            if (this instanceof JettyCometWingServlet) pushable = new JettyPushable(session);
                else if (this instanceof GlassfishCometWingServlet) pushable = new GlassfishPushable(session);
                    else if (this instanceof TomcatCometWingServlet) pushable = new TomcatPushable(session);
            comet.setPushable(pushable);
        }
        return pushable;
    }
    */
}
