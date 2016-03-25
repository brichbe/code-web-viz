package com.codeweb.viz.client;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

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

    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseURL() + "ssaServlet");
    try
    {
      builder.sendRequest(null, new RequestCallback()
      {
        public void onError(Request request, Throwable exception)
        {
          Window.alert(SERVER_ERROR);
        }

        public void onResponseReceived(Request request, Response response)
        {
          if (200 == response.getStatusCode())
          {
            JSONObject responseObj = new JSONObject(JsonUtils.safeEval(response.getText()));
            DOM.getElementById("headerTitle").setInnerHTML(
                "CodeWeb Vizualization - <i>" + responseObj.get("projName").isString().stringValue() + "</i>");
            JSONArray nodesArray = responseObj.get("nodes").isArray();
            JSONArray edgesArray = responseObj.get("edges").isArray();
            GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
          }
          else
          {
            Window.alert(SERVER_ERROR + " (" + response.getStatusText() + ")");
          }
        }
      });
    }
    catch (RequestException e)
    {
      Window.alert(SERVER_ERROR);
    }
  }
}
