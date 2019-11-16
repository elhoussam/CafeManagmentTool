package me.elhoussam.implementation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import me.elhoussam.core.Manager;
import me.elhoussam.core.connection;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;

public class ManagerPc extends UnicastRemoteObject
implements ManagerPcInterface  {

  public ManagerPc() throws RemoteException {}
  /*
   *	String NotifyAdmin(String myInfo) : 
   *	private method To inform the manager 
   *	of the arrival new Pcs, and send PcIp
   * 	using this method, is represent 
   *	the Only service the manager provoide.
   */

  public String NotifyAdmin(String myInfo){
    try {
      myInfo = myInfo.trim();
      if( !myInfo.isEmpty()   ) {
        String ipOfPc =  myInfo ; 
        Manager.addNewPc(ipOfPc);
        connection.getNotifier().run(); 

        Tracking.info(true,"the ("+ipOfPc+") was recieved");

        return "msg is recieved\n";
      }else {

        Tracking.warning(true,"the msg was badly received");
        return "msg was not delivered\n";
      }
    }catch(Exception e ) {
      Tracking.error(true,"some thing happened:"+ ExceptionHandler.getMessage(e));
      return null ;
    }

  }

}
