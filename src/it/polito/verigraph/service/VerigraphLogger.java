package it.polito.verigraph.service;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class VerigraphLogger {
	 static private FileHandler fileTxt;
	 static private SimpleFormatter formatterTxt;
	 private static VerigraphLogger verigraphlogger= new VerigraphLogger();
	public static Logger logger; 
    

    private VerigraphLogger() {

       

        // suppress the logging output to the console
        /*Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }*/
       
    	/*Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for(Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }*/
    	
    	LogManager.getLogManager().reset();
        

        // get the global logger to configure it
        logger = Logger.getLogger(VerigraphLogger.class.getName());
       // logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        try {
        	String path = System.getProperty("catalina.home");
        	if(path != null)
        		fileTxt = new FileHandler(path+"/logs/verigraph_log.txt");
        	else fileTxt= new FileHandler("verigraph_log.txt");
        	
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

     
    }
    
    public static VerigraphLogger getVerigraphlogger(){    	
    	return verigraphlogger;
    }
   
}