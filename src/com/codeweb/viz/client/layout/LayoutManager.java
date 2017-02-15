package com.codeweb.viz.client.layout;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.DOM;

public class LayoutManager
{
  public static void init()
  {
    GwtToJsDispatch.initNetwork(DOM.getElementById("vizSourceStructureNetwork"));
    // TODO: BMB  - add menu/button bar with initial options to change layout, upload new ssa
  }

  public static void displayNetwork(String projName, String projDetails, JSONArray nodesArray, JSONArray edgesArray)
  {
    Element headerTitle = DOM.getElementById("headerTitle");
    headerTitle.setInnerHTML("CodeWeb Vizualization - <i>" + projName + "</i>");
    Element projDetailsEl = DOM.getElementById("headerProjectDetails");
    projDetailsEl.setInnerHTML(projDetails);
    GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
  }
}
