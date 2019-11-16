package me.elhoussam.core;

import java.rmi.Naming;

import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;

public class connection {
  private static Boolean connCheckerStarted = false ;
  private static Boolean connNotifierStarted = false ;
  private static Boolean pcEliminatorStarted = false ;

  private static Thread connectionChecker = null;
  private static Thread connectionNotifier = null;
  private static Thread pcEliminator = null;

  public static Thread getNotifier() {
    return connectionNotifier ;
  } 
  public static void connChecker() { 
    if( ! connCheckerStarted ) {
      connectionChecker = new Thread("ConnectionChecker") {
        public void run(){ 
          try{  
            while( true ) {
              int listsize = Manager.get().size() ;
              if( listsize > 0 ) {
                String ip = "" ;
                for( short i = 0; i <listsize ; i++) {
                  ip = Manager.get().get(i).getIpAddress();
                  infoInterface localRef = connection.getRemoteObj(ip);
                  // if the ref is not NULL means the RemoteObject is ACTIVE
                  if( localRef != null ) {
                    Manager.get().get(i).setPcState(true);
                    Manager.get().get(i).setRef(localRef);
                    Tracking.info(true,"Connection Checker:"+ip+" connected" );
                  }else {
                    Manager.get().get(i).setPcState(false);
                    Manager.get().get(i).setRef(null);
                    Tracking.info(true,"Connection Checker:"+ip+" not connected" );
                  }
                }
              }
              Thread.sleep(3*60*1000);
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
  public static void eliminatorThread() { 
    if( ! connection.pcEliminatorStarted ) {
      connection.pcEliminator = new Thread("pcEliminator") {
        public void run(){ 
          try{  
            while( true ) {
              int listsize = Manager.get().size() ;
              if( listsize > 0 ) { 
                for( short i = 0; i <listsize ; i++) {
                  if( !Manager.get().get(i).getPcState() ) {
                    if ( Manager.get().get(i).getTimeFromLastConn() >= 3*60 ) {
                      Manager.get().remove(i);
                      Tracking.info(true, "Thread Eliminator remove the Pc("+i+")");
                    }
                    }
                  }
              }
              Thread.sleep(60*1000);
            }
          }catch (Exception e){ 
            Tracking.error(true,"Thread Eliminator Failed:" + ExceptionHandler.getMessage(e));
          } 
        } 
      }; 
      pcEliminator.start();
      pcEliminatorStarted = true ;
    }
  }
  public static void connNotifier() {
    if( ! connNotifierStarted ) {
      connectionNotifier = new Thread("Connection Notifier") {
        public void run() {
          try{ 
            // base on ip list 
            int listsize = Manager.get().size() -1 ;
            if( listsize >= 0 ) {

              String ip = Manager.get().get(listsize).getIpAddress();
              Manager.get().remove(listsize);
              int exist = Manager.indexOf(ip);

              Tracking.info(true,"Connection Notifier: New Conn ip: "+ip );
              if( exist == -1 ) { // does not exist 
                // add new pc element, and reInsert ip in the ipList
                Pc newConnectedPc = new Pc(ip); 
                newConnectedPc.setRef( getRemoteObj(ip) );
                newConnectedPc.updateLastconnection();
                Manager.get().add( newConnectedPc ); 
                Tracking.info(true,"Connection Notifier:"+ip+" is new for me" );
              }else { // exist meas update state and Date
                Manager.get().get(exist).setPcState(true);
                Manager.get().get(exist).updateLastconnection();
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
