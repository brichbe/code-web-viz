package com.codeweb.viz.client.util;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ImageMenuItem extends MenuItem
{
  public ImageMenuItem(String text, String imgPath, Command cmd)
  {
    super(text, cmd);
    this.setImgPath(imgPath);
  }

  public ImageMenuItem(String text, String imgPath, MenuBar menu)
  {
    super(text, false, menu);
    this.setImgPath(imgPath);
  }

  public void setImgPath(String imgPath)
  {
    super.setHTML("<img class=\"menuItemImage\" src=\"" + imgPath + "\"/><span class=\"menuItemWithImage\">" + super.getText()
        + "</span>");
  }
}
