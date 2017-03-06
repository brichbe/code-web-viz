package com.codeweb.viz.client.ssa;

import java.util.HashMap;
import java.util.Map;

import com.codeweb.viz.client.CodeWebViz;
import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.ssa.layout.SsaNetworkLayoutManager;
import com.codeweb.viz.client.ssa.search.SsaNetworkSearchPopupPanel;
import com.codeweb.viz.client.upload.SsaLoadProjectPopupPanel;
import com.codeweb.viz.shared.serviceapi.SsaProjectsService;
import com.codeweb.viz.shared.serviceapi.SsaProjectsServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SsaManager
{
  private static final SsaProjectsServiceAsync ssaSvc = GWT.create(SsaProjectsService.class);
  private static SsaProjectNetworkData loadedSsaProject = null;

  public static void loadRemoteSsaProject(String id)
  {
    ssaSvc.getNetworkJsonForProject(id, new AsyncCallback<String>()
    {
      @Override
      public void onSuccess(String result)
      {
        handleSsaFileLoaded(result);
      }

      @Override
      public void onFailure(Throwable caught)
      {
        GWT.log("Failed to get network JSON", caught);
        GwtToJsDispatch.promptError("Failed to load network data", CodeWebViz.SERVER_ERROR);
      }
    });
  }

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
      SsaNetworkLayoutManager.clearNetwork();
      SsaNetworkLayoutManager.displayNetwork(loadedSsaProject);
    }
    catch (Exception e)
    {
      GWT.log("Failed to parse SSA file load response", e);
      GwtToJsDispatch.promptError("Failed to render the SSA network data", CodeWebViz.SERVER_ERROR);
      SsaLoadProjectPopupPanel.show(false);
    }
  }

  public static SsaProjectNetworkData getLoadedSsaProject()
  {
    return loadedSsaProject;
  }
}
