package me.elhoussam.util.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Tracking {
  public static Boolean globalSwitcher = true ;
  /**
   * these two logFolderName, logFolderNameChange to save
   *	the chosing name of logfolder and insure will not
   *	be modified more then one time.
   */
  private static String logFolderName ="logFolder";
  private static Boolean logFolderNameChange = false ;
  private static Logger  instance = null;
  /**
   * which trace the infos of the system
   */

  public static synchronized void info(Boolean enable, String infoMsg) {
    String[] parts = Tracking.LineNb().split("-")  ;
    Toggle(globalSwitcher && enable,parts[0],Integer.valueOf(parts[1])) ;
    instance.info( infoMsg );
    //fh.close();
  }
  /**
   * that switchOff the console handler
   * 
   */
  private static void Toggle(Boolean sw, String classname, int linenumber) {
    ConsoleHandler chLocal = (ConsoleHandler) instance.getHandlers()[0];
    if( sw ) { // enaable console handler by add it IF NOT EXIST
      ((Formatter)chLocal.getFormatter()).setClassName(classname);
      ((Formatter)chLocal.getFormatter()).setLineNumber(linenumber);
      ((Formatter)chLocal.getFormatter()).setFormat( "[%3$s:%4$d]%5$s%n" );
    }else {
      ((Formatter)chLocal.getFormatter()).setFormat("");
    }
    FileHandler fhLocal = (FileHandler) instance.getHandlers()[1];
    ((Formatter)fhLocal.getFormatter()).setClassName(classname);
    ((Formatter)fhLocal.getFormatter()).setLineNumber(linenumber);
  }
  /**
   *which trace the warning of the system
   */
  public static synchronized void warning(Boolean enable, String warningMsg ) {

    String[] parts = Tracking.LineNb().split("-")  ;
    Toggle(globalSwitcher && enable,parts[0],Integer.valueOf(parts[1])) ;
    instance.warning( warningMsg );
    //fh.close();
  }
  /**
   * public method which trace the errors of the system
   */
  public static synchronized void error(Boolean enable, String errorMsg ) {
    enable = true;
    String[] parts = Tracking.LineNb().split("-")  ;
    Toggle(enable ,parts[0],Integer.valueOf(parts[1])) ;
    instance.severe(errorMsg);
  }
  /**
   * allow you to set new name folder
   * that contain the log file just for one time.
   */
  public static void setFolderName(String name) {
    if ( !logFolderNameChange && !name.trim().isEmpty()  && ! name.trim().equalsIgnoreCase(logFolderName) ) {
      logFolderName = "log_"+name.trim(); logFolderNameChange = true; }
    String[] parts = Tracking.LineNb().split("-")  ;
    Tracking.getInstance( parts[0],Integer.valueOf(parts[1])) ;
  }
  /**
   *  that return new Logger object
   *	if its the first you invoke this method, or will
   *	setup new formatter if the lastclassname, lastlinenumber
   *	is changed.
   */
  private static Logger getInstance(String className, int lineNumber ) {
    if( instance == null )  {
      instance = setUpLogger(  className, lineNumber  );
    }
    return instance;
  }
  /**
   *  that return new Logger object
   *	if is the first time, or set new formatter to
   *	the existing logger object.
   */
  private static Logger setUpLogger( String ClassName, int LineNb ) {
    Logger lg = Logger.getLogger("MyLOgger") ;
    try {

      String logDirName = logFolderName;
      //System.out.println(logDirName );
      File logDir = new File(logDirName+"/");
      if( !(logDir.exists()) )
        logDir.mkdir();
      lg.setUseParentHandlers(false);

      lg.addHandler( newConsoleHandler(ClassName, LineNb) );
      lg.addHandler( newFileHandler(ClassName, LineNb, logFolderName ) );
    } catch (SecurityException | IOException e) {
      echo("Exception Utilities "+e.getMessage() );
    }
    return  lg;
  }
  /**
   * create new consoleHandler with new formatter and return it
   */
  private static ConsoleHandler newConsoleHandler( String ClassName, int LineNb) {
    ConsoleHandler ch = new ConsoleHandler();
    ch.setFormatter(new Formatter(ClassName, LineNb, "[%3$s:%4$d]%5$s%n"  )) ;
    return ch;
  }
  /**
   * create new fileHandler with new formatter and return it
   */
  private static FileHandler newFileHandler( String ClassName, int LineNb, String folder) throws SecurityException, IOException {
    FileHandler fh = new FileHandler( folder+"/"+"file.log" ,true );
    fh.setFormatter(new Formatter(ClassName, LineNb, "[%1$tF %1$tT][%2$-7s][%3$s:%4$d]%5$s%n"  ));
    return fh;
  }
  /**
   * echo : simple printf
   */
  public static void echo(Object obj) {
    if( obj == null ) System.out.println("NULL");
    else {
      String str = obj.toString() ;
      //if( ! str.endsWith("\n") )  str+="\n";
      System.out.println( str );
    }
  }

  /** @return The line number of the code that ran this method
   * @author Brian_Entei */
  public static String LineNb() {
    return ___8drrd3148796d_Xaf();
  }

  /** This methods name is ridiculous on purpose to prevent any other method
   * names in the stack trace from potentially matching this one.
   * 
   * @return The line number of the code that called the method that called
   *         this method(Should only be called by getLineNumber()).
   * @author Brian_Entei
   *
   * @Developer elhoussam
   * after understanding the magic :D in side  "___8drrd3148796d_Xaf "
   * i make same changes to return the lastlinenumber also the lastclassname of
   * any Method that calls :
   * Track.info(), Track.warning() , Track.error(true,)
   * to simplify the reuse of this class in the future ;)
   * */
  private static String ___8drrd3148796d_Xaf() {
    boolean thisOne = false;
    int thisOneCountDown = 2;
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for(StackTraceElement element : elements) {
      String methodName = element.getMethodName();
      int lineNum = element.getLineNumber();
      String className = element.getClassName();
      if(thisOne && (thisOneCountDown == 0)) {

        return className+"-"+ Integer.toString(lineNum) ;
      } else if(thisOne) {
        thisOneCountDown--;
      }
      if(methodName.equals("___8drrd3148796d_Xaf")) {
        thisOne = true;
      }

    }
    return "NULL";
  }
}
