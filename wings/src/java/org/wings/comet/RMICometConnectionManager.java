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

    public boolean canAddHangingGet() {
        synchronized (connectionSet) {
            try {
                return !connectionSet.contains(getBrowserId());
            } catch (RemoteException e) {
                //e.printStackTrace();
                connectionSet = getSharedObject();
                return canAddHangingGet();
            }
        }
    }

    public boolean addHangingGet() {
        synchronized (connectionSet) {
            try {
                final boolean result = !connectionSet.contains(getBrowserId());
                connectionSet.add(getBrowserId());
                return result;
            } catch (RemoteException e) {
                //e.printStackTrace();
                connectionSet = getSharedObject();
                return addHangingGet();
            }
        }
    }

    public void removeHangingGet() {
        synchronized (connectionSet) {
            try {
                connectionSet.remove(getBrowserId());
            } catch (RemoteException e) {
                //e.printStackTrace();
                connectionSet = getSharedObject();
                removeHangingGet();
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
