package me.elhoussam.implementation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import me.elhoussam.core.Pc;
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

}
