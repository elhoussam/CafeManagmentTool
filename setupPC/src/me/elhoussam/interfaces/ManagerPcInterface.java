package me.elhoussam.interfaces;
import java.rmi.*;

public interface ManagerPcInterface extends Remote {	
	/*
	*	String NotifyAdmin(String myInfo) : 
	*	private method To inform the manager 
	*	of the arrival new Pcs, and send PcIp
	* 	using this method, is represent 
	*	the Only service the manager provoide.
	*/
	public String NotifyAdmin(String myInfo) throws RemoteException;
}
