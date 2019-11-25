package me.elhoussam.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import me.elhoussam.core.Pc;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.TimeHandler;

public class info extends UnicastRemoteObject implements infoInterface {

  public info() throws RemoteException {}

  /*
   * String String getter() : public method To give the manager information about the active pc, and
   * it represent the sevice in PC side.
   */
  @Override
  public String get(String property) throws RemoteException {
    String str = System.getProperty(property);
    Tracking.info(true, "info getter Triggered");
    return str;
  }

  @Override
  public int getLifeTime() throws RemoteException {
    int startTime = Pc.getStartTime();
    return TimeHandler.timeDifference(startTime);
  }

  @Override
  public String getIpAddress() throws RemoteException {
    return Pc.getMyIp();
  }

  @Override
  public int getStartTime() throws RemoteException {
    return Pc.getStartTime();
  }

  private static String file = "";

  @Override
  public void setFile(String f) {
    file = f;
  }

  @Override
  public boolean login(ManagerPcInterface c) throws RemoteException {
    /*
     *
     * Sending The File...
     *
     */
    try {
      File f1 = new File(file);
      FileInputStream in = new FileInputStream(f1);
      byte[] mydata = new byte[1024 * 1024];
      int mylen = in.read(mydata);
      while (mylen > 0) {
        c.sendData(f1.getName(), mydata, mylen);
        mylen = in.read(mydata);
      }
    } catch (Exception e) {
      e.printStackTrace();

    }

    return true;
  }
}
