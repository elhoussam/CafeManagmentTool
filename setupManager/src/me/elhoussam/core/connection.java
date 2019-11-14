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
									infoInterface localRef = connection.getRemoteObj(ip);
									// if the ref is not NULL means the RemoteObject is ACTIVE
									if( localRef != null ) {
										Manager.get().getListePcs().get(i).setPcState(true);
										Manager.get().getListePcs().get(i).setRef(localRef);
										Tracking.info(true,"Connection Checker:"+ip+" connected" );
									}else {
										Manager.get().getListePcs().get(i).setPcState(false);
										Manager.get().getListePcs().get(i).setRef(null);
										Tracking.info(true,"Connection Checker:"+ip+" not connected" );
									}
								}
							}
							Thread.sleep(60*1000);
						}
					}catch (Exception e){ 
						Tracking.error(true,"Thread Checker Failed:" + ExceptionHandler.getMessage(e));
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
				public void run() {
 					try{ 
						int listsize = Manager.get().getListeActivePc().size() -1 ;
						if( listsize >= 0 ) {

							String ip = Manager.get().getListeActivePc().get(listsize);
							Manager.get().getListeActivePc().remove(listsize);
							int exist = Manager.get().getListeActivePc().indexOf(ip);

							Tracking.info(true,"Connection Notifier: New Conn ip: "+ip );
							if( exist == -1 ) { // does not exist 
								// add new pc element, and reInsert ip in the ipList
								Pc newConnectedPc = new Pc();
								newConnectedPc.setIpAddress(ip);
								newConnectedPc.setRef( getRemoteObj(ip) );
								newConnectedPc.updateLastconnection();
								Manager.get().getListePcs().add( newConnectedPc );
								Manager.get().getListeActivePc().add(ip);
								Tracking.info(true,"Connection Notifier:"+ip+" is new for me" );
							}else { // exist meas update state and Date
								Manager.get().getListePcs().get(exist).setPcState(true);
								Manager.get().getListePcs().get(exist).updateLastconnection();
								Tracking.info(true,"Connection Notifier:"+ip+" is Aready listed" );
							}
							
						}else {
							Tracking.info(true,"Connection Notifier: Waiting");
						}
					}catch (Exception e) { 
						Tracking.error(true,"Thread Notifier Failed:" + ExceptionHandler.getMessage(e));
					} 
 				}
			}; 
			connectionNotifier.start();
			connNotifierStarted = true ;
		}
	}
	private static infoInterface getRemoteObj(String ipAddress) {
		try {

			String fullPath =  "//"+ ipAddress  +"/pcWait" ;

			infoInterface infoObj;
			infoObj = (infoInterface) Naming.lookup( fullPath );

			//Tracking.info(true,"Thread Checker lookup for "+fullPath);
			String result= infoObj.get("os.name");
			//Tracking.info(true,"Thread Checker get info :"+result+" from "+ip);
			return infoObj ;
		}catch (Exception e) {
			Tracking.error(true,"getRemoteObj ("+ipAddress+"):Not connected"  );
			return null;
			//ExceptionHandler.getMessage(e)
		}
	}
}
