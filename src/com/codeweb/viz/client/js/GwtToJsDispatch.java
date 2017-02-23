package com.codeweb.viz.client.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Handles the translation and dispatch of GWT/Java data/functions into invoking
 * the necessary Javascript functions.
 */
public class GwtToJsDispatch
{
  public static native void initNetwork(Element container)
  /*-{
		$wnd.initNetwork(container);
  }-*/;

  public static native void clearNetworkData()
  /*-{
		$wnd.clearNetworkData();
  }-*/;

  public static native void setNetworkData(JavaScriptObject nodesJson, JavaScriptObject edgesJson)
  /*-{
		$wnd.setNetworkData(nodesJson, edgesJson);
  }-*/;

  public static native void addNodes(JavaScriptObject nodesJson)
  /*-{
		$wnd.addNodes(nodesJson);
  }-*/;

  public static native void addEdges(JavaScriptObject edgesJson)
  /*-{
		$wnd.addEdges(edgesJson);
  }-*/;

  public static native void toggleNetworkLayout(boolean asHierarchical)
  /*-{
		$wnd.toggleNetworkLayout(asHierarchical);
  }-*/;

  public static native void fitNetwork()
  /*-{
		$wnd.fitNetwork();
  }-*/;

  public static native void stabilizeNetwork()
  /*-{
		$wnd.stabilizeNetwork();
  }-*/;

  public static native void focusNetworkOnItem(String id)
  /*-{
		$wnd.focusNetworkOnItem(id);
  }-*/;

  public static native void logJson(JavaScriptObject json)
  /*-{
		console.log(JSON.stringify(json, null, 4));
  }-*/;

  public static native void promptError(String title, String msg)
  /*-{
		$wnd.swal({
			title : title,
			text : msg,
			type : "error",
			animation : "pop"
		});
  }-*/;

  public static native void showNetworkIndeterminateProgress()
  /*-{
		$wnd.startNetworkIndeterminateProgress();
  }-*/;

  public static native void hideNetworkIndeterminateProgress()
  /*-{
		$wnd.stopNetworkIndeterminateProgress();
  }-*/;

  public static native void showNetworkDeterminateProgress()
  /*-{
		$wnd.startNetworkDeterminateProgress();
  }-*/;

  public static native void setNetworkDeterminateProgress(double percentage)
  /*-{
		$wnd.setNetworkDeterminateProgress(percentage);
  }-*/;

  public static native void hideNetworkDeterminateProgress()
  /*-{
		$wnd.stopNetworkDeterminateProgress();
  }-*/;
}
