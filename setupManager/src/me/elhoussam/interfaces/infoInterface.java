package me.elhoussam.interfaces;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface infoInterface extends java.rmi.Remote {
  /*
   * String String getter() : public method To give the manager information about the active pc, and
   * it represent the sevice in PC side.
   */
  public String get(String property) throws RemoteException;

  public int getLifeTime() throws RemoteException;

  public int getStartTime() throws RemoteException;

  public String getIpAddress() throws RemoteException;

  public void setFile(String f) throws RemoteException;

  public boolean login(ManagerPcInterface c) throws RemoteException;

  public String getSceenshotNow() throws RemoteException;

  public Object getFileChooser() throws RemoteException;

  public ArrayList<String> getRootDir(Boolean option) throws RemoteException;

  public String[] changeDirAndListContent(String path) throws RemoteException;

  public byte fileOrDirectory(String path) throws RemoteException;
}
