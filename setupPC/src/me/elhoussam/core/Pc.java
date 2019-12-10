package me.elhoussam.core;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import me.elhoussam.implementation.info;
import me.elhoussam.interfaces.infoInterface.STATE;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.SecurityHandler;

public class Pc {
  private static Pc localObj = null;
  private String ipAddress = "NONE";
  private int startTime = -1;
  private int lastOpenTime = -1;
  private int workTime = 0;
  private boolean firstTimeOpen;
  private STATE currentState = STATE.PAUSSED;

  public static STATE getCurrentState() {
    return localObj.currentState;
  }

  public static int getWorkTime() {
    if (Pc.getCurrentState().equals(STATE.PAUSSED))
      return localObj.workTime;
    else if (Pc.getCurrentState().equals(STATE.WORKING))
      return localObj.workTime + (connection.currentTimeManagerPc() - localObj.lastOpenTime);
    else if (Pc.getCurrentState().equals(STATE.CLOSED))
      return 0;
    return -2;
  }

  public static int Pause() {
    localObj.currentState = STATE.PAUSSED;
    int currentTime = connection.currentTimeManagerPc();
    if (localObj.firstTimeOpen == true) {
      localObj.workTime = currentTime - localObj.startTime;
    } else {
      localObj.workTime = localObj.workTime + (currentTime - localObj.lastOpenTime);
    }

    return localObj.workTime;
  }

  public static int Close() {

    localObj.currentState = STATE.CLOSED;
    int currentTime = connection.currentTimeManagerPc();

    if (localObj.firstTimeOpen == true) {
      return currentTime - localObj.startTime;
    } else {
      int time = localObj.workTime + (currentTime - localObj.lastOpenTime);
      localObj.workTime = 0;
      return time;
    }
  }

  public static void Open(int lastWorkTime) {
    if (getCurrentState().equals(STATE.CLOSED))
      localObj.firstTimeOpen = true;
    else // paussed
      localObj.firstTimeOpen = false;

    localObj.currentState = STATE.WORKING;
    localObj.lastOpenTime = connection.currentTimeManagerPc();

    if (lastWorkTime >= 0)
      localObj.workTime = lastWorkTime;

  }

  public static int getStartTime() {
    return localObj.startTime;
  }

  public static int getLastOpenTime() {
    return localObj.lastOpenTime;
  }

  public static void setStartTime(int time) {
    if (localObj.startTime == -1) {
      localObj.startTime = time;
      // localObj.lastOpenTime = time;
    }
  }

  public static String getMyIp() {
    return localObj.ipAddress;
  }

  /**
   * static method that load the security policy file and setup the security manager
   */
  private static void setupSecurityPolicy() throws Exception {
    String res = SecurityHandler.instance.LoadSecurityPolicy("");
    Tracking.info(true, "Security State : " + res);
  }

  /**
   * static method create the object the object which represent the service then start the
   * LocalRegistry in server and finaly bind the service object with a public name in the
   * localregistry
   */
  private static void providerWaiting() {
    try {
      setupSecurityPolicy();
      localObj.ipAddress = SecurityHandler.myLocalIp();
      System.setProperty("java.rmi.server.hostname", localObj.ipAddress);
      Tracking.info(true, "Pc Ip Address : " + localObj.ipAddress);
      info provideWait = new info();
      // provideWait.setFile("ensoftcorp.jar");
      LocateRegistry.createRegistry(1099);
      Naming.rebind("//" + localObj.ipAddress + "/pcWait", provideWait);
      Tracking.info(true, "Provider PC is ready.");
    } catch (Exception e) {
      Tracking.error(true, "Provider PC failed: " + ExceptionHandler.getMessage(e));
    }
  }

  /**
   * method call other methodes to construct the pieces of the app
   */
  public static void start() {

    Tracking.setFolderName("PcApp");
    Tracking.info(true, "Start Pc Applicaion");
    // for java to use preferIp version = 4
    // java.net.preferIPv4Stack
    System.setProperty("java.net.preferIPv6Addresses", "true");
    String ipManager = "DEFAULT:192.168.1.2";
    // create the remote object which represent the service the pc provide
    initPcObject();
    providerWaiting();

    Tracking.info(true, "ip of the manager " + ipManager);
    // launch the thread to notifier the manager
    connection.launchThreads();
    localObj.startTime = connection.currentTimeManagerPc();

  }

  private static void initPcObject() {
    if (localObj == null) {
      localObj = new Pc();
    }

  }
}
