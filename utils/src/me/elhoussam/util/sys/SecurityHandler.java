package me.elhoussam.util.sys; 
import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import me.elhoussam.util.log.Tracking;
public class SecurityHandler {
	/*
	 * Security Class Signleton PD that handle loading the Policy file 
	 * and apply it set the security manager
	 * */
	private String securityPolicyPath = "config/security.policy" ;
	public static SecurityHandler instance = new SecurityHandler(); 
	private SecurityHandler() {}
	/*
	 * InitSecurityPolicy() method load the security policy
	 * by adding security.policy property into the system 
	 * */
	private String InitSecurityPolicy(String securityPolicy) {
		//setSecurityPolicyPath(securityPolicy);
		try {
			//getClass().getClassLoader();
			// this used to load the file from resourceFolder in side jar file
			//String DynamicPath =  getClass().getClassLoader().getResource( securityPolicyPath ).toString();
			
			Tracking.echo("after Dynamic file :::: ");   
			String DynamicPath =  Paths.get(".").toAbsolutePath().normalize().toString() +"\\sec.policy" ;
			Tracking.echo("after Dynamic file :::: "+ DynamicPath );
			File policyfile = new File(DynamicPath); 
			if( !(policyfile.exists()) )
				DynamicPath =  getClass().getClassLoader().getResource( securityPolicyPath ).toString(); 
			
			
			System.setProperty( "java.security.policy",DynamicPath );
			Tracking.info("The Security policy is initialized");
			return System.getProperty("java.security.policy");
		}catch( Exception e) {
			Tracking.error("The Security policy does not initialized:"+ ExceptionHandler.getMessage(e));	
			return null ;
		}
		 
	}	
	private void setSecurityPolicyPath(String securityPolicy) {
		if( ! securityPolicy.trim().isEmpty() )
			securityPolicyPath = securityPolicy;
	}
	/*
	 * LoadSecurityPolicy() method set the security Manger after loading the Security file
	 * */
	public String LoadSecurityPolicy(String policyPath) {
		try {
			String res = InitSecurityPolicy(policyPath);
			if( !res.isEmpty() ) System.setSecurityManager(new SecurityManager()) ;

			Tracking.info("The Security policy setup");
			return res;
		}catch(Exception e ) {
			Tracking.error("The Security policy cant setup:"+ ExceptionHandler.getMessage(e));
			return null ;
		}
	}
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
}
