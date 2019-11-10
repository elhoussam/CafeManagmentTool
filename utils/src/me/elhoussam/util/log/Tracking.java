package me.elhoussam.util.log;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import me.elhoussam.util.sys.SecurityHandler;

public class Tracking {
	/*
	* these two logFolderName, logFolderNameChange to save
	*	the chosing name of logfolder and insure will not 
	*	be modified more then one time.
	*/
	private static String logFolderName ="logFolder";
	private static Boolean logFolderNameChange = false ; 
	/*
	* these two className, lineNumber to follow the Tracking 
	*	funcionality by saving the last className and lineNumber
	*	to make sure there is one instance of the Logger instance 
	*/	
	private static String className = "";
	private static int lineNumber = -1;
	/*
	* instance, fh, ch : use to save and show all 
	*	event that happend in the system into specific
	*	file and show the update in the console.
	*/	
	private static Logger  instance = null;
	private static Handler fh;
	private static Handler ch; 
	
	/*
	* void info(String infoMsg)
	*	public method which trace the infos of the system
	*/	

	public static void info(String infoMsg) {
		 
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance(parts[0],Integer.valueOf(parts[1]) ).info( infoMsg );
		fh.close();
	}
	/*
	* void warning(String infoMsg)
	*	public method which trace the warning of the system
	*/
	public static void warning(String warningMsg) {
		 
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance(parts[0],Integer.valueOf(parts[1]) ).warning( warningMsg );
		fh.close();
	}
	/*
	* void error(String infoMsg)
	*	public method which trace the errors of the system
	*/
	public static void error(String errorMsg) {
		 
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance( parts[0],Integer.valueOf(parts[1])).severe(errorMsg);
		fh.close();
	}
	/*
	* void setFolderName(String name)
	*	public method : allow you to set new name folder
	*	that contain the log file just for one time.
	*/
	public static void setFolderName(String name) {
		 
		if ( !logFolderNameChange && !name.trim().isEmpty()  && ! name.trim().equalsIgnoreCase(logFolderName) ) {  
			logFolderName = name.trim(); logFolderNameChange = true; }
	}
	/*
	* Logger (String ClassName, int lineNb )
	*	private method : that return new Logger object 
	*	if its the first you invoke this method, or will 
	*	setup new formatter if the className, lineNumber 
	*	is changed.
	*/
	private static Logger getInstance(String ClassName, int lineNb ) {
		if( instance == null || !Tracking.className.equals(ClassName) || !(Tracking.lineNumber==lineNb) )  {
			instance = setUpLogger(  ClassName, lineNb  );
			Tracking.className = ClassName;  Tracking.lineNumber =lineNb ;
		}	
		return instance;
	}
	/*
	*	Logger setUpLogger( SimpleFormatter SP )
	*	private method : that return new Logger object 
	*	if is the first time, or set new formatter to 
	*	the existing logger object.
	*/
	private static Logger setUpLogger( String ClassName, int LineNb ) {

		Logger lg = Logger.getLogger("MyLOgger") ;
		try{
			String logDirName = logFolderName+"_logs";
			System.out.println(logDirName );
			File logDir = new File(logDirName+"/"); 
			if( !(logDir.exists()) )
				logDir.mkdir();

			for( Handler elem : lg.getHandlers()) {
				lg.removeHandler( elem );
			}

			lg.setUseParentHandlers(false);

			ch = new ConsoleHandler();
			ch.setFormatter(new Formatter(ClassName, LineNb, "[%2$-7s][%3$s:%4$d]%5$s%n"  )) ;
			lg.addHandler( ch );

			fh = new FileHandler( logDirName+"/"+"file.log" ,true )  ;
			fh.setFormatter(new Formatter(ClassName, LineNb, "[%1$tF %1$tT][%2$-7s][%3$s:%4$d]%5$s%n"  ));			
			lg.addHandler(fh);

		}catch( Exception e) {
			echo("Exception Utilities "+e.getMessage() );
		}
		return  lg;
	}

	public static void echo(Object obj) { 
		System.out.println(obj.toString());
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
	 * i make same changes to return the lineNumber also the className of
	 * any Method that calls :
	 * Track.info(), Track.warning() , Track.error() 
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
