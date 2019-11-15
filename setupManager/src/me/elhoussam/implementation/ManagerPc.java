package me.elhoussam.implementation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import me.elhoussam.core.Pc;
import me.elhoussam.core.connection;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;

public class ManagerPc extends UnicastRemoteObject
implements ManagerPcInterface  {
  /*
   *	ArrayList IpOfPcs  contain all the ip of connect pcs
   */
  private ArrayList<String> IpOfPcs = new ArrayList<String>();
  private ArrayList<Pc> listOfPcs = new ArrayList<Pc>();
  /*
   *	ArrayList<String> getListeActivePc() : 
   *	public method return the Array of all ip
   */
  public ArrayList<String> getListeActivePc() {
    return IpOfPcs;
  }
  public ArrayList<Pc> getListePcs() {
    return listOfPcs;
  }
  /*
   *	addNewIp(String ipNewActivePc) : 
   *	private method to add new ip to the list
   */
  private void addNewIp(String ipNewActivePc ) {
    IpOfPcs.add(ipNewActivePc);
  }

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
        addNewIp(ipOfPc);
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
