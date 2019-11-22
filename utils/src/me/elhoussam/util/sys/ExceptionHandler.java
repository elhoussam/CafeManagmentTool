package me.elhoussam.util.sys;

public class ExceptionHandler {
  public static String getMessage(Exception e) {
    String name = e.getClass().getCanonicalName();
    return name ;
  }

}
