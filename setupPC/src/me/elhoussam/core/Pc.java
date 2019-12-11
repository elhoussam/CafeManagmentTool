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
  private int startTime = 0;
  private int lastOpenTime = 0;
  private int workTime = 0;
  private int lastCloseTime = 0;
  private int closeTime = 0;
  private int lastPauseTime = 0;
  private int pauseTime = 0;
  private boolean firstTimeOpen;
  private STATE currentState = STATE.CLOSED;

  public static STATE getCurrentState() {
    return localObj.currentState;
  }

  public static int getWorkTime() {
    if (Pc.getCurrentState().equals(STATE.PAUSSED))
      return localObj.workTime;
    else if (Pc.getCurrentState().equals(STATE.WORKING))
      return localObj.workTime + (connection.currentTimeManagerPc() - localObj.lastOpenTime);
    else if (Pc.getCurrentState().equals(STATE.CLOSED))
      return localObj.workTime;
    return -2;
  }

  public static int getPauseTime() {
    if (Pc.getCurrentState().equals(STATE.PAUSSED))
      return localObj.pauseTime + (connection.currentTimeManagerPc() - localObj.lastPauseTime);
    else if (Pc.getCurrentState().equals(STATE.WORKING))
      return localObj.pauseTime;
    else if (Pc.getCurrentState().equals(STATE.CLOSED))
      return localObj.pauseTime;
    return -2;
  }

  public static int getCloseTime() {
    if (Pc.getCurrentState().equals(STATE.PAUSSED))
      return localObj.closeTime;
    else if (Pc.getCurrentState().equals(STATE.WORKING))
      return localObj.closeTime;
    else if (Pc.getCurrentState().equals(STATE.CLOSED))
      return localObj.closeTime + (connection.currentTimeManagerPc() - localObj.lastCloseTime);
    return -2;
  }

  public static int Pause(int lastPauseTime) {
    localObj.currentState = STATE.PAUSSED;
    int currentTime = connection.currentTimeManagerPc();
    localObj.lastPauseTime = currentTime;
    if (lastPauseTime >= 0)
      localObj.pauseTime = lastPauseTime;
    localObj.workTime = localObj.workTime + (currentTime - localObj.lastOpenTime);

    return localObj.workTime;
  }

  public static int Close(int lastCloseTime) {
    STATE lastState = localObj.currentState;
    localObj.currentState = STATE.CLOSED;
    int currentTime = connection.currentTimeManagerPc();
    localObj.lastCloseTime = currentTime;
    if (lastCloseTime >= 0)
      localObj.closeTime = lastCloseTime;
    int time = localObj.workTime;
    localObj.workTime = 0;
    if (lastState.equals(STATE.WORKING))
      time += (currentTime - localObj.lastOpenTime);
    return time;
  }

  public static int Open(int lastWorkTime) {
    if (getCurrentState().equals(STATE.CLOSED))
      localObj.firstTimeOpen = true;
    else // paussed
      localObj.firstTimeOpen = false;

    localObj.currentState = STATE.WORKING;
    int currentTime = connection.currentTimeManagerPc();
    localObj.lastOpenTime = currentTime;
    if (lastWorkTime >= 0)
      localObj.workTime = lastWorkTime;

    localObj.closeTime += (currentTime - localObj.lastCloseTime);
    return localObj.closeTime;

  }

  public static int Resume(int lastWorkTime) {
    localObj.currentState = STATE.WORKING;
    int currentTime = connection.currentTimeManagerPc();

    localObj.lastOpenTime = currentTime;
    if (lastWorkTime >= 0)
      localObj.workTime = lastWorkTime;

    localObj.pauseTime = +(currentTime - localObj.lastPauseTime);
    return localObj.pauseTime;

  }

  public static int getStartTime() {
    return localObj.startTime;
  }

  public static int getLastOpenTime() {
    return localObj.lastOpenTime;
  }

  public static int getLastCloseTime() {
    return localObj.lastCloseTime;
  }

  public static int getLastPauseTime() {
    return localObj.lastPauseTime;
  }

  public static void setStartTime(int time) {
    if (localObj.startTime == 0) {
      localObj.startTime = time;
      localObj.lastCloseTime = time;
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
  public static void start(String arg) {

    Tracking.setFolderName("PcApp");
    Tracking.info(true, "Start Pc Applicaion");
    // for java to use preferIp version = 4
    // java.net.preferIPv4Stack
    System.setProperty("java.net.preferIPv6Addresses", "true");
    String ipManager = (!arg.isEmpty()) ? arg : "192.168.1.2";
    // create the remote object which represent the service the pc provide
    initPcObject();
    providerWaiting();

    Tracking.info(true, "ip of the manager " + ipManager);
    // launch the thread to notifier the manager
    connection.launchThreads(ipManager);
    localObj.startTime = connection.currentTimeManagerPc();

  }

  private static void initPcObject() {
    if (localObj == null) {
      localObj = new Pc();
    }

  }


}
