package me.elhoussam.core;

import java.rmi.Naming;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.PropertyHandler;
import me.elhoussam.util.sys.TimeHandler;

public class Connection {
  /*
   * logical variable to insure the threads was created just ones
   * */
  private Boolean connCheckerStarted = false;
  private Boolean pcEliminatorStarted = false;
  private Boolean connNotifierStarted = false ;
  private static Boolean launchThreads = false ;
  private static Connection obj = null ;
  /*
   * connCheckerStarted  : thread check Connection with all connected pcs
   * connNotifierStarted : thread recieve the new Connection from pcs
   * pcEliminatorStarted : thread remove all pcs that disconnected
   * */
  private Thread connectionChecker = null;
  private Thread connectionNotifier = null;
  private Thread pcEliminator = null;
  private int threadPeriodInSecond = 120;

  public static byte launchThreads() {
    if( !Connection.launchThreads ) {
      obj = new Connection();
      Connection.launchThreads = true ;
      return obj.init() ;
    }
    return 0;
  }
  /*
   * setup the env for the thread
   * */
  public byte init() {
    // read property file
    threadPeriodInSecond = getValue("thread.period");
    // Lunch the Thread = (ConnNotifier)
    connNotifier();
    Tracking.info(true, "Connection launch Notifier thread");

    // then launch the thread = connChecker
    connChecker();
    Tracking.info(true, "Manager launch Checker thread");

    // then launch the thread = Eliminator
    eliminatorThread();
    Tracking.info(true, "Manager launch Eliminator thread");
    return 1;

  }

  public static Thread getNotifier() {
    return  obj.connectionNotifier;
  }
  /*
   * read properties from external config file if exist
   * */
  private int getValue(String str) {
    PropertyHandler ph = new PropertyHandler("");
    String s = ph.getPropetry(str); // "thread.period"
    if (s != null)
      return Integer.valueOf(s);
    else
      return -1;
  }

  private void connChecker() {
    if (!connCheckerStarted) {
      connectionChecker = new Thread("ConnectionChecker") {
        @Override
        public void run() {
          try {
            while (true) {
              int listsize = Manager.getListofPcs().size();
              if (listsize > 0) {
                String ip = "";
                for (short i = 0; i < listsize; i++) {
                  ip = Manager.getListofPcs().get(i).getIpAddress();
                  infoInterface localRef = Connection.getRemoteObj(ip);
                  // if the ref is not NULL means the RemoteObject is ACTIVE
                  if (localRef != null) {
                    Manager.getListofPcs().get(i).setPcState(true);
                    Manager.getListofPcs().get(i).setRef(localRef);
                    Tracking.info(true, "Connection Checker:" + ip + " connected");
                  } else {
                    Manager.getListofPcs().get(i).setPcState(false);
                    Manager.getListofPcs().get(i).setRef(null);
                    Tracking.info(true, "Connection Checker:" + ip + " not connected");
                  }
                }
              }
              Thread.sleep(threadPeriodInSecond * 1000);
            }
          } catch (Exception e) {
            Tracking.error(true, "Thread Checker Failed:" + ExceptionHandler.getMessage(e));
          }
        }
      };
      connectionChecker.start();
      connCheckerStarted = true;
    }
  }

  private void eliminatorThread() {
    if (!pcEliminatorStarted) {
      pcEliminator = new Thread("pcEliminator") {
        @Override
        public void run() {
          try {
            int residMorethanSeconds = getValue("time.eliminat");
            residMorethanSeconds = (residMorethanSeconds == -1) ? 180 : residMorethanSeconds;

            do{
              int listsize = Manager.getListofPcs().size();
              if (listsize > 0) {
                for (short i = 0; i < listsize; i++) {
                  if (!Manager.getListofPcs().get(i).getPcState()) {
                    if (Manager.getListofPcs().get(i).getTimeFromLastConn() >= residMorethanSeconds) {
                      Tracking.echo("PC("+i+") lastconn~"+ Manager.getListofPcs().get(i).getLastconnection()
                          +"|RightNow~"+TimeHandler.toString(TimeHandler.getCurrentTime(),true,true,true)   );
                      Manager.getListofPcs().remove(i);
                      Tracking.info(true, "Thread Eliminator remove the Pc(" + i + ")");
                    }
                  }
                }
              }
              Thread.sleep(threadPeriodInSecond * 1000);
            }while (true);
          } catch (Exception e) {
            Tracking.error(true, "Thread Eliminator Failed:" + ExceptionHandler.getMessage(e));
          }
        }
      };
      pcEliminator.start();
      pcEliminatorStarted = true;
    }
  }

  private void connNotifier() {
    if (!connNotifierStarted) {
      connectionNotifier = new Thread("Connection Notifier") {
        @Override
        public void run() {
          try {
            // base on ip list
            int listsize = Manager.getListofPcs().size() - 1;
            if (listsize >= 0) {

              String ip = Manager.getListofPcs().get(listsize).getIpAddress();
              Manager.getListofPcs().remove(listsize);
              int exist = Manager.indexOf(ip);

              Tracking.info(true, "Connection Notifier: New Conn ip: " + ip);
              if (exist == -1) { // does not exist
                // add new pc element, and reInsert ip in the ipList
                Pc newConnectedPc = new Pc(ip);
                newConnectedPc.setRef(getRemoteObj(ip));
                newConnectedPc.updateLastconnection();
                Manager.getListofPcs().add(newConnectedPc);
                Tracking.info(true, "Connection Notifier:" + ip + " is new for me");
              } else { // exist meas update state and Date
                Manager.getListofPcs().get(exist).setPcState(true);
                Manager.getListofPcs().get(exist).updateLastconnection();
                Tracking.info(true, "Connection Notifier:" + ip + " is Aready listed");
              }

            } else {
              Tracking.info(true, "Connection Notifier: Waiting");
            }
          } catch (Exception e) {
            Tracking.error(true, "Thread Notifier Failed:" + ExceptionHandler.getMessage(e));
          }
        }
      };
      connectionNotifier.start();
      connNotifierStarted = true ;
    }
  }
  /*
   * try to connect with remote object of specific PC
   * */
  private static infoInterface getRemoteObj(String ipAddress) {
    try {

      String fullPath = "//" + ipAddress + "/pcWait";

      infoInterface infoObj;
      infoObj = (infoInterface) Naming.lookup(fullPath);

      infoObj.get("os.name");
      // Tracking.info(true,"Thread Checker get info :"+result+" from "+ip);
      return infoObj;
    } catch (Exception e) {
      Tracking.error(true, "getRemoteObj (" + ipAddress + "):Not connected"+ExceptionHandler.getMessage(e));
      return null;
    }
  }
}
