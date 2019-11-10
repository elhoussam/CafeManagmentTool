package me.elhoussam.util.log;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Formatter extends SimpleFormatter {
	
	private String format ="[%1$tF %1$tT] [%2$-7s] [%3$s:%4$d] %5$s %n";
	private String className="unkown";
	private int lineNumber=-1; 
	
	
	public Formatter(Object... argv) {
		className = (String) argv[0] ;
		this.lineNumber = (int) argv[1];
		this.format = (String) argv[2] ;
	}
	// format-console-handler
	// date 	level className lineNumber Message
	// disable	  *		argv[0]	 argv[1]	*
	// ============================================
	// format-file-handler
	// date 	level className lineNumber Message
	// enable	  *	   argv[0]	argv[1]		*
	
	@Override
	public synchronized String format(LogRecord lr) {
				return String.format(
				this.format, 
				new Date(lr.getMillis()), // %1$
				lr.getLevel().getLocalizedName(), // %2$ 
				className, lineNumber,			// %3$ %4$
				lr.getMessage()		// %5$
				);

	}
}