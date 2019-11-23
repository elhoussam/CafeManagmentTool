package me.elhoussam.implementation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import me.elhoussam.core.Pc;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

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
  public Long getLifeTime() throws RemoteException {
    Date now = new Date();
    long time = Math.abs(now.getTime() - Pc.getStartTime());

    return TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS);
  }

  @Override
  public String getIpAddress() throws RemoteException {
    return Pc.getMyIp();
  }

}
