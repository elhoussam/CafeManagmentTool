import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.TimeUnit;

import me.elhoussam.core.SecurityHandler;
import me.elhoussam.interfaces.ActivePc;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.Tracking;

public class ManagerEntryPoint {	
	public static String myLocalIp() throws UnknownHostException, SocketException {
		String ip = "none";
		DatagramSocket socket = new DatagramSocket(); 
		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
		ip = socket.getLocalAddress().getHostAddress();
		return ip ;
	}
	
	private static void setupSecurityPolicy() throws Exception {
			String res = SecurityHandler.instance.LoadSecurityPolicy()  ;
			Tracking.info("Security State : "+ res ) ; 
	}
	private static ActivePc managerWaiting() {
		try { 
			setupSecurityPolicy()  ;
			
			String res =  myLocalIp() ;
			System.setProperty("java.rmi.server.hostname", res );
			Tracking.info("Manager Ip Address : "+ res ) ;
			
						
			ActivePc  ManagerWait = new ActivePc();		
			LocateRegistry.createRegistry(1099);
			Naming.rebind("//"+res+"/ManagerWait", ManagerWait);

			Tracking.info("Manager Server is ready.");
			return ManagerWait ;
		}catch (Exception e) {
			Tracking.error("Manager App failed: " + e);
			return null; 
		}
	}

	public static void main (String[] argv) throws InterruptedException { 
		Tracking.setFolderName("ManagerApp");
		
		ActivePc  managerWait = managerWaiting();
		// limit for test purpose 
		int limit = (int)( ( argv.length == 0 )?1:Integer.valueOf( argv[0] )) ;
		int activePcs = 0; 
	
		while( activePcs < limit ) {
			Tracking.info("Waiting for Pcs");
			activePcs = managerWait.getListeActivePc().size();
			if( activePcs > 0 )
				controlPcs( managerWait.getListeActivePc().get(activePcs-1) );
			TimeUnit.SECONDS.sleep(20);
		
		}
		//RegistryInspector(argv[1]);
		//controlPcs( managerWait.getListeActivePc().get(0) );  // request the Pc func as CLIENT
	}

	private static void controlPcs(String providerIp) {
		try {

			String fullPath =  "//"+ providerIp +"/pcWait" ;

			infoInterface infoObj;
			infoObj = (infoInterface) Naming.lookup( fullPath );

			Tracking.info("Manager lookup for "+fullPath);
			String result= infoObj.getter();
			Tracking.info("Manager get info :"+result);
		}catch (Exception e) {
			Tracking.error("Manager get Failed:" + e.getStackTrace());
		}
	}

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
           Tracking.error( "RegistryInspector failed "+e) ;
        }
    }
}
