/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
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
            if(System.getProperty("catalina.home") != null)
                fileTxt = new FileHandler(System.getProperty("catalina.home")+"/logs/verigraph_log.txt");
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