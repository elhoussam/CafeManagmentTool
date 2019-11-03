package me.elhoussam.interfaces;



import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ActivePcInterface extends Remote {
	public String NotifyAdmin(String myInfo) throws RemoteException;
}
