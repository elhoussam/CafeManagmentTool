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

public class ManagerEntryPoint {
	/*	String myLocalIp() : 
	*	static method that return the ip of the machine 
	*	in the current network
	*/	
	public static String myLocalIp() throws UnknownHostException, SocketException {
		//java.net.Authenticator  -- .preferIPv4Stack=true
		String ip = "none";
		DatagramSocket socket = new DatagramSocket(); 
		// by using any ip address and any port, then return the current ip
		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
		ip = socket.getLocalAddress().getHostAddress();
		return ip ;
	}
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
			

			String res =  myLocalIp() ;
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
	/*	void main(String[] args) :  
	*	this method call other methodes
	*	to construct the pieces of the app
	*/	
	public static void main (String[] argv) throws InterruptedException { 
		Tracking.setFolderName("ManagerApp");
		Tracking.info("Start Manager Applicaion");
		// for java to use preferIp version = 4 
		//java.net.preferIPv6Addresses : to use only
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		
		

		
		ActivePc  managerWait = managerWaiting();
		// limit for test purpose 
		int limit = (int)( ( argv.length == 0 )?1:Integer.valueOf( argv[0] )) ;
		int activePcs = 0; 
	
		while( activePcs < limit ) {
			Tracking.info("Waiting for Pcs");
			activePcs = managerWait.getListeActivePc().size();
			if( activePcs > 0 )
				controlPcs( managerWait.getListeActivePc().get(activePcs-1) );
			TimeUnit.SECONDS.sleep(5);
		
		}
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
