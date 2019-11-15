package it.polito.verefoo.extra;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class Package1LoggingClass {

	private static Set<String> files = new HashSet<String>();
	static LoggerContext context = new LoggerContext();
	public static Logger createLoggerFor(String string, String file) {
		if (files.contains(file)) {
			return (Logger) context.getLogger(string);
		}
		files.add(file);
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();

		ple.setPattern("%date [%thread] %logger{10} %msg%n");
		ple.setContext(lc);
		ple.start();
		FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
		fileAppender.setFile(file);
		fileAppender.setEncoder(ple);
		fileAppender.setContext(lc);
		fileAppender.start();

		Logger logger = (Logger) LoggerFactory.getLogger(string);
		logger.addAppender(fileAppender);
		logger.setLevel(Level.DEBUG);
		logger.setAdditive(false); /* set to true if root should log too */

		return logger;
	}

}