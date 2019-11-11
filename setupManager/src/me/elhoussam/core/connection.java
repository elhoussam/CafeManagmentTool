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
								String ip = "" ;
								for( short i = 0; i <listsize ; i++) {
									ip = Manager.get().getListeActivePc().get(i);
									try {

										String fullPath =  "//"+ ip  +"/pcWait" ;

										infoInterface infoObj;
										infoObj = (infoInterface) Naming.lookup( fullPath );

										//Tracking.info("Thread Checker lookup for "+fullPath);
										String result= infoObj.get("os.name");
										//Tracking.info("Thread Checker get info :"+result+" from "+ip);
									}catch (Exception e) {
										Tracking.error("Thread Checker ("+ip+"):Not connected"  );
										//ExceptionHandler.getMessage(e)
									}
								}
							}
							Thread.sleep(60*1000);
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
							//Tracking.info("Connection Notifier: New Conn ip: "+ip );
						}else {
							//Tracking.info("Connection Notifier: Waiting");
						}
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
