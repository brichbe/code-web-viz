package com.codeweb.viz.server.log;

import javax.servlet.ServletContext;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;

public class LoggerFactory
{
  private static final String CONF_FILENAME = "/conf/log4j.xml";
  private static final String LOG_ROOT_KEY = "log_root";
  private static final String LOG_ROOT_DIR = "/log";

  public static synchronized void initLogging(ServletContext context)
  {
    if (context != null)
    {
      if (System.getProperty(LOG_ROOT_KEY) == null)
      {
        System.setProperty(LOG_ROOT_KEY, context.getRealPath(LOG_ROOT_DIR));
      }
      String fullPath = context.getRealPath(CONF_FILENAME);
      DOMConfigurator.configure(fullPath);
      Logger logger = createLogger(LoggerFactory.class);
      logger.info(System.lineSeparator() + "-------------------------" + System.lineSeparator() + "-- CodeWebViz Starting --"
          + System.lineSeparator() + "-------------------------");
      logger.info("Configured logger from path: " + fullPath);
    }
  }

  public static synchronized Logger createLogger(Class<?> clazz)
  {
    return org.slf4j.LoggerFactory.getLogger(clazz);
  }
}
