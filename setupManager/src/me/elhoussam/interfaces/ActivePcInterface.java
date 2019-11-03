package me.elhoussam.interfaces;
import java.rmi.*;

public interface ActivePcInterface extends Remote {
	public String NotifyAdmin(String myInfo) throws RemoteException;
}
