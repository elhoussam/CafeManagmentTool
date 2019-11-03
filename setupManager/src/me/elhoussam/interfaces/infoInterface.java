package me.elhoussam.interfaces;

import java.rmi.RemoteException;

public interface infoInterface extends java.rmi.Remote {
	public String getter()  throws RemoteException;
}
