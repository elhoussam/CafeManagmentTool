import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import me.elhoussam.core.connection;
import me.elhoussam.implementation.info;
import me.elhoussam.interfaces.*;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.SecurityHandler;

public class PcEntryPoint {

	/*	void setupSecurityPolicy() : 
	*	static method that load the 
	*	security policy file and setup
	*	the security manager 
	*/
	private static void setupSecurityPolicy() throws Exception {
			String res = SecurityHandler.instance.LoadSecurityPolicy("")  ;
			Tracking.info(true,"Security State : "+ res ) ; 
	}
	/*	void providerWaiting() : 
	*	static method create the object
	*	the object which represent the service
	*	then start the LocalRegistry in server
	*	and finaly bind the service object with
	*	a public name in the localregistry
	*/	
	private static void providerWaiting() {
		try { 
			setupSecurityPolicy();
			String res =  SecurityHandler.myLocalIp() ;
			
			
			System.setProperty("java.rmi.server.hostname", res );
			Tracking.info(true,"Pc Ip Address : "+ res ) ;

		
			info  provideWait = new info();		
			LocateRegistry.createRegistry(1099);
			
			Naming.rebind("//"+res+"/pcWait", provideWait);
			Tracking.info(true,"Provider PC is ready."); 
		}catch (Exception e) {
			Tracking.error(true,"Provider PC failed: " + ExceptionHandler.getMessage(e) ); 
		}		
	}
	/*	void main(String[] args) :  
	*	this method call other methodes
	*	to construct the pieces of the app
	*/	
	
	public static void main (String[] args) {

		Tracking.setFolderName("PcApp",false);
		Tracking.info(true,"Start Pc Applicaion");
		// for java to use preferIp version = 4 
		//java.net.preferIPv4Stack
		System.setProperty("java.net.preferIPv6Addresses", "true");
		
		String ipManager = (args.length == 0)?"192.168.1.2":args[0];
		providerWaiting();
		Tracking.info(true,"ip of the manager "+ipManager);
		
		  
		connection.reachManager(ipManager);
		//connection.getThread().run();
		
	} 

}