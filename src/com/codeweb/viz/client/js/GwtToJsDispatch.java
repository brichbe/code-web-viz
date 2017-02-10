package com.codeweb.viz.client.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Handles the translation and dispatch of GWT/Java data/functions into invoking
 * the necessary Javascript functions.
 */
public class GwtToJsDispatch
{
  public static native void buildNetwork(Element container)
  /*-{
		$wnd.initNetwork(container);
  }-*/;
  
  public static native void setNetworkData(JavaScriptObject nodesJson, JavaScriptObject edgesJson)
  /*-{
        $wnd.setNetworkData(nodesJson, edgesJson);
  }-*/;
  
  public static native void logJson(JavaScriptObject json)
  /*-{
        console.log(JSON.stringify(json, null, 4));
  }-*/;
}
