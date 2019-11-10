package me.elhoussam.core;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

import me.elhoussam.implementation.ActivePc;
import me.elhoussam.interfaces.infoInterface;
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
			Tracking.info("Security State : "+ res ) ; 
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
			Tracking.info("Manager Ip Address : "+ res ) ; 
						
			ActivePc  ManagerWait = new ActivePc();		
			LocateRegistry.createRegistry(1099);
			Naming.rebind("//"+res+"/ManagerWait", ManagerWait);

			Tracking.info("Manager Server is ready.");
			return ManagerWait ;
		}catch (Exception e) {
			Tracking.error("Manager App failed: " + ExceptionHandler.getMessage(e));
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
			Tracking.setFolderName("ManagerApp");
			Tracking.info("Start Manager Applicaion");
			//java.net.preferIPv6Addresses : to use only
			System.setProperty("java.net.preferIPv4Stack", "true");
			
			onlyObjectProvide = managerWaiting();
			
			// Lunch the Thread = (ConnNotifier)
			connection.connNotifier();

			Tracking.info("Manager launch Notifier thread");
			// then launch the thread = connChecker
			
			
		} catch (Exception e) {

			Tracking.error("Manager start :" + ExceptionHandler.getMessage(e));
		}
		
		// limit for test purpose 
		/*int limit = 3 ;
		int activePcs = 0; 
	
		while( activePcs < limit ) {
			Tracking.info("Waiting for Pcs");
			activePcs = onlyObjectProvide.getListeActivePc().size();
			if( activePcs > 0 )
				controlPcs( onlyObjectProvide.getListeActivePc().get(activePcs-1) );
			TimeUnit.SECONDS.sleep(5);
		
		}*/
		//RegistryInspector(argv[1]);
		//controlPcs( managerWait.getListeActivePc().get(0) );  // request the Pc func as CLIENT
	}	
	
	/*	void controlPcs(String providerIp) : 
	*	static method that allow the Manager
	*	controls the connected Pcs, by lookup
	*	for the remote object that represent 
	*	the service in the other side (Pcs),
	* 	then gain the full acces according 
	*	to the available methode in the Remote Object.
	*/	
	private static void controlPcs(String providerIp) {
		try {

			String fullPath =  "//"+ providerIp +"/pcWait" ;

			infoInterface infoObj;
			infoObj = (infoInterface) Naming.lookup( fullPath );

			Tracking.info("Manager lookup for "+fullPath);
			String result= infoObj.getter();
			Tracking.info("Manager get info :"+result);
		}catch (Exception e) {
			Tracking.error("Manager get Failed:" + ExceptionHandler.getMessage(e));
		}
	}
	/*	void RegistryInspector(String  args) : 
	*	take String args represent 
	*	the Ip of the RmiRegistry 
	*	the list all names binded 
	*	to the rmiregistry server
	*/	
    public static void RegistryInspector(String  args) {
        String registry = args;
        try {
            String[] names = Naming.list("//"+registry+"/");
			Tracking.info( "Naming.list(" +registry+")" ) ;

            for (String name : names) {
                Remote remoteObject = Naming.lookup(name);
				Tracking.info( "Naming.lookup(" +name+")" ) ;

                if (remoteObject != null) {
                    System.out.println("name[" + name + "] class=" + remoteObject.getClass().getCanonicalName());
                } else {
                    System.out.println("name[" + name + "] is null.");
                }
            }
        } catch (Exception e) {
           Tracking.error( "RegistryInspector failed "+ ExceptionHandler.getMessage(e)) ;
        }
    }
}