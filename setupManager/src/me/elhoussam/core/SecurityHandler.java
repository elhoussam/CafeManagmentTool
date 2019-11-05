package me.elhoussam.core;

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
	private String InitSecurityPolicy() {
		try {
			
			String DynamicPath =  getClass().getClassLoader().getResource( securityPolicyPath ).toString();
			if ( DynamicPath.isEmpty() ) {return DynamicPath ;}
			System.setProperty( "java.security.policy",DynamicPath );
			Tracking.info("The Security policy is initialized");
			return System.getProperty( "java.security.policy");
		}catch( Exception e) {
			Tracking.error("The Security policy does not initialized:"+e);	
			return null ;
		}
		 
	}	
	/*
	 * LoadSecurityPolicy() method set the security Manger after loading the Security file
	 * */
	public String LoadSecurityPolicy() {
		try {
			String res = InitSecurityPolicy();
			if( !res.isEmpty() ) System.setSecurityManager(new SecurityManager()) ;

			Tracking.info("The Security policy setup");
			return res;
		}catch(Exception e ) {
			Tracking.error("The Security policy cant setup:"+e);
			return null ;
		}
	}
}
