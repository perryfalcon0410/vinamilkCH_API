package vn.viettel.core.logging;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFile {
//	static LogFile globalInstance = new LogFile();

	public static void logToFile(String logFile, String username, LogLevel logLevel, HttpServletRequest request, String message){
		if (logFile != null && !logFile.equals("")) {
			String ipAddress = "Not.Get.IP";
			String uri = "Not.Get.URI";

			if (username == null){
				username = "Not login";
			}

			if (request != null) {
				ipAddress = request.getHeader("X-FORWARDED-FOR");
				if (ipAddress == null) {
					ipAddress = request.getRemoteAddr();
				}
				uri = request.getRequestURI();
			}

			message = "[" + ipAddress + " - " + uri + " - " + username + "] " + getSource() + " - " + message;

			Logger logger = LoggerFactory.getLogger(logFile);

			if (LogLevel.DEBUG.equals(logLevel)) {
				logger.debug(message);
			} else if (LogLevel.ERROR.equals(logLevel)) {
				logger.error(message);
			} else if (LogLevel.INFO.equals(logLevel)) {
				logger.info(message);
			} else if (LogLevel.TRACE.equals(logLevel)) {
				logger.trace(message);
			} else if (LogLevel.WARN.equals(logLevel)) {
				logger.warn(message);
			}
		}
	}

	private static String getSource(){
		StackTraceElement e = null;
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i=1; i<stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(LogFile.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
				e = ste;
				break;
			}
		}
		String source = e == null ? "" : e.getClassName() + "." + e.getMethodName();
		String[] items = source.split("\\.");
		source = "";
		for(int i = 0; i < items.length; i++){
			if (i < items.length - 2)
				source += items[i].charAt(0) + ".";
			else if (i == items.length - 2)
				source += items[i] + ".";
			else
				source += items[i];
		}

		return source;
	}
}
