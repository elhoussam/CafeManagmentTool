package me.elhoussam.interfaces;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import me.elhoussam.util.Tracking;
 
public class ActivePc extends UnicastRemoteObject
implements ActivePcInterface  {
	
	private ArrayList<String> IpOfPcs = new ArrayList<String>();

	
	public ArrayList<String> getListeActivePc() {
		return IpOfPcs;
	}
	
	private void addNewIp(String ipNewActivePc ) {
		IpOfPcs.add(ipNewActivePc);
	}
	
	public ActivePc() throws RemoteException {}

	public String NotifyAdmin(String myInfo){
		try {
		
		if( !myInfo.trim().isEmpty() ) {
			String ipOfPc =  myInfo.trim();
			addNewIp(ipOfPc);
			Tracking.info("the ("+ipOfPc+") was recieved");
			return "msg is recieved\n";
		}else {

			Tracking.warning("the msg was badly received");
			return "msg was not delivered\n";
		}
		}catch(Exception e ) {
			Tracking.error("some thing happened:"+e);
			return null ;
		}
		
	}
	
}
