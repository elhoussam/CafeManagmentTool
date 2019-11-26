package me.elhoussam.implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import me.elhoussam.core.Connection;
import me.elhoussam.core.Manager;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;

public class ManagerPc extends UnicastRemoteObject implements ManagerPcInterface {

  public ManagerPc() throws RemoteException {}
  /*
   * String NotifyAdmin(String myInfo) : private method To inform the manager of the arrival new
   * Pcs, and send PcIp using this method, is represent the Only service the manager provoide.
   */

  @Override
  public String NotifyAdmin(String myInfo) {
    try {
      myInfo = myInfo.trim();
      if (!myInfo.isEmpty()) {
        String ipOfPc = myInfo;
        Manager.addNewPc(ipOfPc);
        Connection.getNotifier().run();

        Tracking.info(true, "the (" + ipOfPc + ") was recieved");

        return "msg is recieved\n";
      } else {

        Tracking.warning(true, "the msg was badly received");
        return "msg was not delivered\n";
      }
    } catch (Exception e) {
      Tracking.error(true, "some thing happened:" + ExceptionHandler.getMessage(e));
      return null;
    }

  }
  private static final long serialVersionUID = 1L;
  public String name;


  @Override
  public String getName() throws RemoteException{
    return name;
  }

  @Override
  public boolean sendData(String filename, byte[] data, int len) throws RemoteException{
    try{
      File f=new File(filename);
      if(f.exists()==true)f.delete();
      f.createNewFile();
      FileOutputStream out=new FileOutputStream(f,true);
      out.write(data,0,len);
      out.flush();
      out.close();
      System.out.println("Done writing data...");
    }catch(Exception e){
      e.printStackTrace();
    }
    return true;
  }
}
