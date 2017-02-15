package com.codeweb.viz.client.upload;

import com.codeweb.viz.client.CodeWebViz;
import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.ssa.SsaManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

// TODO: BMB - Also support uploading a zip file that contains src,
// extracts on server, and creates the SSA file from it,
// then proceeds to parse and display that network.
public class SsaFileUploadPopupPanel
{
  private static final PopupPanel popupPanel = new PopupPanel(false);
  static
  {
    popupPanel.setAnimationEnabled(false);
    popupPanel.setGlassEnabled(true);
    popupPanel.setModal(false);
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

    fileUploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler()
    {
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event)
      {
        String result = event.getResults();
        if (result == null)
        {
          GwtToJsDispatch.promptError("Connection Error", CodeWebViz.SERVER_ERROR);
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
          GwtToJsDispatch.promptError("Invalid File", "The selected file (" + filename + ") is not a valid SSA document.");
          return;
        }

        result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\\\/", "/");
        if (SsaManager.handleSsaFileLoaded(result))
        {
          hide();
        }
        else
        {
          GwtToJsDispatch.promptError("Data Error", "Unable to render the SSA network data. Try again or choose another file.");
        }
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
