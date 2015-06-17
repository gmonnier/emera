package logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * WARNING : This class is loaded dynamically inside the configuration file given to the javaStyleLogger.
 */
public class CustomLogFormatter extends SimpleFormatter {
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");

	public CustomLogFormatter() {
		super();
	}

	public String format(LogRecord record) {
		
		// Create a StringBuffer to contain the formatted record
		// start with the date.
		StringBuffer sb = new StringBuffer();
		
		// Get the date from the LogRecord and add it to the buffer
		Date date = new Date(record.getMillis());
		sb.append(sdf.format(date));
		sb.append(" ");
		
		// Get the level name and add it to the buffer
		sb.append(record.getLevel().getName());
		sb.append(" ");
		
		sb.append(record.getSourceClassName());
		sb.append("        ");
		
		// Get the formatted message (includes localization 
		// and substitution of paramters) and add it to the buffer
		sb.append(formatMessage(record));
		sb.append("\n");

		return sb.toString();
	}
}