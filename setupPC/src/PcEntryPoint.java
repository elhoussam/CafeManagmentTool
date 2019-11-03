import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import me.elhoussam.interfaces.*;
import me.elhoussam.provide.SecurityHandler;
import me.elhoussam.util.Tracking;

public class PcEntryPoint {
	public static String myLocalIp() throws UnknownHostException, SocketException {
		String ip = "none";
		DatagramSocket socket = new DatagramSocket(); 
		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
		ip = socket.getLocalAddress().getHostAddress();
		return ip ;
	}
	
	private static void providerWaiting() {
		try { 
			String res =  SecurityHandler.instance.LoadSecurityPolicy() ;
			Tracking.info("Security State : "+ res ) ; 
			
			res =  myLocalIp() ;
			System.setProperty("java.rmi.server.hostname", res );
			Tracking.info("Pc Ip Address : "+ res ) ;
			
			info  provideWait = new info();		
			LocateRegistry.createRegistry(1099);
			// rmiregistry aready running
			Naming.rebind("//"+res+"/pcWait", provideWait);

			Tracking.info("Provider PC is ready."); 
		}catch (Exception e) {
			Tracking.error("Provider PC failed: " + e); 
		}		
	}
	
	public static void main (String[] args) {
		
		Tracking.setFolderName("PcApp");
		
		Tracking.info("ip of the manager"+args[0]);		
		reachManager(args[0]); // reach manager by his fixed LOCAL_IP
		
		providerWaiting();
	}
	


	private static void reachManager(String ipManager) {
		try {
			 
			String res = SecurityHandler.instance.LoadSecurityPolicy()  ;
			Tracking.info("Security State : "+ res ) ; 
			
			ActivePcInterface activePcObj;
			activePcObj = (ActivePcInterface) Naming.lookup("//"+ ipManager +"/ManagerWait");
			
			res =  myLocalIp() ;
			String result= activePcObj.NotifyAdmin(res);
			Tracking.info("PC Active :"+result);
		}catch (Exception e) {
 
			Tracking.error("PC App Failed:" + e);
		}
	}

}