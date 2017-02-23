package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.layout.NetworkLayoutManager;
import com.codeweb.viz.client.upload.SsaFileUploadPopupPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;

public class SsaManager
{
  private static SsaProjectNetworkData loadedSsaProject = null;

  public static void handleSsaFileLoaded(String ssaNetworkJson)
  {
    try
    {
      JSONObject responseObj = new JSONObject(JsonUtils.safeEval(ssaNetworkJson));
      loadedSsaProject = new SsaProjectNetworkData(responseObj.get("projName").isString().stringValue(), (int) responseObj
          .get("numPkgs").isNumber().doubleValue(), (int) responseObj.get("numSrcFiles").isNumber().doubleValue(),
          (int) responseObj.get("totalSloc").isNumber().doubleValue(), responseObj.get("nodes").isArray(), responseObj.get(
              "edges").isArray());
      NetworkLayoutManager.clearNetwork();
      NetworkLayoutManager.displayNetwork(loadedSsaProject);
    }
    catch (Exception e)
    {
      GWT.log("Failed to parse SSA file load response", e);
      GwtToJsDispatch.promptError("Data Error", "Unable to render the SSA network data. Try again or choose another file.");
      SsaFileUploadPopupPanel.show(false);
    }
  }

  public static SsaProjectNetworkData getLoadedSsaProject()
  {
    return loadedSsaProject;
  }
}
