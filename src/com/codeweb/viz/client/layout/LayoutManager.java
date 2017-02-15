package com.codeweb.viz.client.layout;

import java.util.ArrayList;
import java.util.List;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;

public class LayoutManager
{
  public static void init()
  {
    GwtToJsDispatch.initNetwork(DOM.getElementById("vizSourceStructureNetwork"));
    // TODO: BMB  - add menu/button bar with initial options to change layout, upload new ssa
  }

  public static void displayNetwork(String projName, String projDetails, JSONArray nodesArray, JSONArray edgesArray)
  {
    GwtToJsDispatch.showNetworkIndeterminateProgress();

    Element headerTitle = DOM.getElementById("headerTitle");
    headerTitle.setInnerHTML("CodeWeb Vizualization - <i>\"" + projName + "\"</i>");
    Element projDetailsEl = DOM.getElementById("headerProjectDetails");
    projDetailsEl.setInnerHTML(projDetails);

    final List<JSONValue> nodeValues = new ArrayList<>();
    for (int i = 0; i < nodesArray.size(); i++)
    {
      nodeValues.add(nodesArray.get(i));
    }
    final List<JSONValue> edgeValues = new ArrayList<>();
    for (int i = 0; i < edgesArray.size(); i++)
    {
      edgeValues.add(edgesArray.get(i));
    }

    final int MAX_ANIMATE_CYCLES = 25;
    final int ANIMATE_RATE = 10;
    final int TIMER_MAX_ANIMATE_TIME = 1000;

    int totalNetworkObjs = nodeValues.size() + edgeValues.size();
    final int OBJS_PER_CYCLE = Math.max(1, Math.round(totalNetworkObjs / MAX_ANIMATE_CYCLES));

    final long nodesRepeaterStart = System.currentTimeMillis();
    final Timer nodesRepeater = new Timer()
    {
      @Override
      public void run()
      {
        if (nodeValues.isEmpty())
        {
          final long edgesRepeaterStart = System.currentTimeMillis();
          final Timer edgesRepeater = new Timer()
          {
            @Override
            public void run()
            {
              if (edgeValues.isEmpty())
              {
                GwtToJsDispatch.fitNetwork();
                GwtToJsDispatch.hideNetworkIndeterminateProgress();
                cancel();
                return;
              }
              int batchSize = (System.currentTimeMillis() - edgesRepeaterStart > TIMER_MAX_ANIMATE_TIME) ? edgeValues.size()
                  : Math.min(OBJS_PER_CYCLE, edgeValues.size());
              JSONArray arr = new JSONArray();
              for (int i = 0; i < batchSize; i++)
              {
                arr.set(arr.size(), edgeValues.remove(0));
              }
              GwtToJsDispatch.addEdges(arr.getJavaScriptObject());
            }
          };
          edgesRepeater.scheduleRepeating(ANIMATE_RATE);
          cancel();
          return;
        }
        int batchSize = (System.currentTimeMillis() - nodesRepeaterStart > TIMER_MAX_ANIMATE_TIME) ? nodeValues.size() : Math
            .min(OBJS_PER_CYCLE, nodeValues.size());
        JSONArray arr = new JSONArray();
        for (int i = 0; i < batchSize; i++)
        {
          arr.set(arr.size(), nodeValues.remove(0));
        }
        GwtToJsDispatch.addNodes(arr.getJavaScriptObject());
      }
    };
    nodesRepeater.scheduleRepeating(ANIMATE_RATE);
  }
}
