package org.wings.comet;

import org.wings.io.ServletDevice;
import org.wings.resource.UpdateResource;
import org.wings.session.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Pushable {

    private static final String REQUEST_UPDATES_STRING = "wingS.comet.requestUpdates()";
    private static final String RECONNECT_STRING = "wingS.comet.connect()";
    private static final String DISCONNECT_STRING = "wingS.comet.disconnect()";

    private static final String PERIODIC_POLLING_STRING = "wingS.comet.periodicPolling";

    static final int REQUEST_UPDATES = 0;
    static final int RECONNECT = 1;
    static final int DISCONNECT = 2;

    final ServletDevice out;
    final Session session;
    //private final Comet comet;

    //private Timer timer = null;
    private boolean valid = false;
    private boolean switchActive = false;

    public Pushable(Session session) {
        this.session = session;
        out = new ServletDevice(session.getCharacterEncoding());
        //this.comet = session.getComet();
    }

    public boolean isValid() {
        return valid;
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }

    boolean isSwitchActive() {
        return switchActive;
    }

    void setSwitchActive(boolean switchActive) {
        this.switchActive = switchActive;
    }

    /*
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        Task task = new Task(SessionManager.getSession(), this);
        long timeout = comet.getLongPollingTimeout();
        if (timeout > 0) {
            timer.schedule(task, timeout);
        }
    }
    */

    void write(HttpServletResponse response, Object o) {
        Session temp = SessionManager.getSession();
        SessionManager.setSession(session);
        response.setContentType("text/xml; charset=" + SessionManager.getSession().getCharacterEncoding());
        try {
            final ServletOutputStream servletOutputStream = response.getOutputStream();
            out.setServletOutputStream(servletOutputStream);
            UpdateResource.writeHeader(out);
            switch ((Integer)o) {
                case REQUEST_UPDATES: UpdateResource.writeUpdate(out, REQUEST_UPDATES_STRING); break;
                case RECONNECT: UpdateResource.writeUpdate(out, RECONNECT_STRING); break;
                case DISCONNECT: UpdateResource.writeUpdate(out, DISCONNECT_STRING);
            }
            UpdateResource.writeFooter(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SessionManager.setSession(temp);
        }
    }

    void setPeriodicPolling(HttpServletResponse response) {
        response.setContentType("text/xml; charset=" + SessionManager.getSession().getCharacterEncoding());
        try {
            final ServletOutputStream servletOutputStream = response.getOutputStream();
            final ServletDevice out = new ServletDevice(servletOutputStream, session.getCharacterEncoding());
            final Comet comet = session.getComet();
            UpdateResource.writeHeader(out);
            UpdateResource.writeUpdate(out, PERIODIC_POLLING_STRING + "(" + comet.getPeriodicPollingInterval() + ")");
            UpdateResource.writeFooter(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            }
    }

    void switchToHanging() {
        final Comet comet = session.getComet();
        comet.getBindedFrame().setCometUpdate(Comet.COMET_UPDATE_HANGING);
    }

    void reset() {
        final Comet comet = session.getComet();
        final CometConnectionManager connectionManager = comet.getConnectionManager();
        if (connectionManager != null) connectionManager.setHangingGetActive(false);
        setValid(false);
        //timer.cancel();
    }

    //-----abstract methods---------------------
    abstract void setPushInfo(Object o);
    public abstract void push();
    abstract void reconnect();
    abstract void disconnect();

    void setPushInfo(Pushable pushable) {
        final Comet comet = session.getComet();
        final LowLevelEventDispatcher dispatcher = session.getDispatcher();
        if (!comet.isCometActive()) {
            pushable.disconnect();
            return;
        }
        if (!dispatcher.isRunnablesListEmpty()) {
            pushable.push();
            return;
        }
        //startTimer();
        valid = true;
        switchActive = false;
    }

    /*
    class Task extends TimerTask {

        private final Session session;
        private final Pushable pushable;

        public Task(Session session, Pushable pushable) {
            this.session = session;
            this.pushable = pushable;
        }

        public void run() {
            SessionManager.setSession(session);
            synchronized(pushable) {
                if (pushable.isValid()) {
                    pushable.reconnect();
                }
            }
        }
    }
    */
}
