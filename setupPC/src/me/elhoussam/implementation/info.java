package me.elhoussam.implementation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

public class info extends UnicastRemoteObject implements infoInterface {
	
	public info() throws RemoteException {}
	/*
	*	String String getter() : 
	*	public method To give the manager  
	*	information about the active pc, 
	*	and it represent the sevice in PC side.
	*/
	public String get(String property)  throws RemoteException{
		String str = System.getProperty(property);
		Tracking.info("info getter Triggered");
		return str ;
	}

}
