package me.elhoussam.core;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import me.elhoussam.implementation.info;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.SecurityHandler;
import me.elhoussam.util.sys.TimeHandler;

public class Pc {
  private static String ipAddress = "NONE";
  private static int startTime = -1;

  public static int getStartTime() {
    return startTime;
  }

  public static String getMyIp() {
    return ipAddress;
  }

  /*
   * void setupSecurityPolicy() : static method that load the security policy file and setup the
   * security manager
   */
  private static void setupSecurityPolicy() throws Exception {
    String res = SecurityHandler.instance.LoadSecurityPolicy("");
    Tracking.info(true, "Security State : " + res);
  }

  /*
   * void providerWaiting() : static method create the object the object which represent the service
   * then start the LocalRegistry in server and finaly bind the service object with a public name in
   * the localregistry
   */
  private static void providerWaiting() {
    try {
      setupSecurityPolicy();
      ipAddress = SecurityHandler.myLocalIp();
      System.setProperty("java.rmi.server.hostname", ipAddress);
      Tracking.info(true, "Pc Ip Address : " + ipAddress);
      info provideWait = new info();
      LocateRegistry.createRegistry(1099);
      Naming.rebind("//" + ipAddress + "/pcWait", provideWait);
      Tracking.info(true, "Provider PC is ready.");
    } catch (Exception e) {
      Tracking.error(true, "Provider PC failed: " + ExceptionHandler.getMessage(e));
    }
  }

  /*
   * void main(String[] args) : this method call other methodes to construct the pieces of the app
   */
  public static void start() {
    Tracking.setFolderName("PcApp");
    Tracking.info(true, "Start Pc Applicaion");
    // for java to use preferIp version = 4
    // java.net.preferIPv4Stack
    System.setProperty("java.net.preferIPv6Addresses", "true");
    String ipManager = "DEFAULT:192.168.1.2";
    // create the remote object which represent the service the pc provide
    providerWaiting();
    Tracking.info(true, "ip of the manager " + ipManager);
    // launch the thread to notifier the manager
    connection.Init();
    startTime = TimeHandler.getCurrentTime();
  }
}
