package com.codeweb.viz.client.ssa;

import java.util.HashMap;
import java.util.Map;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.layout.NetworkLayoutManager;
import com.codeweb.viz.client.ssa.search.SsaNetworkSearchPopupPanel;
import com.codeweb.viz.client.upload.SsaFileUploadPopupPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

// TODO: BMB - handle right clicks on network nodes with initial option to focus on
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

      Map<String, String> searchableItemLabelToId = new HashMap<>();
      JSONArray nodesArray = responseObj.get("nodes").isArray();
      for (int i = 0; i < nodesArray.size(); i++)
      {
        JSONObject nodeVal = nodesArray.get(i).isObject();
        searchableItemLabelToId.put(nodeVal.get("label").isString().stringValue(), nodeVal.get("id").isString().stringValue());
      }
      SsaNetworkSearchPopupPanel.get().updateSearchableItems(searchableItemLabelToId);
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
