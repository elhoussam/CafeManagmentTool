package me.elhoussam.util.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Tracking {

	/*
	 * these two logFolderName, logFolderNameChange to save
	 *	the chosing name of logfolder and insure will not 
	 *	be modified more then one time.
	 */
	private static String logFolderName ="logFolder";
	private static Boolean logFolderNameChange = false ;  
	/*
	 * these two lastclassname, lastlinenumber to follow the Tracking 
	 *	funcionality by saving the last lastclassname and lastlinenumber
	 *	to make sure there is one instance of the Logger instance 
	 */	
	private static String lastclassname = "";
	private static int lastlinenumber = -1;
	private static Boolean stateOfHandler = true ;
	/*
	 * instance, fh, ch : use to save and show all 
	 *	event that happend in the system into specific
	 *	file and show the update in the console.
	 */	
	private static Logger  instance = null;
	//	private static Handler fh =null, ch = null; 

	/*
	 * void info(String infoMsg)
	 *	public method which trace the infos of the system
	 */	

	public static void info(Boolean enable, String infoMsg) {

		String[] parts = Tracking.LineNb().split("-")  ;
		//Tracking.getInstance(parts[0],Integer.valueOf(parts[1]) ).info( infoMsg );
		Toggle(enable,parts[0],Integer.valueOf(parts[1])) ;
		instance.info( infoMsg );
		//fh.close();
	}

	private static void Toggle(Boolean sw, String classname, int linenumber) { 

		ConsoleHandler chLocal = (ConsoleHandler) instance.getHandlers()[0];
		if( sw ) { // enaable console handler by add it IF NOT EXIST
			((Formatter)chLocal.getFormatter()).setClassName(classname);
			((Formatter)chLocal.getFormatter()).setLineNumber(linenumber); 
			((Formatter)chLocal.getFormatter()).setFormat( "[%3$s:%4$d]%5$s%n" );			
		}else {
			((Formatter)chLocal.getFormatter()).setFormat("");
		}
		stateOfHandler = sw ;

		FileHandler fhLocal = (FileHandler) instance.getHandlers()[1];
		((Formatter)fhLocal.getFormatter()).setClassName(classname);
		((Formatter)fhLocal.getFormatter()).setLineNumber(linenumber); 
	}
	/*
	 * void warning(String infoMsg)
	 *	public method which trace the warning of the system
	 */
	public static void warning(Boolean enable, String warningMsg ) {

		String[] parts = Tracking.LineNb().split("-")  ;
		//Tracking.getInstance(parts[0],Integer.valueOf(parts[1]) ).warning( warningMsg );
		Toggle(enable,parts[0],Integer.valueOf(parts[1])) ;
		instance.warning( warningMsg );
		//fh.close();
	}
	/*
	 * void error(false,String infoMsg)
	 *	public method which trace the errors of the system
	 */
	public static void error(Boolean enable, String errorMsg ) {

		String[] parts = Tracking.LineNb().split("-")  ;
		//Tracking.getInstance( parts[0],Integer.valueOf(parts[1])).severe(errorMsg);
		Toggle(enable,parts[0],Integer.valueOf(parts[1])) ;
		instance.severe(errorMsg);
		//fh.close();
	}
	/*
	 * void setFolderName(String name)
	 *	public method : allow you to set new name folder
	 *	that contain the log file just for one time.
	 */
	public static void setFolderName(String name, Boolean consoleSwitch) {
		if ( !logFolderNameChange && !name.trim().isEmpty()  && ! name.trim().equalsIgnoreCase(logFolderName) ) {  
			logFolderName = "log_"+name.trim(); logFolderNameChange = true; }
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance( parts[0],Integer.valueOf(parts[1])) ; 
	}
	/*
	 * Logger (String ClassName, int lineNb )
	 *	private method : that return new Logger object 
	 *	if its the first you invoke this method, or will 
	 *	setup new formatter if the lastclassname, lastlinenumber 
	 *	is changed.
	 */
	private static Logger getInstance(String className, int lineNumber ) {
		if( instance == null )  {
			Tracking.lastclassname = className;  Tracking.lastlinenumber =lineNumber ;

			instance = setUpLogger(  className, lineNumber  );
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

	private static void clearAllHandler( Logger obj ) {

		for( Handler elem : obj.getHandlers()) {
			obj.removeHandler( elem );
		}

	}
	private static ConsoleHandler newConsoleHandler( String ClassName, int LineNb) {

		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new Formatter(ClassName, LineNb, "[%3$s:%4$d]%5$s%n"  )) ;
		return (ConsoleHandler) ch;
	}
	private static FileHandler newFileHandler( String ClassName, int LineNb, String folder) throws SecurityException, IOException {
		FileHandler fh = new FileHandler( folder+"/"+"file.log" ,true );
		fh.setFormatter(new Formatter(ClassName, LineNb, "[%1$tF %1$tT][%2$-7s][%3$s:%4$d]%5$s%n"  ));	
		return (FileHandler) fh;
	}
	public static void echo(Object obj) { 
		String str = obj.toString() ;
		//if( ! str.endsWith("\n") )  str+="\n";
		System.out.println( str );
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
	 * Track.info(), Track.warning() , Track.error(false,) 
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
