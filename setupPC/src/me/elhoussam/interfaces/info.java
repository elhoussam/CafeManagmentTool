package me.elhoussam.interfaces;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import me.elhoussam.util.log.Tracking;

public class info extends UnicastRemoteObject implements infoInterface {
	
	public info() throws RemoteException {}
	/*
	*	String String getter() : 
	*	public method To give the manager  
	*	information about the active pc, 
	*	and it represent the sevice in PC side.
	*/
	public String getter()  throws RemoteException{

		String str = System.getProperty("os.name");
		Tracking.info("info getter Triggered");
		return str ;
	}

}
