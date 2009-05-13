package org.wings.comet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashSet;

class ConnectionSet extends HashSet<String> implements RemoteConnectionSet {

    public ConnectionSet() {
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            RemoteConnectionSet stub = (RemoteConnectionSet) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(CometConnectionManager.NAME, stub);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
