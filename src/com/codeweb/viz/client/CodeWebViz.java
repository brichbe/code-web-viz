package com.codeweb.viz.client;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CodeWebViz implements EntryPoint
{
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network " + "connection and try again.";

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    GwtToJsDispatch.buildNetwork(DOM.getElementById("vizSourceStructureNetwork"));
  }
}
