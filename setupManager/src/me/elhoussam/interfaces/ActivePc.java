package me.elhoussam.interfaces;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import me.elhoussam.util.Tracking;
 
public class ActivePc extends UnicastRemoteObject
implements ActivePcInterface  {
	/*
	*	ArrayList IpOfPcs  contain all the ip of connect pcs
	*/
	private ArrayList<String> IpOfPcs = new ArrayList<String>();
	/*
	*	ArrayList<String> getListeActivePc() : 
	*	public method return the Array of all ip
	*/
	public ArrayList<String> getListeActivePc() {
		return IpOfPcs;
	}
	/*
	*	addNewIp(String ipNewActivePc) : 
	*	public method return the Array of all ip
	*/
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
