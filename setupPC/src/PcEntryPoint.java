import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import me.elhoussam.implementation.info;
import me.elhoussam.interfaces.*;
import me.elhoussam.provide.SecurityHandler;
import me.elhoussam.util.log.Tracking;

public class PcEntryPoint {
	/*	String myLocalIp() : 
	*	static method that return the ip of the machine 
	*	in the current network
	*/
	public static String myLocalIp() throws UnknownHostException, SocketException {
		String ip = "none";
		DatagramSocket socket = new DatagramSocket(); 
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
			String res = SecurityHandler.instance.LoadSecurityPolicy()  ;
			Tracking.info("Security State : "+ res ) ; 
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
			String res =  myLocalIp() ;
			
			
			System.setProperty("java.rmi.server.hostname", res );
			Tracking.info("Pc Ip Address : "+ res ) ;

		
			info  provideWait = new info();		
			LocateRegistry.createRegistry(1099);
			
			Naming.rebind("//"+res+"/pcWait", provideWait);
			Tracking.info("Provider PC is ready."); 
		}catch (Exception e) {
			Tracking.error("Provider PC failed: " + e); 
		}		
	}
	/*	void main(String[] args) :  
	*	this method call other methodes
	*	to construct the pieces of the app
	*/	
	
	public static void main (String[] args) {

		// for java to use preferIp version = 4 
		//java.net.preferIPv4Stack
		System.setProperty("java.net.preferIPv6Addresses", "true");
		
		
		Tracking.setFolderName("PcApp");
		providerWaiting();
		Tracking.info("ip of the manager"+args[0]);		
		reachManager(args[0]); // reach manager by his fixed LOCAL_IP
		
		
	}
	/*	void reachManager(String ipManager) :  
	*	this method try to connect to the ManagerComputer
	*	using the ipManager that giving by the user 
	*	to send the current ip of the PcComputer into
	*	ManagerComputer throws the remote object ActivePc 
	*/
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