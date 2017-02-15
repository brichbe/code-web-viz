package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;

public class SsaManager
{

  public static boolean handleSsaFileLoaded(String ssaNetworkJson)
  {
    GwtToJsDispatch.showIndeterminateProgress();
    try
    {
      ssaNetworkJson = ssaNetworkJson.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\\\/", "/");
      JSONObject responseObj = new JSONObject(JsonUtils.safeEval(ssaNetworkJson));
      // TODO: BMB  - add menu/button bar with initial options to change layout, upload new ssa
      Element headerTitle = DOM.getElementById("headerTitle");
      headerTitle.setInnerHTML("CodeWeb Vizualization - <i>" + responseObj.get("projName").isString().stringValue() + "</i>");
      Element projDetails = DOM.getElementById("headerProjectDetails");
      projDetails.setInnerHTML(responseObj.get("numPkgs").toString() + " packages,  " + responseObj.get("numSrcFiles").toString()
          + " source files,  " + responseObj.get("totalSloc").toString() + " total SLOC");
      headerTitle.appendChild(projDetails);
      JSONArray nodesArray = responseObj.get("nodes").isArray();
      JSONArray edgesArray = responseObj.get("edges").isArray();
      GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
      return true;
    }
    catch (Exception e)
    {
      GWT.log("Failed to parse SSA file load response", e);
    }
    finally
    {
      GwtToJsDispatch.hideIndeterminateProgress();
    }
    return false;
  }
}
