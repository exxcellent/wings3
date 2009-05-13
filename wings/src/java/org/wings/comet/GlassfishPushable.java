package org.wings.comet;

import com.sun.enterprise.web.connector.grizzly.comet.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.session.Session;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class GlassfishPushable extends Pushable {
   
    private static final transient Log log = LogFactory.getLog(GlassfishPushable.class);

    private final GlassfishCometHandler handler;

    private int handlerID;

    public GlassfishPushable(Session session) {
        super(session);
        handler = new GlassfishCometHandler(session);
    }

    void setPushInfo(Object o) {
        log.info("setting push info");
        handler.attach((HttpServletResponse)o);
        CometEngine engine = CometEngine.getEngine();
        CometContext context = engine.getCometContext(CometWingServlet.HANGING_PATH);
        handlerID = context.addCometHandler(handler);
        super.setPushInfo(this);
    }

    public void push() {
        log.info("push called in Pushable");
        setValid(false);
        notifyHandler(REQUEST_UPDATES);
    }

    void reconnect() {
        log.info("reconnect called in Pushable");
        //setValid(false);
        //notifyHandler(RECONNECT);
        write(handler.getHttpServletResponse(), RECONNECT);
    }

    void disconnect() {
        log.info("disconnect called in Pushable");
        setValid(false);
        notifyHandler(DISCONNECT);
    }

    private void notifyHandler(Object o) {
        CometEngine engine = CometEngine.getEngine();
        CometContext context = engine.getCometContext(CometWingServlet.HANGING_PATH);
        try {
            context.notify(o, CometEvent.NOTIFY, handlerID);
        } catch (IOException e) {
            e.printStackTrace();
            reset();
        }
    }
}
