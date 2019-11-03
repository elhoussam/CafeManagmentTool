package me.elhoussam.util;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Tracking {
	
	private static String logFolderName ="logFolder";
	private static Boolean logFolderNameChange = false ;
	private static String className = "";
	private static int lineNumber = -1;
	private static Logger  instance = null;
	private static Handler fh;
	private static Handler ch; 

	public static void info(String infoMsg) {
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance(parts[0],Integer.valueOf(parts[1]) ).info( infoMsg );
		fh.close();
	}
	public static void warning(String warningMsg) {
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance(parts[0],Integer.valueOf(parts[1]) ).warning( warningMsg );
		fh.close();
	}
	public static void error(String errorMsg) {
		String[] parts = Tracking.LineNb().split("-")  ;
		Tracking.getInstance( parts[0],Integer.valueOf(parts[1])).severe(errorMsg);
		fh.close();
	}

	public static void setFolderName(String name) {
		
		if ( !logFolderNameChange && !name.trim().isEmpty()  && ! name.trim().equalsIgnoreCase(logFolderName) ) { // default
			logFolderName = name.trim(); logFolderNameChange = true; }
	}
	private static Logger getInstance(String ClassName, int lineNb ) {
		if( instance == null || !Tracking.className.equals(ClassName) || !(Tracking.lineNumber==lineNb) )  {
			instance = setUpLogger( setUpFormatter(ClassName, lineNb) );
			Tracking.className = ClassName;  Tracking.lineNumber =lineNb ;
		}	
		return instance;
	}

	private static SimpleFormatter setUpFormatter(String ClassName, int LineNb ) {
		return   new SimpleFormatter() {
			private String format ="[%1$tF %1$tT] [%2$-7s] [%3$s:%4$d] %5$s %n";

			@Override
			public synchronized String format(LogRecord lr) {
						return String.format(format,
						new Date(lr.getMillis()),
						lr.getLevel().getLocalizedName(),
						ClassName,
						LineNb, 
						lr.getMessage()
						);
			}
		};
	}

	private static Logger setUpLogger( SimpleFormatter SP ) {

		Logger lg = Logger.getLogger("MyLOgger") ;
		try{
			String logDirName = logFolderName+"_logs";
			File logDir = new File(logDirName+"/"); 
			if( !(logDir.exists()) )
				logDir.mkdir();

			for( Handler elem : lg.getHandlers()) {
				lg.removeHandler( elem );
			}

			lg.setUseParentHandlers(false);

			ch = new ConsoleHandler();
			ch.setFormatter(SP) ;
			lg.addHandler( ch );

			fh = new FileHandler( logDirName+"/"+"file.log" ,true )  ;
			fh.setFormatter(SP);			
			lg.addHandler(fh);

		}catch( Exception e) {
			echo("Exception Utilities"+e.getMessage() );
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
	 * Track.info(), Track.error() 
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
