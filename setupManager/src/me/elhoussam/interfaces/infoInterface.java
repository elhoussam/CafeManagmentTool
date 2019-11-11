package me.elhoussam.interfaces;

import java.rmi.RemoteException;

public interface infoInterface extends java.rmi.Remote {
	/*
	*	String String getter() : 
	*	public method To give the manager  
	*	information about the active pc, 
	*	and it represent the sevice in PC side.
	*/
	public String get(String property) throws RemoteException;
}
