package com.codeweb.viz.client.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.codeweb.viz.client.CodeWebViz;
import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.ssa.SsaManager;
import com.codeweb.viz.client.util.WidgetUtil;
import com.codeweb.viz.shared.dto.SavedSsaProjectDto;
import com.codeweb.viz.shared.serviceapi.SsaProjectsService;
import com.codeweb.viz.shared.serviceapi.SsaProjectsServiceAsync;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

// TODO: BMB - Also support uploading a zip file that contains src,
// extracts on server, and creates the SSA file from it,
// then proceeds to parse and display that network.
public class SsaLoadProjectPopupPanel
{
  public static final DateTimeFormat DATE_TIME_FORMAT_MED_NO_SECS = DateTimeFormat.getFormat("d MMMM yyyy HH:mm");
  private static final SsaProjectsServiceAsync ssaSvc = GWT.create(SsaProjectsService.class);
  private static boolean allowEscToClose = false;
  private static final PopupPanel popupPanel = new PopupPanel(false);
  private static final ListDataProvider<SavedSsaProjectDto> savedSsaProjectsDataProvider = new ListDataProvider<SavedSsaProjectDto>();
  static
  {
    popupPanel.setAnimationEnabled(false);
    popupPanel.setGlassEnabled(true);
    popupPanel.setModal(false);
    popupPanel.getElement().setId("fileUploadPopupPanel");

    VerticalPanel vPanel = new VerticalPanel();
    vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    vPanel.add(createSsaFileUploadPanel());
    vPanel.add(WidgetUtil.createHrWithText("OR"));
    vPanel.add(createSsaProjectsList());
    popupPanel.setWidget(vPanel);

    Event.addNativePreviewHandler(new Event.NativePreviewHandler()
    {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event)
      {
        if (allowEscToClose && popupPanel.isShowing())
        {
          if (Event.ONKEYDOWN == event.getTypeInt())
          {
            NativeEvent ne = event.getNativeEvent();
            if (KeyCodes.KEY_ESCAPE == ne.getKeyCode())
            {
              hide();
            }
          }
        }
      }
    });
  }

  private static Widget createSsaFileUploadPanel()
  {
    Label headerLabel = new Label("Upload Source Structure File (.ssa)");
    headerLabel.getElement().addClassName("fileUploadHeaderLabel");

    Image img = new Image("images/upload.png");
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
        final String result = event.getResults();
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

        hide();
        SsaManager.loadRemoteSsaProject(result);
      }
    });

    DockPanel mainPanel = new DockPanel();
    mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    mainPanel.setSpacing(8);
    mainPanel.add(headerLabel, DockPanel.NORTH);
    mainPanel.add(img, DockPanel.CENTER);
    mainPanel.add(fileUploadForm, DockPanel.SOUTH);
    return mainPanel;
  }

  private static Widget createSsaProjectsList()
  {
    Label headerLabel = new Label("Load an Existing SSA Project");
    headerLabel.getElement().addClassName("fileUploadHeaderLabel");

    CellList<SavedSsaProjectDto> ssaProjectsList = new CellList<SavedSsaProjectDto>(new SavedSsaProjectCell());
    ssaProjectsList.getElement().getStyle().setProperty("margin", "auto");
    ssaProjectsList.setTitle("Choose an existing project to load");
    ssaProjectsList.setSelectionModel(new SingleSelectionModel<SavedSsaProjectDto>());
    ssaProjectsList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
    savedSsaProjectsDataProvider.addDataDisplay(ssaProjectsList);

    Label emptyListLabel = new Label("No records found");
    emptyListLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
    ssaProjectsList.setEmptyListWidget(emptyListLabel);

    ScrollPanel scroller = new ScrollPanel(ssaProjectsList);
    scroller.setHeight("100px");
    scroller.getElement().getStyle().setProperty("border", "3px groove lightgray");
    scroller.getElement().getStyle().setProperty("borderRadius", "4px");
    scroller.getElement().getStyle().setMargin(8, Unit.PX);
    scroller.getElement().getStyle().setPadding(4, Unit.PX);

    VerticalPanel panel = new VerticalPanel();
    panel.add(headerLabel);
    panel.add(scroller);
    return panel;
  }

  private static void populateSavedSsaProjectsList()
  {
    ssaSvc.getSavedProjects(new AsyncCallback<Collection<SavedSsaProjectDto>>()
    {
      @Override
      public void onSuccess(Collection<SavedSsaProjectDto> result)
      {
        savedSsaProjectsDataProvider.getList().clear();
        List<SavedSsaProjectDto> newList = new ArrayList<SavedSsaProjectDto>(result);
        Collections.sort(newList, new Comparator<SavedSsaProjectDto>()
        {
          @Override
          public int compare(SavedSsaProjectDto o1, SavedSsaProjectDto o2)
          {
            return o1.getName().compareToIgnoreCase(o2.getName());
          }
        });
        savedSsaProjectsDataProvider.setList(newList);
      }

      @Override
      public void onFailure(Throwable caught)
      {
        GwtToJsDispatch.promptError("Failed retrieving saved projects", CodeWebViz.SERVER_ERROR);
      }
    });
  }

  public static synchronized void show(boolean allowClose)
  {
    allowEscToClose = allowClose;
    popupPanel.center();
    populateSavedSsaProjectsList();
  }

  public static synchronized void hide()
  {
    popupPanel.hide();
  }

  private static class SavedSsaProjectCell extends AbstractCell<SavedSsaProjectDto>
  {
    public SavedSsaProjectCell()
    {
      super();
    }

    @Override
    public void render(Context context, SavedSsaProjectDto value, SafeHtmlBuilder sb)
    {
      if (value != null)
      {
        sb.appendHtmlConstant("<table style='border-bottom: 1px solid rgb(150, 150, 150); margin-bottom: 6px; width: 100%'>");
        sb.appendHtmlConstant("<tr><td style='font-size:1.2em; font-weight: bold; color: white'>");
        sb.appendEscaped(value.getName());
        sb.appendHtmlConstant("</td><td rowspan='2' style='padding-left: 10px'>");
        sb.appendHtmlConstant("<a title=\"Load this project\" href=\"javascript:void(0);\" onclick=\"window.JsToGwtLoadSsaProject('"
            + String.valueOf(value.getId()) + "'); return true;" + "\">Load</a>");
        sb.appendHtmlConstant("</td></tr><tr><td>");
        sb.appendEscaped("Loaded  " + DATE_TIME_FORMAT_MED_NO_SECS.format(new Date(value.getCreateDtg())));
        sb.appendHtmlConstant("</td>");
        sb.appendHtmlConstant("</tr></table>");
      }
    }
  }
}
