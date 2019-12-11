package me.elhoussam.core;

import java.rmi.Naming;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.PropertyHandler;
import me.elhoussam.util.sys.SecurityHandler;
import me.elhoussam.util.sys.TimeHandler;

public class connection {
  /**
   * threadStarted : to unsure the thread was activated just ones Notifier : the thread that
   * responsible to reach to manager ipOfManager : read the IP from the config.properties or use the
   * default
   */
  private Boolean threadStarted = false;
  private Boolean localClockStarted = false;
  private Thread Notifier = null;
  private Thread localClock = null;
  private Boolean timeUpdatedFromServer = false;
  private String ipOfManager = "";
  private static ManagerPcInterface managerRef = null;
  private static Boolean managerRefAssigned = false;
  private static connection obj = null;
  private int currentTimeManagerPc = 0;
  private int periodToSyncronize = 180;
  private int lastSynchrWithManager = -1 * periodToSyncronize;


  public static int currentTimeManagerPc() {
    if (obj == null)
      return -2;
    return obj.currentTimeManagerPc;
  }

  public static ManagerPcInterface getManagerRef() {
    return managerRef;
  }

  public static void setManagerRef(ManagerPcInterface managerRef) {
    if (connection.managerRef == null || !connection.managerRef.equals(managerRef)) {
      connection.managerRef = managerRef;
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

  public static void launchThreads(String ip) {
    if (obj == null) {
      obj = new connection();
      obj.Init(ip);
    }
  }

  /*
   * Init: start invoking other private method to setup the env for Notifier thread
   */
  private void Init(String address) {
    // read values from config.properties
    String res = (address.isEmpty()) ? connection.getValue("ip.manager") : address;
    ipOfManager = (res != null) ? res : "192.168.1.2";
    // launch the reachManagerThread
    ManagerPcInterface obj = connection.getRemoteObj(ipOfManager);

    connection.setManagerRef(obj);

    launchLocalClock();

    reachManager(ipOfManager);

  }

  /*
   * reachManager : here we implement the functionality of the Notifier thread Notifier thread task
   * : reach the manager and sleep for a while and repeat just in case the manager restart.
   */
  private void reachManager(String ip) {
    final String ipManager = ip;
    if (!threadStarted) {
      Notifier = new Thread("reachManager") {
        @Override
        public void run() {

          // here we read theread.period property from external config file
          String str = connection.getValue("thread.period");
          // default value is 333
          int taskPeriod = (str == null) ? 20 : Integer.valueOf(str);
          // Tracking.echo(taskPeriod);
          int loopNb = 0;
          do {
            try {
              if (loopNb != 0) {
                connection.setManagerRef(getRemoteObj(ipManager));
                if (connection.getManagerRef() != null) {
                  int currentTime = connection.obj.currentTimeManagerPc;



                  int time = connection.currentTimeManagerPc() - Pc.getStartTime();
                  Tracking.echo("STARTUP "
                      + TimeHandler.toString(Pc.getStartTime(), true, true, true) + "\tNOW "
                      + TimeHandler.toString(currentTime, true, true, true) + "\tUPTIME "
                      + TimeHandler.toString(time, true, true, true) + "\nL.Open "
                      + TimeHandler.toString(Pc.getLastOpenTime(), true, true, true) + "\twork "
                      + TimeHandler.toString(Pc.getWorkTime(), true, true, true) + "\nL.Pause "

                      + TimeHandler.toString(Pc.getLastPauseTime(), true, true, true) + "\tpause "
                      + TimeHandler.toString(Pc.getPauseTime(), true, true, true) + "\nL.Close "

                      + TimeHandler.toString(Pc.getLastCloseTime(), true, true, true) + "\tclose "
                      + TimeHandler.toString(Pc.getCloseTime(), true, true, true)

                      + "\tSTATE NOW [" + Pc.getCurrentState() + "]"

                  );
                } else {
                  Tracking.echo("rach manager failed because is NULL ");
                }
              }
              // notifier manager every minute
            } catch (Exception e) {
              Tracking.error(true, "PC Notifier Failed:" + ExceptionHandler.getMessage(e));
            } finally {
              try {
                loopNb++;
                Thread.sleep(taskPeriod * 1000);
              } catch (InterruptedException e1) {
                Tracking.error(true, "PC Notifier Failed:" + ExceptionHandler.getMessage(e1));
              } // taskPeriod
            }
          } while (true);
        }
      };
      Notifier.start();
      threadStarted = true;
    }

  }

  private void launchLocalClock() {
    if (!localClockStarted) {
      localClock = new Thread("localClock") {
        @Override
        public void run() {
          Boolean setStartTime = false;
          do {
            try {
              int timeBetweenSynch =
                  connection.obj.currentTimeManagerPc - connection.obj.lastSynchrWithManager;
              if (timeBetweenSynch >= connection.obj.periodToSyncronize) {
                connection.setManagerRef(connection.getRemoteObj(connection.obj.ipOfManager));
                int currentTimeAtManager = connection.getManagerRef().getCurrentTime();
                connection.obj.currentTimeManagerPc = currentTimeAtManager;
                connection.obj.lastSynchrWithManager = currentTimeAtManager;
                if (!setStartTime) {
                  Pc.setStartTime(currentTimeAtManager);
                  setStartTime = true;
                }
                Tracking.echo("Synchronize Time with Manager "
                    + TimeHandler.toString(currentTimeAtManager, true, true, true));
              }

              synchronized (connection.obj) {
                connection.obj.currentTimeManagerPc += 1;
              }
            } catch (Exception e) {
              Tracking.error(true, "PC launchLocalClock :" + ExceptionHandler.getMessage(e));
            } finally {
              try {
                Thread.sleep(1 * 1000);
              } catch (InterruptedException e1) {
                Tracking.error(true, "PC Notifier Failed:" + ExceptionHandler.getMessage(e1));
              } // taskPeriod
            }
          } while (true);

        }
      };
      localClock.start();
      localClockStarted = true;
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
    }
  }

}
