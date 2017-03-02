package com.codeweb.viz.client.ssa.layout;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.util.ImageMenuItem;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class NetworkNodeContextMenuPopup extends PopupPanel
{
  private final ClickHandler clickHandler = new ClickHandler()
  {
    @Override
    public void onClick(ClickEvent event)
    {
      // Sometimes the PopupPanel doesn't auto-hide like it should when the parent widget
      // is left-clicked, so ensure it's hidden when any click occurs while visible.
      hide();
    }
  };
  private HandlerRegistration clickHandlerReg;

  public NetworkNodeContextMenuPopup(final String id, final int clickX, final int clickY)
  {
    super(true);

    MenuBar menu = new MenuBar(true);
    setWidget(menu);

    MenuItem focusItem = new ImageMenuItem("Focus On", "images/focus_item.png", new Command()
    {
      @Override
      public void execute()
      {
        hide();
        GwtToJsDispatch.focusNetworkOnItem(id);
        GwtToJsDispatch.selectNetworkNode(id);
      }
    });
    menu.addItem(focusItem);

    setPopupPositionAndShow(new PositionCallback()
    {
      @Override
      public void setPosition(int offsetWidth, int offsetHeight)
      {
        int windowWidth = Window.getClientWidth();
        int windowHeight = Window.getClientHeight();
        int showX = clickX + 20;
        int showY = clickY + 20;
        if (clickX + offsetWidth > windowWidth)
        {
          int adjustedX = clickX - offsetWidth;
          if (adjustedX > 0)
          {
            showX = adjustedX;
          }
        }
        if (clickY + offsetHeight > windowHeight)
        {
          int adjustedY = clickY - offsetHeight;
          if (adjustedY > 0)
          {
            showY = adjustedY;
          }
        }
        setPopupPosition(showX, showY);
        clickHandlerReg = getParent().addDomHandler(clickHandler, ClickEvent.getType());
      }
    });
  }

  @Override
  public void hide()
  {
    this.clickHandlerReg.removeHandler();
    super.hide();
  }
}
