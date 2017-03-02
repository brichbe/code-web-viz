package com.codeweb.viz.client.js;

public class JsToGwtDispatch
{
  public static native void exposeMethods()
  /*-{
		$wnd.JsToGwtLoadSsaProject = function(id) {
			@com.codeweb.viz.client.upload.SsaLoadProjectPopupPanel::hide()();
			@com.codeweb.viz.client.ssa.SsaManager::loadSsaProject(Ljava/lang/String;)(id);
		}
  }-*/;
}
