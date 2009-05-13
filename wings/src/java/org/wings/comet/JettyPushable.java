package org.wings.comet;

import org.mortbay.util.ajax.Continuation;
import org.wings.session.Session;
import org.wings.session.LowLevelEventDispatcher;

import javax.servlet.http.HttpServletResponse;

class JettyPushable extends Pushable {

    private Continuation continuation = null;

    private HttpServletResponse response = null;
    private boolean pushInfoValid = true;

    public JettyPushable(Session session) {
        super(session);
    }

    void setPushInfo(HttpServletResponse res, Object o) {
        response = res;
        setPushInfo(o);
    }

    void setPushInfo(Object o) {
        continuation = (Continuation) o;

        setValid(true);
        setSwitchActive(false);

        final Comet comet = session.getComet();
        final LowLevelEventDispatcher dispatcher = session.getDispatcher();

        if (!comet.isCometActive()) {
            pushInfoValid = false;
            write(response, DISCONNECT);
            reset();
        }
        if (!dispatcher.isRunnablesListEmpty()) {
            pushInfoValid = false;
            write(response, REQUEST_UPDATES);
            reset();
        }
    }

    public void push() {
        setValid(false);
        continuation.setObject(REQUEST_UPDATES);
        continuation.resume();
    }

    void reconnect() {
        //setValid(false);
        continuation.setObject(RECONNECT);
        //continuation.resume();
    }

    void disconnect() {
        setValid(false);
        continuation.setObject(DISCONNECT);
        continuation.resume();
    }

    @Override
    void reset() {
        super.reset();
        pushInfoValid = true;
    }

    public boolean isPushInfoValid() {
        return pushInfoValid;
    }
}
