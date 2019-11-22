package me.elhoussam.util.sys;
import java.time.LocalTime;
public class TimeHandler {

  public static int getCurrentTime() {

    return LocalTime.now().toSecondOfDay();
  }
  public static String getTimeString() {
    return LocalTime.now().toString();
  }

  public static int timeDifference(int time) {
    int rightNow = getCurrentTime();
    return rightNow - time ;
  }

  public static String toString(int time,Boolean...enable) {
    String mytime = "";
    //time = (time>89999)?89999:time;
    //for(Boolean e : enable)Tracking.echo(e);

    byte hour = Byte.valueOf(Integer.toString(time / 3600));
    mytime += ((enable.length > 0 && enable[0]==true)?hour:"**");
    time = time - (hour * (3600));


    byte min = Byte.valueOf( Integer.toString(time/60)) ;
    mytime += ((enable.length >1 && enable[1]==true)?":"+ min:":**");
    time = time - (min * (60));

    byte sec = Byte.valueOf( Integer.toString(time)) ;
    mytime+=((enable.length >2 && enable[2])?":"+ sec:"");

    return mytime ;
  }
}
