package com.codeweb.viz.client.js;

public class JsToGwtDispatch
{
  public static native void exposeMethods()
  /*-{
		$wnd.JsToGwtLoadSsaProject = function(id) {
			@com.codeweb.viz.client.upload.SsaLoadProjectPopupPanel::hide()();
			@com.codeweb.viz.client.ssa.SsaManager::loadRemoteSsaProject(Ljava/lang/String;)(id);
		}

		$wnd.JsToGwtHandleNetworkNodeRightClick = function(id, clickX, clickY) {
			@com.codeweb.viz.client.ssa.layout.SsaNetworkLayoutManager::handleNetworkNodeRightClick(Ljava/lang/String;II)(id, clickX, clickY);
		}
  }-*/;
}
