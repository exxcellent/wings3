package org.wings.comet;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface RemoteConnectionSet extends Remote {

    public boolean contains(Object o) throws RemoteException;

    public boolean add(String s) throws RemoteException;

    public boolean remove(Object o) throws RemoteException;

}
