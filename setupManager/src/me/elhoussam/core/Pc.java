package me.elhoussam.core;

import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.sys.TimeHandler;

public class Pc {
  /**/
  private String ipAddress = "unkown";
  private infoInterface ref = null;
  private Boolean pcState = true;
  private int lastconnection = -1;
  private int lastWorkTime = 0;

  public String getIpAddress() {
    return ipAddress;
  }
  public int getLastWorkTime() {
    return lastWorkTime;
  }
  public void setLastWorkTime(int workTime ) {
    lastWorkTime = workTime ;
  }

  public Pc(String ipAddress, infoInterface ref, int lastconnection) {
    super();
    this.ipAddress = ipAddress;
    this.ref = ref;
    this.lastconnection = lastconnection;
  }

  public Pc(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public infoInterface getRef() {
    return ref;
  }

  public void setRef(infoInterface ref) {
    this.ref = ref;
  }

  public Boolean getPcState() {
    return pcState;
  }

  public void setPcState(Boolean pcState) {
    this.pcState = pcState;
  }
  public String getLastconnection() {
    return TimeHandler.toString(lastconnection, true,true,true);
    //    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    //return dateFormat.format(this.lastconnection);

  }
  public int getLastConnectionInt() {
    return  lastconnection ;
    //    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    //return dateFormat.format(this.lastconnection);

  }
  /*
   * update the time&date of the last Connection with this pc
   * */
  public void updateLastconnection() {
    //Date date = new Date();
    this.lastconnection = TimeHandler.getCurrentTime();
  }
  /*
   * compute the difference between NOW and the last Connection
   * */
  public int getTimeFromLastConn() {
    //int rightNow = TimeHandler.getCurrentTime();
    return TimeHandler.getCurrentTime() - lastconnection;
  }

}
