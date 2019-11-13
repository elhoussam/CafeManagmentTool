package me.elhoussam.core;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import me.elhoussam.implementation.ActivePc;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.SecurityHandler;

public class Manager {
	private static ActivePc onlyObjectProvide = null;
 
	/*	void setupSecurityPolicy() : 
	*	static method that load the 
	*	security policy file and setup
	*	the security manager 
	*/
	

	private static void setupSecurityPolicy() throws Exception {
			String res = SecurityHandler.instance.LoadSecurityPolicy("")  ;
			Tracking.info(false,"Security State : "+ res ) ; 
	}	
	/*	void managerWaiting() : 
	*	static method create the object
	*	which represent the service then start 
	*	the LocalRegistry in server
	*	and finaly bind the service object with
	*	a public name in the localregistry
	*/	
	private static ActivePc managerWaiting() {
		try { 
			setupSecurityPolicy()  ;
			
			String res =  SecurityHandler.myLocalIp() ;
			// set server.hostname to IP_MANAGER
			System.setProperty("java.rmi.server.hostname", res );
			Tracking.info(false,"Manager Ip Address : "+ res ) ; 
						
			ActivePc  ManagerWait = new ActivePc();		
			LocateRegistry.createRegistry(1099);
			Naming.rebind("//"+res+"/ManagerWait", ManagerWait);

			Tracking.info(false,"Manager Server is ready.");
			return ManagerWait ;
		}catch (Exception e) {
			Tracking.error(false,"Manager App failed: " + ExceptionHandler.getMessage(e));
			return null; 
		}
	}
	/*
	 * return ActivePc object, the only 
	 * */
	public static ActivePc get() {
		return onlyObjectProvide;
	}
	/*	void main(String[] args) :  
	*	this method call other methodes
	*	to construct the pieces of the app
	*/	
	public static void start (){ 
		try {
			Tracking.setFolderName("ManagerApp",false);
			Tracking.info(false,"Start Manager Applicaion");
			//java.net.preferIPv6Addresses : to use only
			System.setProperty("java.net.preferIPv4Stack", "true");
			
			onlyObjectProvide = managerWaiting();
			
			// Lunch the Thread = (ConnNotifier)
			connection.connNotifier();

			Tracking.info(false,"Manager launch Notifier thread");
			// then launch the thread = connChecker
			connection.connChecker();

			Tracking.info(false,"Manager launch Checker thread");
			new cli();
		} catch (Exception e) {

			Tracking.error(false,"Manager start :" + ExceptionHandler.getMessage(e));
		}
	}	
}
