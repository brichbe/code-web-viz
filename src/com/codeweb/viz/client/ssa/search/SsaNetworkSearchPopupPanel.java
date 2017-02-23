package com.codeweb.viz.client.ssa.search;

import java.util.HashMap;
import java.util.Map;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SsaNetworkSearchPopupPanel extends PopupPanel
{
  private static SsaNetworkSearchPopupPanel INSTANCE;
  private static MultiWordSuggestOracle searchOracle;
  private static SuggestBox searchBox;
  private static final Map<String, String> searchableItemLabelToId = new HashMap<>();

  public static SsaNetworkSearchPopupPanel get()
  {
    if (INSTANCE == null)
    {
      INSTANCE = new SsaNetworkSearchPopupPanel();
    }
    return INSTANCE;
  }

  public void updateSearchableItems(Map<String, String> itemLabelToId)
  {
    searchableItemLabelToId.clear();
    searchableItemLabelToId.putAll(itemLabelToId);

    searchOracle.clear();
    searchOracle.addAll(itemLabelToId.keySet());
  }

  protected SsaNetworkSearchPopupPanel()
  {
    super(true);

    searchOracle = new MultiWordSuggestOracle();
    searchBox = new SuggestBox(searchOracle);
    searchBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>()
    {
      @Override
      public void onSelection(SelectionEvent<Suggestion> event)
      {
        GwtToJsDispatch.focusNetworkOnItem(searchableItemLabelToId.get(event.getSelectedItem().getReplacementString()));
        hide();
      }
    });

    final Button dismissBtn = new Button("<img src=\"images/cancel.png\" id=\"ssaSearchDismissBtnImg\"/>");
    dismissBtn.setTitle("Dismiss");
    dismissBtn.getElement().setId("ssaSearchDismissBtn");
    dismissBtn.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        hide();
      }
    });

    HorizontalPanel popupPanel = new HorizontalPanel();
    popupPanel.add(searchBox);
    popupPanel.add(dismissBtn);
    setWidget(popupPanel);
  }

  @Override
  public void show()
  {
    super.show();
    Timer t = new Timer()
    {
      @Override
      public void run()
      {
        searchBox.getValueBox().setFocus(true);
      }
    };
    t.schedule(250);
  }

}
