package com.codeweb.viz.client.js;

public class JsToGwtDispatch
{
  public static native void exposeMethods()
  /*-{
		$wnd.JsToGwtLoadSsaProject = function(id) {
			@com.codeweb.viz.client.ssa.SsaManager::handleLoadSsaProject(Ljava/lang/String;)(id);
		}
  }-*/;
}
