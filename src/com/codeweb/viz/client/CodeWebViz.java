package com.codeweb.viz.client;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CodeWebViz implements EntryPoint
{
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network " + "connection and try again.";

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    GwtToJsDispatch.buildNetwork(DOM.getElementById("vizSourceStructureNetwork"));

    JSONArray nodesArray = new JSONArray();
    JSONArray edgesArray = new JSONArray();

    JSONObject topPkg = new JSONObject();
    topPkg.put("id", new JSONNumber(0));
    topPkg.put("label", new JSONString("com"));
    topPkg.put("title", new JSONString("Package: <b>com</b><br>Sub-packages: <b>3</b>"));
    topPkg.put("group", new JSONNumber(0));
    nodesArray.set(0, topPkg);
    for (int i = 1; i < 4; i++)
    {
      JSONObject subPkg = new JSONObject();
      subPkg.put("id", new JSONNumber(i));
      subPkg.put("label", new JSONString("com." + i));
      subPkg.put("title", new JSONString("Package: <b>" + ("com." + i) + "</b><br>Sub-packages: <b>0</b><br>Classes: <b>3</b>"));
      subPkg.put("group", new JSONNumber(i));
      nodesArray.set(nodesArray.size(), subPkg);

      JSONObject edgeToTop = new JSONObject();
      edgeToTop.put("from", new JSONNumber(0));
      edgeToTop.put("to", new JSONNumber(i));
      edgesArray.set(edgesArray.size(), edgeToTop);
      for (int j = 101; j < 104; j++)
      {
        JSONObject clz = new JSONObject();
        clz.put("id", new JSONNumber(j * i));
        clz.put("label", new JSONString("class." + i + "." + j));
        clz.put("title", new JSONString("Class: <b>" + ("class." + i + "." + j) + "</b><br>SLOC: <b>23456</b>"));
        clz.put("group", new JSONNumber(i));
        nodesArray.set(nodesArray.size(), clz);

        JSONObject edgeToPkg = new JSONObject();
        edgeToPkg.put("from", new JSONNumber(i));
        edgeToPkg.put("to", new JSONNumber(j * i));
        edgesArray.set(edgesArray.size(), edgeToPkg);
      }
    }
    GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
  }
}
