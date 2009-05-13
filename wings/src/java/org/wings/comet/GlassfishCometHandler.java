package org.wings.comet;

import com.sun.enterprise.web.connector.grizzly.comet.CometEvent;
import com.sun.enterprise.web.connector.grizzly.comet.CometHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.resource.UpdateResource;
import org.wings.session.Session;
import org.wings.session.SessionManager;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class GlassfishCometHandler implements CometHandler<HttpServletResponse> {

    private static final transient Log log = LogFactory.getLog(GlassfishCometHandler.class);

    private final Session session;

    private HttpServletResponse response;

    public GlassfishCometHandler(Session session) {
        this.session = session;
    }

    public void attach(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    public void onEvent(CometEvent event) throws IOException {
        log.info("onEvent called");

        SessionManager.setSession(session);

        final Comet comet = session.getComet();
        final Pushable pushable = comet.getPushable();
        pushable.reset();
        pushable.write(response, event.attachment());

        event.getCometContext().resumeCometHandler(this);
    }

    public void onInitialize(CometEvent event) throws IOException {
        log.info("onInitialize called");
    }

    public void onTerminate(CometEvent event) throws IOException {
        log.info("onTerminate called");
    }

    public void onInterrupt(CometEvent event) throws IOException {
        log.info("onInterrupt called");

        SessionManager.setSession(session);

        final Comet comet = session.getComet();
        final Pushable pushable = comet.getPushable();
        synchronized (pushable) {
            pushable.reset();
            pushable.reconnect();
        }
    }
}
