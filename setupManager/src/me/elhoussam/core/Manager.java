package me.elhoussam.core;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import me.elhoussam.implementation.ManagerPc;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.SecurityHandler;

public class Manager {
	private static ManagerPc onlyObjectProvide = null;
 
	/*	void setupSecurityPolicy() : 
	*	static method that load the 
	*	security policy file and setup
	*	the security manager 
	*/
	

	private static void setupSecurityPolicy() throws Exception {
			String res = SecurityHandler.instance.LoadSecurityPolicy("")  ;
			Tracking.info(true,"Security State : "+ res ) ; 
	}	
	/*	void managerWaiting() : 
	*	static method create the object
	*	which represent the service then start 
	*	the LocalRegistry in server
	*	and finaly bind the service object with
	*	a public name in the localregistry
	*/	
	private static ManagerPc managerWaiting() {
		try { 
			setupSecurityPolicy()  ;
			
			String res =  SecurityHandler.myLocalIp() ;
			// set server.hostname to IP_MANAGER
			System.setProperty("java.rmi.server.hostname", res );
			Tracking.info(true,"Manager Ip Address : "+ res ) ; 
						
			ManagerPc  ManagerWait = new ManagerPc();		
			LocateRegistry.createRegistry(1099);
			Naming.rebind("//"+res+"/ManagerWait", ManagerWait);

			Tracking.info(true,"Manager Server is ready.");
			return ManagerWait ;
		}catch (Exception e) {
			Tracking.error(true,"Manager App failed: " + ExceptionHandler.getMessage(e));
			return null; 
		}
	}
	/*
	 * return ManagerPc object, the only 
	 * */
	public static ManagerPc get() {
		return onlyObjectProvide;
	}
	/*	void main(String[] args) :  
	*	this method call other methodes
	*	to construct the pieces of the app
	*/	
	public static void start (){ 
		try {
			Tracking.setFolderName("ManagerApp",false);
			Tracking.info(true,"Start Manager Applicaion");
			//java.net.preferIPv6Addresses : to use only
			System.setProperty("java.net.preferIPv4Stack", "true");
			
			onlyObjectProvide = managerWaiting();
			
			// Lunch the Thread = (ConnNotifier)
			connection.connNotifier();

			Tracking.info(true,"Manager launch Notifier thread");
			// then launch the thread = connChecker
			connection.connChecker();

			Tracking.info(true,"Manager launch Checker thread");
			// new cli(); // Launch Command Ligne Interface
		} catch (Exception e) {

			Tracking.error(true,"Manager start :" + ExceptionHandler.getMessage(e));
		}
	}	
}
