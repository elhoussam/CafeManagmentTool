package me.elhoussam.core;

import java.rmi.Naming;

import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.SecurityHandler;

public class connection {
	private static Boolean threadStarted = false ; 
	private static Thread Notifier = null; 
	private static String ipOfManager = "";

	public static Thread getThread() {
		return Notifier ;
	}  
	public static void reachManager(String ip) {
		ipOfManager = ip ;
		if( ! threadStarted ) {
			Notifier = new Thread("Notifier") {
				public void run(){ 
					try {
						while( true ) {

							ManagerPcInterface obj = getRemoteObj( ipOfManager );

							// notifier manager every minute
							Thread.sleep(3*60*1000);
						}
					}catch (Exception e) {
							Tracking.error(true,"PC Notifier Failed:"  + ExceptionHandler.getMessage(e) );
					}
				} 
			}; 
			Notifier.start();
			threadStarted = true ;
		}
	}
	private static ManagerPcInterface getRemoteObj(String ipAddress) {
		try {

			Tracking.info(true,"getRemoteObj try to reach Manager ");
			ManagerPcInterface remoteObj;
			remoteObj = (ManagerPcInterface) Naming.lookup("//"+ ipAddress +"/ManagerWait");

			String res =  SecurityHandler.myLocalIp() ;
			String result= remoteObj.NotifyAdmin(res);
			Tracking.info(true,"getRemoteObj res ="+result);
			return remoteObj ;
		}catch (Exception e) {
			Tracking.error(true,"getRemoteObj ("+ipAddress+"):Not connected"  );
			return null;
			//ExceptionHandler.getMessage(e)
		}
	}

}
