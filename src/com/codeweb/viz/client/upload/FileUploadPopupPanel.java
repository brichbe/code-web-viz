package com.codeweb.viz.client.upload;

import com.codeweb.viz.client.CodeWebViz;
import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileUploadPopupPanel
{
  private static final PopupPanel popupPanel = new PopupPanel(false);
  static
  {
    popupPanel.setAnimationEnabled(false);
    popupPanel.setGlassEnabled(true);
    popupPanel.setModal(true);
    popupPanel.getElement().setId("fileUploadPopupPanel");

    VerticalPanel headerPanel = new VerticalPanel();
    Label headerLabel = new Label("Upload Source Structure File (.ssa)");
    headerLabel.getElement().setId("fileUploadPopupPanelHeader");
    headerPanel.add(headerLabel);
    headerPanel.add(new HTML("<hr/>"));

    Image img = new Image("images/upload.png");
    img.setSize("64px", "64px");
    img.getElement().setId("fileUploadPopupPanelImg");
    img.setTitle("Choose file to upload");

    final FormPanel fileUploadForm = new FormPanel();
    fileUploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
    fileUploadForm.setMethod(FormPanel.METHOD_POST);
    fileUploadForm.setAction(GWT.getModuleBaseURL() + "ssaFileUpload");
    fileUploadForm.setVisible(false);
    final FileUpload fileUpload = new FileUpload();
    fileUpload.setName("upload");
    fileUpload.getElement().getStyle().setPosition(Position.RELATIVE);
    fileUploadForm.add(fileUpload);

    fileUpload.addChangeHandler(new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent event)
      {
        fileUploadForm.submit();
      }
    });

    img.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        fileUpload.click();
      }
    });

    // TODO: BMB - controls to change the network layout type
    fileUploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler()
    {
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event)
      {
        String result = event.getResults();
        if (result == null)
        {
          // TODO: BMB - here and elsewhere use sweet alert.
          Window.alert(CodeWebViz.SERVER_ERROR);
          return;
        }
        if (result.trim().isEmpty())
        {
          String filename = fileUpload.getFilename();
          int index = filename.lastIndexOf('\\');
          if (index != -1)
          {
            filename = filename.substring(index + 1);
          }
          index = filename.lastIndexOf('/');
          if (index != -1)
          {
            filename = filename.substring(index + 1);
          }
          Window.alert("The selected file (" + filename + ") is not a valid SSA document.");
          return;
        }

        hide();

        // TODO: BMB - refactor into manager class and use try/catch
        result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\\\/", "/");
        JSONObject responseObj = new JSONObject(JsonUtils.safeEval(result));
        DOM.getElementById("headerTitle").setInnerHTML(
            "CodeWeb Vizualization - <i>" + responseObj.get("projName").isString().stringValue() + "</i>");
        JSONArray nodesArray = responseObj.get("nodes").isArray();
        JSONArray edgesArray = responseObj.get("edges").isArray();
        GwtToJsDispatch.setNetworkData(nodesArray.getJavaScriptObject(), edgesArray.getJavaScriptObject());
      }
    });

    DockPanel mainPanel = new DockPanel();
    mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    mainPanel.setSpacing(8);
    mainPanel.add(headerPanel, DockPanel.NORTH);
    mainPanel.add(img, DockPanel.CENTER);
    mainPanel.add(fileUploadForm, DockPanel.SOUTH);

    popupPanel.setWidget(mainPanel);
  }

  public static synchronized void show()
  {
    popupPanel.center();
  }

  private static synchronized void hide()
  {
    popupPanel.hide();
  }
}
