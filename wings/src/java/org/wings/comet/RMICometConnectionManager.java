package org.wings.comet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

class RMICometConnectionManager extends CometConnectionManager {

    private RemoteConnectionSet connectionSet;

    public RMICometConnectionManager() {
        connectionSet = getSharedObject();
    }

    public boolean isHangingGetActive() {
        synchronized (connectionSet) {
            try {
                return connectionSet.contains(browserId);
            } catch (RemoteException e) {
                //e.printStackTrace();
                connectionSet = getSharedObject();
                return isHangingGetActive();
            }
        }
    }

    public boolean hangingGetActive(boolean value) {
        synchronized (connectionSet) {
            try {
                final boolean oldValue = connectionSet.contains(browserId);
                if (value) {
                    connectionSet.add(browserId);
                } else {
                    connectionSet.remove(browserId);
                }
                return oldValue;
            } catch (RemoteException e) {
                //e.printStackTrace();
                connectionSet = getSharedObject();
                return hangingGetActive(value);
            }
        }
    }

    public void setHangingGetActive(boolean value) {
        synchronized (connectionSet) {
            try {
                if (value) {
                    connectionSet.add(browserId);
                } else {
                    connectionSet.remove(browserId);
                }
            } catch (RemoteException e) {
                //e.printStackTrace();
                connectionSet = getSharedObject();
                setHangingGetActive(value);
            }
        }
    }

    RemoteConnectionSet getSharedObject() {
        RemoteConnectionSet remoteConnectionSet = null;
        try {
            Registry registry = LocateRegistry.getRegistry();
            remoteConnectionSet = (RemoteConnectionSet) registry.lookup(NAME);
        } catch (RemoteException e) {
            //e.printStackTrace();
            remoteConnectionSet = new ConnectionSet();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return remoteConnectionSet;
    }
}
