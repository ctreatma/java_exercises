package edu.upenn.cis555.mustang.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A singleton to access web server log 
 */
public class ServerLog {
	private static final String LOG_FILE = "server_maz.log";
	
	private static ServerLog instance;
	private File log;
	private PrintWriter writer;
	private Calendar calendar;
	private DateFormat dateFormat; 
	
	private ServerLog(String logPath) {
		calendar = GregorianCalendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		log = new File(logPath);
		try {
			writer = new PrintWriter(new FileOutputStream(log, true), true);
		} catch (FileNotFoundException ignored) { }
	}
	
	public synchronized static ServerLog getLog() {
		if (instance == null) {
			instance = new ServerLog(LOG_FILE);
		}
		return instance;
	}
	
	public synchronized void logMessage(String message) {
		calendar.setTimeInMillis(System.currentTimeMillis());
		writer.println(dateFormat.format(calendar.getTime()) + " " + message);
	}
	
	public void viewLog() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
			String line;
	        while ((line = input.readLine()) != null && line.length() != 0) {
	        	System.out.println(line);
	        }
	        input.close();
		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) { }
	}
	
	public void closeLog() {
		writer.close();
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
