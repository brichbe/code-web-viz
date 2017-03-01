package com.codeweb.viz.server.applistener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.codeweb.viz.server.db.DbApi;

public class AppContextListener implements ServletContextListener
{
  public AppContextListener()
  {
    super();
  }

  @Override
  public void contextInitialized(ServletContextEvent arg0)
  {
    DbApi.init();
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0)
  {
    DbApi.close();
  }
}
