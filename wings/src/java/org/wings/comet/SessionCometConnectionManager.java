package org.wings.comet;

public class SessionCometConnectionManager extends CometConnectionManager {

    private boolean hangingGetActive;

    public SessionCometConnectionManager() {
        hangingGetActive = getSharedObject();
    }

    public synchronized boolean isHangingGetActive() {
        return hangingGetActive;
    }

    public synchronized boolean hangingGetActive(boolean value) {
        boolean oldValue = this.hangingGetActive;
        this.hangingGetActive = value;
        return oldValue;
    }

    public synchronized void setHangingGetActive(boolean value) {
        this.hangingGetActive = value;
    }

    Boolean getSharedObject() {
        return false;
    }
}
