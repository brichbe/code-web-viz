package com.codeweb.viz.client.util;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtil
{
  public static Widget createHrWithText(String text)
  {
    return new HTML("<div id=\"hrWithCenterText\">" + text + "</div>");
  }
}
