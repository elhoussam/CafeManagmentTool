package me.elhoussam.core;

import java.rmi.Naming;

import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;

public class connection {
	private static Boolean connCheckerStarted = false ;
	private static Boolean connNotifierStarted = false ;
	
	private static Thread connectionChecker = null;
	private static Thread connectionNotifier = null;
	
	public static Thread getNotifier() {
		return connectionNotifier ;
	} 
	public static void connChecker() {
		if( ! connCheckerStarted ) {
			connectionChecker = new Thread("ConnectionChecker") {
				public void run(){ 
					try{  
						while( true ) {
							int listsize = Manager.get().getListeActivePc().size() ;
							if( listsize > 0 ) {
								try {
									String fullPath =  "//"+ 
									Manager.get().getListeActivePc().get(listsize-1) +"/pcWait" ;
		
									infoInterface infoObj;
									infoObj = (infoInterface) Naming.lookup( fullPath );
		
									Tracking.info("Thread Checker lookup for "+fullPath);
									String result= infoObj.getter();
									Tracking.info("Thread Checker get info :"+result);
									Thread.sleep(20*1000);
								}catch (Exception e) {
									Tracking.error("Thread Checker Failed:" + ExceptionHandler.getMessage(e));
								}
							}
						}
					}catch (Exception e){ 
						Tracking.error("Thread Checker Failed:" + ExceptionHandler.getMessage(e));
					} 
				} 
			}; 
			connectionChecker.start();
			connCheckerStarted = true ;
		}
	}
	public static void connNotifier() {
		if( ! connNotifierStarted ) {
			  connectionNotifier = new Thread("Connection Notifier") {
				public void run(){ 
					try{ 
						int listsize = Manager.get().getListeActivePc().size() -1 ;
						if( listsize >= 0 ) {
							String ip =  Manager.get().getListeActivePc().get( listsize );
							Tracking.info("Connection Notifier: New Conn ip: "+ip );
						}else {
							Tracking.info("Connection Notifier: Waiting");
						}
						
						/*int activePcs = 0; 
						while( true ) {
							if( activePcs !=  Manager.get().getListeActivePc().size() ) {
								
							}
							Thread.currentThread().interrupt();*/
							
								//controlPcs( Manager.get().getListeActivePc().get(activePcs-1) );
							//TimeUnit.SECONDS.sleep(60);
						
					}catch (Exception e) { 
						Tracking.error("Thread Notifier Failed:" + ExceptionHandler.getMessage(e));
					} 
				} 
			}; 
			connectionNotifier.start();
			connNotifierStarted = true ;
		}
	}
}
