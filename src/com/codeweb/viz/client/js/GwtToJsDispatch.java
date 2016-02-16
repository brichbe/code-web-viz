package com.codeweb.viz.client.js;

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
}
