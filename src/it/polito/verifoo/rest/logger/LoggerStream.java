package it.polito.verifoo.rest.logger;

import java.io.PrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggerStream extends PrintStream
{
private final Logger logger;

public LoggerStream(PrintStream realPrintStream,Logger logger2)
{
    super(realPrintStream);
    this.logger = logger2;
}
@Override
public void print(final String string) {
    logger.debug(string);
}
@Override
public void println(final String string) {
    logger.debug(string);
}

}