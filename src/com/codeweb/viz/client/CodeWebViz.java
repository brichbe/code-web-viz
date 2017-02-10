package com.codeweb.viz.client;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.RootPanel;

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

    // TODO: BMB - move this to an initial modal popup...
    final FormPanel fileUploadForm = new FormPanel();
    fileUploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
    fileUploadForm.setMethod(FormPanel.METHOD_POST);
    fileUploadForm.setAction(GWT.getModuleBaseURL() + "ssaFileUpload");
    FileUpload fileUpload = new FileUpload();
    fileUpload.setName("upload");
    fileUpload.getElement().getStyle().setPosition(Position.RELATIVE);
    fileUploadForm.add(fileUpload);
    RootPanel.get("contentArea").add(fileUploadForm);

    fileUpload.addChangeHandler(new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent event)
      {
        GWT.log("BMB - Submitting form on file upload change event");
        fileUploadForm.submit();
        // TODO: BMB - hide the form...
      }
    });

    fileUploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler()
    {
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event)
      {
        String result = event.getResults();
        if (result == null)
        {
          Window.alert(SERVER_ERROR);
          return;
        }
        GWT.log("Got result:");
        GWT.log(result);
        GWT.log("" + result.length());
        GWT.log("" + result.indexOf("projName"));
//        GWT.log(JsonUtils.escapeJsonForEval(result));
//        GWT.log(JsonUtils.escapeValue(result));
        GwtToJsDispatch.logJson(JsonUtils.safeEval(JsonUtils.escapeValue(result)));
        GwtToJsDispatch.logJson(JsonUtils.unsafeEval(JsonUtils.escapeValue(result)));
        JSONObject responseObj = JSONParser.parseLenient(JsonUtils.escapeValue(result)).isObject(); //new JSONObject(JsonUtils.unsafeEval(JsonUtils.escapeValue(result)));
        GwtToJsDispatch.logJson(responseObj.getJavaScriptObject());
        for(String key : responseObj.keySet())
        {
          GWT.log("Key: " + key);
          GWT.log("Value: " + responseObj.get(key));
        }
        DOM.getElementById("headerTitle").setInnerHTML(
            "CodeWeb Vizualization - <i>" + responseObj.get("projName").isString().stringValue() + "</i>");
        JSONArray nodesArray = responseObj.get("nodes").isArray();
        JSONArray edgesArray = responseObj.get("edges").isArray();
        GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
      }
    });
  }
}
