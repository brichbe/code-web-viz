package com.codeweb.viz.client.layout;

import java.util.ArrayList;
import java.util.List;

import com.codeweb.viz.client.js.GwtToJsDispatch;
import com.codeweb.viz.client.ssa.SsaManager;
import com.codeweb.viz.client.ssa.SsaProjectMenuBar;
import com.codeweb.viz.client.ssa.SsaProjectNetworkData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

public class NetworkLayoutManager
{
  private static final SsaProjectMenuBar ssaProjectMenuBar = new SsaProjectMenuBar();

  private static boolean layoutAsHierarchical = true;

  public static void init()
  {
    GwtToJsDispatch.initNetwork(DOM.getElementById("vizSourceStructureNetwork"));
    RootPanel.get("contentArea").add(ssaProjectMenuBar);
    ssaProjectMenuBar.hide();
  }

  public static void clearNetwork()
  {
    GwtToJsDispatch.clearNetworkData();
  }

  public static void displayNetwork(SsaProjectNetworkData ssaNetworkData)
  {
    ssaProjectMenuBar.hide();

    Element headerTitle = DOM.getElementById("headerTitle");
    headerTitle.setInnerHTML("CodeWeb Vizualization - <i>\"" + ssaNetworkData.getProjectName() + "\"</i>");
    Element projDetailsEl = DOM.getElementById("headerProjectDetails");
    // TODO: BMB - format these numbers...
    projDetailsEl.setInnerHTML(ssaNetworkData.getNumPackages() + " packages,  " + ssaNetworkData.getNumSourceFiles()
        + " source files,  " + ssaNetworkData.getTotalSloc() + " total SLOC");

    final JSONArray nodesArray = ssaNetworkData.getNetworkNodes();
    final JSONArray edgesArray = ssaNetworkData.getNetworkEdges();
    final int totalNetworkObjs = nodesArray.size() + edgesArray.size();
    Scheduler.get().scheduleDeferred(new ScheduledCommand()
    {
      @Override
      public void execute()
      {
        if (totalNetworkObjs <= 250)
        {
          buildNetworkByChunks(nodesArray, edgesArray);
        }
        else
        {
          buildNetworkAllAtOnce(nodesArray, edgesArray);
        }
        ssaProjectMenuBar.show();
      }
    });
  }

  // TODO: BMB - Reassess this logic/performance with the next VisJs version.
  private static void buildNetworkAllAtOnce(final JSONArray nodesArray, final JSONArray edgesArray)
  {
    GwtToJsDispatch.showNetworkIndeterminateProgress();
    final Timer delayTimer = new Timer()
    {
      @Override
      public void run()
      {
        GwtToJsDispatch.addNodes(nodesArray.getJavaScriptObject());
        final Timer delayTimer2 = new Timer()
        {
          @Override
          public void run()
          {
            GwtToJsDispatch.addEdges(edgesArray.getJavaScriptObject());
            if (!layoutAsHierarchical)
            {
              GwtToJsDispatch.stabilizeNetwork();
            }
            GwtToJsDispatch.fitNetwork();
            GwtToJsDispatch.hideNetworkIndeterminateProgress();
          }
        };
        delayTimer2.schedule(250);
      }
    };
    delayTimer.schedule(250);
  }

  private static void buildNetworkByChunks(final JSONArray nodesArray, final JSONArray edgesArray)
  {
    GwtToJsDispatch.showNetworkDeterminateProgress();

    final int totalNetworkObjs = nodesArray.size() + edgesArray.size();
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
                if (!layoutAsHierarchical)
                {
                  GwtToJsDispatch.stabilizeNetwork();
                }
                GwtToJsDispatch.fitNetwork();
                GwtToJsDispatch.hideNetworkDeterminateProgress();
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
              GwtToJsDispatch
                  .setNetworkDeterminateProgress(1 - ((nodeValues.size() + edgeValues.size()) / (double) totalNetworkObjs));
              if (!layoutAsHierarchical)
              {
                GwtToJsDispatch.stabilizeNetwork();
              }
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
        GwtToJsDispatch.setNetworkDeterminateProgress(1 - ((nodeValues.size() + edgeValues.size()) / (double) totalNetworkObjs));
        if (!layoutAsHierarchical)
        {
          GwtToJsDispatch.stabilizeNetwork();
        }
      }
    };
    nodesRepeater.scheduleRepeating(ANIMATE_RATE);
  }

  public static boolean doLayoutAsHierarchical()
  {
    return layoutAsHierarchical;
  }

  public static boolean toggleHierarchicalLayout()
  {
    layoutAsHierarchical = !layoutAsHierarchical;
    clearNetwork();
    GwtToJsDispatch.toggleNetworkLayout(layoutAsHierarchical);
    SsaProjectNetworkData networkData = SsaManager.getLoadedSsaProject();
    if (networkData != null)
    {
      displayNetwork(networkData);
    }
    return layoutAsHierarchical;
  }
}
