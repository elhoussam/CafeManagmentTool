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
	private static void setupSecurityPolicy() throws Exception {
			String res = SecurityHandler.instance.LoadSecurityPolicy()  ;
			Tracking.info("Security State : "+ res ) ; 
	}	
	private static void providerWaiting() {
		try { 
			setupSecurityPolicy();
			
			String res =  myLocalIp() ;
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
		providerWaiting();
		Tracking.info("ip of the manager"+args[0]);		
		reachManager(args[0]); // reach manager by his fixed LOCAL_IP
		
		
	}
	


	private static void reachManager(String ipManager) {
		try { 
			
			ActivePcInterface activePcObj;
			activePcObj = (ActivePcInterface) Naming.lookup("//"+ ipManager +"/ManagerWait");
			
			String res =  myLocalIp() ;
			String result= activePcObj.NotifyAdmin(res);
			Tracking.info("PC Active :"+result);
		}catch (Exception e) {
 
			Tracking.error("PC App Failed:" + e);
		}
	}

}