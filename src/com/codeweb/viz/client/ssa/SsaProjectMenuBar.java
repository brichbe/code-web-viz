package com.codeweb.viz.client.ssa;

import com.codeweb.viz.client.layout.NetworkLayoutManager;
import com.codeweb.viz.client.upload.SsaFileUploadPopupPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class SsaProjectMenuBar extends HorizontalPanel
{
  public SsaProjectMenuBar()
  {
    super();
    getElement().setId("ssaProjMenuPanel");

    final Button reloadSsaProjBtn = new Button("<img src=\"images/arrow_refresh.png\"/>", new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        SsaFileUploadPopupPanel.show(true);
      }
    });
    reloadSsaProjBtn.getElement().addClassName("ssaProjMenuPanelBtn");
    reloadSsaProjBtn.setTitle("Load a new SSA project");
    add(reloadSsaProjBtn);

    final Button toggleNetworkLayoutBtn = new Button("<img src=\"images/molecule.png\"/>");
    toggleNetworkLayoutBtn.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        boolean hierLayout = NetworkLayoutManager.toggleHierarchicalLayout();
        if (hierLayout)
        {
          toggleNetworkLayoutBtn.setHTML("<img src=\"images/molecule.png\"/>");
        }
        else
        {
          toggleNetworkLayoutBtn.setHTML("<img src=\"images/chart_organisation.png\"/>");
        }
      }
    });
    toggleNetworkLayoutBtn.getElement().addClassName("ssaProjMenuPanelBtn");
    toggleNetworkLayoutBtn.setTitle("Toggle network layout");
    add(toggleNetworkLayoutBtn);
  }

  // TODO: Change this to disable/enable
  public void show()
  {
    super.setVisible(true);
  }

  public void hide()
  {
    super.setVisible(false);
  }
}
