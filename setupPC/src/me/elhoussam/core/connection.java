package me.elhoussam.core;

import java.rmi.Naming;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.PropertyHandler;
import me.elhoussam.util.sys.SecurityHandler;
import me.elhoussam.util.sys.TimeHandler;

public class connection {
  /*
   * threadStarted : to unsure the thread was activated just ones Notifier : the thread that
   * responsible to reach to manager ipOfManager : read the IP from the config.properties or use the
   * default IP=192.168.1.2
   */
  private static Boolean threadStarted = false;
  private static Thread Notifier = null;
  private static String ipOfManager = "";
  private static ManagerPcInterface managerRef = null;
  private static Boolean managerRefAssigned = false;

  public static ManagerPcInterface getManagerRef() {
    return managerRef;
  }

  public static void setManagerRef(ManagerPcInterface managerRef) {
    if (!connection.managerRefAssigned) {
      connection.managerRef = managerRef;
      connection.managerRefAssigned = true;
    }
  }

  /*
   * getValue : read the properties from external config file
   */
  private static String getValue(String str) {
    PropertyHandler ph = new PropertyHandler("");
    String s = ph.getPropetry(str); // "thread.period"
    return s;
  }

  /*
   * Init: start invoking other private method to setup the env for Notifier thread
   */
  public static void Init() {
    // read values from config.properties
    String res = connection.getValue("ip.manager");
    ipOfManager = (res != null) ? res : "192.168.1.2";
    // launch the reachManagerThread
    ManagerPcInterface obj = connection.getRemoteObj(ipOfManager);

    connection.setManagerRef(obj);
    connection.reachManager(ipOfManager);
  }

  /*
   * reachManager : here we implement the functionality of the Notifier thread Notifier thread task
   * : reach the manager and sleep for a while and repeat just in case the manager restart.
   */
  private static void reachManager(String ip) {
    final String ipManager = ip;
    if (!threadStarted) {
      Notifier = new Thread("Notifier") {
        @Override
        public void run() {
          try {
            // here we read theread.period property from external config file
            String str = connection.getValue("thread.period");
            // default value is 333
            int taskPeriod = (str == null) ? 333 : Integer.valueOf(str);
            Tracking.echo(taskPeriod);
            do {
              getRemoteObj(ipManager);
              int time = TimeHandler.timeDifference(Pc.getStartTime());
              Tracking
                  .echo("PC start at~" + TimeHandler.toString(Pc.getStartTime(), true, true, true)
                      + "|Right Now~" + TimeHandler.getTimeString() + "|pc uptime~"
                      + TimeHandler.toString(time, true, true, true)

                  );

              // notifier manager every minute
              Thread.sleep(taskPeriod * 1000);
            } while (true);
          } catch (Exception e) {
            Tracking.error(true, "PC Notifier Failed:" + ExceptionHandler.getMessage(e));
          }
        }
      };
      Notifier.start();
      threadStarted = true;
    }
  }

  /*
   * getRemoteObj : try to connect with the manager remote object and send the local ip of pc
   * machine
   */
  private static ManagerPcInterface getRemoteObj(String ipAddress) {
    try {

      Tracking.info(true, "getRemoteObj try to reach Manager ");
      ManagerPcInterface remoteObj;
      remoteObj = (ManagerPcInterface) Naming.lookup("//" + ipAddress + "/ManagerWait");

      String res = SecurityHandler.myLocalIp();
      String result = remoteObj.NotifyAdmin(res);
      Tracking.info(true, "getRemoteObj res =" + result);
      return remoteObj;
    } catch (Exception e) {
      Tracking.error(true, "getRemoteObj (" + ipAddress + "):Not connected");
      return null;
      // ExceptionHandler.getMessage(e)
    }
  }

}
