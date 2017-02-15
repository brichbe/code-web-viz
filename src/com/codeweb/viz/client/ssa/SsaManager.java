package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.layout.LayoutManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;

public class SsaManager
{
  public static boolean handleSsaFileLoaded(String ssaNetworkJson)
  {
    boolean success = true;
    GwtToJsDispatch.showNetworkIndeterminateProgress();
    try
    {
      JSONObject responseObj = new JSONObject(JsonUtils.safeEval(ssaNetworkJson));
      LayoutManager.displayNetwork(responseObj.get("projName").toString(), responseObj.get("numPkgs").toString() + " packages,  "
          + responseObj.get("numSrcFiles").toString() + " source files,  " + responseObj.get("totalSloc").toString()
          + " total SLOC", responseObj.get("nodes").isArray(), responseObj.get("edges").isArray());
    }
    catch (Exception e)
    {
      GWT.log("Failed to parse SSA file load response", e);
      success = false;
    }
    finally
    {
      GwtToJsDispatch.hideNetworkIndeterminateProgress();
    }
    return success;

  }
}
