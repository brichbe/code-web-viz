package com.codeweb.viz.client.ssa.layout;

import com.codeweb.viz.client.ssa.search.SsaNetworkSearchPopupPanel;
import com.codeweb.viz.client.upload.SsaLoadProjectPopupPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

public class SsaProjectMenuBar extends HorizontalPanel
{
  public SsaProjectMenuBar()
  {
    super();
    getElement().setId("ssaProjMenuPanel");

    final Button reloadSsaProjBtn = new Button("<img src=\"images/arrow_refresh.png\" class=\"ssaProjMenuPanelBtnImg\"/>",
        new ClickHandler()
        {
          @Override
          public void onClick(ClickEvent event)
          {
            SsaLoadProjectPopupPanel.show(true);
          }
        });
    reloadSsaProjBtn.getElement().addClassName("ssaProjMenuPanelBtn");
    reloadSsaProjBtn.setTitle("Load a new SSA project");
    add(reloadSsaProjBtn);

    final Button toggleNetworkLayoutBtn = new Button("<img src=\"images/molecule.png\" class=\"ssaProjMenuPanelBtnImg\"/>");
    toggleNetworkLayoutBtn.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        boolean hierLayout = SsaNetworkLayoutManager.toggleHierarchicalLayout();
        if (hierLayout)
        {
          toggleNetworkLayoutBtn.setHTML("<img src=\"images/molecule.png\" class=\"ssaProjMenuPanelBtnImg\"/>");
        }
        else
        {
          toggleNetworkLayoutBtn.setHTML("<img src=\"images/chart_organisation.png\" class=\"ssaProjMenuPanelBtnImg\"/>");
        }
      }
    });
    toggleNetworkLayoutBtn.getElement().addClassName("ssaProjMenuPanelBtn");
    toggleNetworkLayoutBtn.setTitle("Toggle network layout");
    add(toggleNetworkLayoutBtn);

    final Button searchNetworkBtn = new Button("<img src=\"images/magnifier.png\" class=\"ssaProjMenuPanelBtnImg\"/>");
    searchNetworkBtn.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(final ClickEvent event)
      {
        SsaNetworkSearchPopupPanel.get().setPopupPositionAndShow(new PositionCallback()
        {
          @Override
          public void setPosition(int offsetWidth, int offsetHeight)
          {
            SsaNetworkSearchPopupPanel.get().setPopupPosition(event.getClientX() - offsetWidth + 20,
                event.getClientY() + offsetHeight);
          }
        });
      }
    });
    searchNetworkBtn.getElement().addClassName("ssaProjMenuPanelBtn");
    searchNetworkBtn.setTitle("Search");
    add(searchNetworkBtn);
  }

  public void setEnabled(boolean enabled)
  {
    for (int i = 0; i < getWidgetCount(); i++)
    {
      Widget w = getWidget(i);
      if (w instanceof FocusWidget)
      {
        ((FocusWidget) w).setEnabled(enabled);
      }
    }
  }
}
