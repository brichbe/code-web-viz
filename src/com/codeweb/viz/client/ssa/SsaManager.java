package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.layout.NetworkLayoutManager;
import com.codeweb.viz.client.upload.SsaFileUploadPopupPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;

public class SsaManager
{
  public static void handleSsaFileLoaded(String ssaNetworkJson)
  {
    try
    {
      // TODO: BMB - format these numbers...
      JSONObject responseObj = new JSONObject(JsonUtils.safeEval(ssaNetworkJson));
      NetworkLayoutManager.displayNetwork(responseObj.get("projName").isString().stringValue(), responseObj.get("numPkgs").toString()
          + " packages,  " + responseObj.get("numSrcFiles").toString() + " source files,  "
          + responseObj.get("totalSloc").toString() + " total SLOC", responseObj.get("nodes").isArray(), responseObj.get("edges")
          .isArray());
    }
    catch (Exception e)
    {
      GWT.log("Failed to parse SSA file load response", e);
      GwtToJsDispatch.promptError("Data Error", "Unable to render the SSA network data. Try again or choose another file.");
      SsaFileUploadPopupPanel.show(false);
    }
  }
}
