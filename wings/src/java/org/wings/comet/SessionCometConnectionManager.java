package org.wings.comet;

public class SessionCometConnectionManager extends CometConnectionManager {

    private boolean hangingGetActive;

    public SessionCometConnectionManager() {
        hangingGetActive = getSharedObject();
    }

    public synchronized boolean canAddHangingGet() {
        return !hangingGetActive;
    }

    public synchronized boolean addHangingGet() {
        boolean result = !this.hangingGetActive;
        this.hangingGetActive = true;
        return result;
    }

    public synchronized void removeHangingGet() {
        this.hangingGetActive = false;
    }

    Boolean getSharedObject() {
        return false;
    }
}
