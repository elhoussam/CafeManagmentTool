package me.elhoussam.interfaces;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import me.elhoussam.util.Tracking;

public class info extends UnicastRemoteObject
implements infoInterface {
	
	public info() throws RemoteException {}

	
	public String getter()  throws RemoteException{

		String str = System.getProperty("os.name");
		Tracking.info("info getter Triggered");
		return str ;
	}

}
