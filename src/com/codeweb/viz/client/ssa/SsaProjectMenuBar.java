package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.upload.SsaFileUploadPopupPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

// TODO: BMB - add menu/button bar with initial options to change layout, upload
// new ssa
public class SsaProjectMenuBar extends HorizontalPanel
{
  private final Button reloadSsaProjBtn;

  public SsaProjectMenuBar()
  {
    super();
    super.getElement().setId("ssaProjMenuPanel");

    reloadSsaProjBtn = new Button("<img src=\"images/arrow_refresh.png\"/>", new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        hide();
        SsaFileUploadPopupPanel.show();
      }
    });
    reloadSsaProjBtn.getElement().addClassName("ssaProjMenuPanelBtn");
    reloadSsaProjBtn.setTitle("Load a new SSA project");
    super.add(reloadSsaProjBtn);
  }

  public void show()
  {
    super.setVisible(true);
  }

  public void hide()
  {
    super.setVisible(false);
  }
}
