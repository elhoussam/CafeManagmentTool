package me.elhoussam.core;

import java.rmi.Naming;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.PropertyHandler;
import me.elhoussam.util.sys.TimeHandler;

public class connection {
  /*
   * logical variable to insure the threads was created just ones
   * */
  private static Boolean connCheckerStarted = false;
  private static Boolean connNotifierStarted = false;
  private static Boolean pcEliminatorStarted = false;
  /*
   * connCheckerStarted  : thread check connection with all connected pcs
   * connNotifierStarted : thread recieve the new connection from pcs
   * pcEliminatorStarted : thread remove all pcs that disconnected
   * */
  private static Thread connectionChecker = null;
  private static Thread connectionNotifier = null;
  private static Thread pcEliminator = null;

  private static int threadPeriodInSecond = 120;
  /*
   * setup the env for the thread
   * */
  public static void init() {
    // read property file
    threadPeriodInSecond = connection.getValue("thread.period");
    // Lunch the Thread = (ConnNotifier)
    connection.connNotifier();
    Tracking.info(true, "Connection launch Notifier thread");

    // then launch the thread = connChecker
    connection.connChecker();
    Tracking.info(true, "Manager launch Checker thread");

    // then launch the thread = Eliminator
    connection.eliminatorThread();
    Tracking.info(true, "Manager launch Eliminator thread");

  }

  public static Thread getNotifier() {
    return connectionNotifier;
  }
  /*
   * read properties from external config file if exist
   * */
  private static int getValue(String str) {
    PropertyHandler ph = new PropertyHandler("");
    String s = ph.getPropetry(str); // "thread.period"
    if (s != null)
      return Integer.valueOf(s);
    else
      return -1;
  }

  private static void connChecker() {
    if (!connCheckerStarted) {
      connectionChecker = new Thread("ConnectionChecker") {
        @Override
        public void run() {
          try {
            while (true) {
              int listsize = Manager.get().size();
              if (listsize > 0) {
                String ip = "";
                for (short i = 0; i < listsize; i++) {
                  ip = Manager.get().get(i).getIpAddress();
                  infoInterface localRef = connection.getRemoteObj(ip);
                  // if the ref is not NULL means the RemoteObject is ACTIVE
                  if (localRef != null) {
                    Manager.get().get(i).setPcState(true);
                    Manager.get().get(i).setRef(localRef);
                    Tracking.info(true, "Connection Checker:" + ip + " connected");
                  } else {
                    Manager.get().get(i).setPcState(false);
                    Manager.get().get(i).setRef(null);
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

  private static void eliminatorThread() {
    if (!connection.pcEliminatorStarted) {
      connection.pcEliminator = new Thread("pcEliminator") {
        @Override
        public void run() {
          try {
            int residMorethanSeconds = connection.getValue("time.eliminat");
            residMorethanSeconds = (residMorethanSeconds == -1) ? 180 : residMorethanSeconds;

            do{
              int listsize = Manager.get().size();
              if (listsize > 0) {
                for (short i = 0; i < listsize; i++) {
                  if (!Manager.get().get(i).getPcState()) {
                    if (Manager.get().get(i).getTimeFromLastConn() >= residMorethanSeconds) {
                      Tracking.echo("PC("+i+") lastconn~"+ Manager.get().get(i).getLastconnection()
                          +"|RightNow~"+TimeHandler.toString(TimeHandler.getCurrentTime(),true,true,true)   );
                      Manager.get().remove(i);
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

  private static void connNotifier() {
    if (!connNotifierStarted) {
      connectionNotifier = new Thread("Connection Notifier") {
        @Override
        public void run() {
          try {
            // base on ip list
            int listsize = Manager.get().size() - 1;
            if (listsize >= 0) {

              String ip = Manager.get().get(listsize).getIpAddress();
              Manager.get().remove(listsize);
              int exist = Manager.indexOf(ip);

              Tracking.info(true, "Connection Notifier: New Conn ip: " + ip);
              if (exist == -1) { // does not exist
                // add new pc element, and reInsert ip in the ipList
                Pc newConnectedPc = new Pc(ip);
                newConnectedPc.setRef(getRemoteObj(ip));
                newConnectedPc.updateLastconnection();
                Manager.get().add(newConnectedPc);
                Tracking.info(true, "Connection Notifier:" + ip + " is new for me");
              } else { // exist meas update state and Date
                Manager.get().get(exist).setPcState(true);
                Manager.get().get(exist).updateLastconnection();
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
      connNotifierStarted = true;
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
