package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;

public class SsaManager
{

  public static boolean handleSsaFileLoaded(String ssaNetworkJson)
  {
    try
    {
      ssaNetworkJson = ssaNetworkJson.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\\\/", "/");
      JSONObject responseObj = new JSONObject(JsonUtils.safeEval(ssaNetworkJson));
      // TODO: BMB  - add menu/button bar with initial options to change layout, upload new ssa
      DOM.getElementById("headerTitle").setInnerHTML(
          "CodeWeb Vizualization - <i>" + responseObj.get("projName").isString().stringValue() + "</i>");
      JSONArray nodesArray = responseObj.get("nodes").isArray();
      JSONArray edgesArray = responseObj.get("edges").isArray();
      GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
      return true;
    }
    catch (Exception e)
    {
      GWT.log("Failed to parse SSA file load response", e);
    }
    return false;
  }
}
