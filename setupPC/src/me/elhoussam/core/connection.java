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
  private int currentTimeManagerPc = -1;

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

  public static void launchThreads() {
    if (obj == null) {
      obj = new connection();
      obj.Init();
    }
  }

  /*
   * Init: start invoking other private method to setup the env for Notifier thread
   */
  private void Init() {
    // read values from config.properties
    String res = connection.getValue("ip.manager");
    ipOfManager = (res != null) ? res : "192.168.1.2";
    // launch the reachManagerThread
    ManagerPcInterface obj = connection.getRemoteObj(ipOfManager);

    connection.setManagerRef(obj);


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

          Boolean setStartTime = false;
          // here we read theread.period property from external config file
          String str = connection.getValue("thread.period");
          // default value is 333
          int taskPeriod = (str == null) ? 20 : Integer.valueOf(str);
          // Tracking.echo(taskPeriod);

          do {
            try {
              connection.setManagerRef(getRemoteObj(ipManager));

              int currentTime = connection.getManagerRef().getCurrentTime();
              synchronized (connection.obj) {
                connection.obj.currentTimeManagerPc = currentTime;
                timeUpdatedFromServer = true;
              }

              if (!setStartTime) {
                Pc.setStartTime(currentTime);
                setStartTime = true;
              }

              int time = connection.currentTimeManagerPc() - Pc.getStartTime();
              Tracking.echo("PC start at~"
                  + TimeHandler.toString(Pc.getStartTime(), true, true, true) + "|Right Now~"
                  + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true)
                  + "|pc uptime~" + TimeHandler.toString(time, true, true, true));
              // notifier manager every minute
            } catch (Exception e) {

              synchronized (connection.obj) {
                timeUpdatedFromServer = false;
              }
              if (localClock == null) {
                if (currentTimeManagerPc != -1)
                  launchLocalClock();
              } else {
                synchronized (connection.obj) {
                  connection.obj.notify();
                }
              }
              Tracking.error(true, "PC Notifier Failed:" + ExceptionHandler.getMessage(e));
            } finally {
              try {
                Thread.sleep(20 * 1000);
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
          try {
            do {
              synchronized (connection.obj) {
                connection.obj.currentTimeManagerPc += 20;
              }
              Tracking.echo("PC launchLocalClock : time "
                  + TimeHandler.toString(connection.obj.currentTimeManagerPc, true, true, true));
              // Thread.sleep(20 * 1000); // taskPeriod


              if (timeUpdatedFromServer == true) {
                synchronized (connection.obj) {
                  connection.obj.wait();
                }
              } else {
                Thread.sleep(20 * 1000); // taskPeriod
              }
            } while (true);
          } catch (Exception e) {
            Tracking.error(true, "PC launchLocalClock :" + ExceptionHandler.getMessage(e));
          }
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
