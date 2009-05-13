package org.wings.comet;

import org.wings.resource.UpdateResource;
import org.wings.session.Session;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class TomcatPushable extends Pushable {

    HttpServletResponse response = null;

    public TomcatPushable(Session session) {
        super(session);
    }

    void setPushInfo(Object o) {
        response = (HttpServletResponse) o;
        super.setPushInfo(this);
    }

    public void push() {
        setValid(false);
        write(response, REQUEST_UPDATES);
    }

    void reconnect() {
        //setValid(false);
        write(response, RECONNECT);
    }

    void disconnect() {
        setValid(false);
        write(response, DISCONNECT);
    }

    /*
    public void push() {
        try {
            PrintWriter writer = response.getWriter();
            writer.println("<update>wingS.ajax.requestUpdates()</update>");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
