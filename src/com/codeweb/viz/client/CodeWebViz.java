package com.codeweb.viz.client;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.upload.SsaFileUploadPopupPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CodeWebViz implements EntryPoint
{
  public static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network " + "connection and try again.";

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    Window.enableScrolling(false);
    GwtToJsDispatch.buildNetwork(DOM.getElementById("vizSourceStructureNetwork"));

    SsaFileUploadPopupPanel.show();
  }
}
