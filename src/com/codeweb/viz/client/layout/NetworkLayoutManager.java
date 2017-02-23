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
import com.google.gwt.i18n.client.NumberFormat;
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
    ssaProjectMenuBar.setEnabled(false);
  }

  public static void clearNetwork()
  {
    GwtToJsDispatch.clearNetworkData();
  }

  public static void displayNetwork(SsaProjectNetworkData ssaNetworkData)
  {
    ssaProjectMenuBar.setEnabled(false);

    Element headerTitle = DOM.getElementById("headerTitle");
    headerTitle.setInnerHTML("CodeWeb Vizualization - <i>\"" + ssaNetworkData.getProjectName() + "\"</i>");
    Element projDetailsEl = DOM.getElementById("headerProjectDetails");
    projDetailsEl.setInnerHTML(NumberFormat.getDecimalFormat().format(ssaNetworkData.getNumPackages()) + " packages,  "
        + NumberFormat.getDecimalFormat().format(ssaNetworkData.getNumSourceFiles()) + " source files,  "
        + NumberFormat.getDecimalFormat().format(ssaNetworkData.getTotalSloc()) + " total SLOC");

    final JSONArray nodesArray = ssaNetworkData.getNetworkNodes();
    final JSONArray edgesArray = ssaNetworkData.getNetworkEdges();
    final int totalNetworkObjs = nodesArray.size() + edgesArray.size();
    final NetworkBuiltCallback completionCallback = new NetworkBuiltCallback()
    {
      @Override
      public void onNetworkBuildComplete()
      {
        ssaProjectMenuBar.setEnabled(true);
      }
    };
    Scheduler.get().scheduleDeferred(new ScheduledCommand()
    {
      @Override
      public void execute()
      {
        if (totalNetworkObjs <= 250)
        {
          buildNetworkByChunks(nodesArray, edgesArray, completionCallback);
        }
        else
        {
          buildNetworkAllAtOnce(nodesArray, edgesArray, completionCallback);
        }
      }
    });
  }

  // TODO: BMB - Reassess this logic/performance with the next VisJs version.
  private static void buildNetworkAllAtOnce(final JSONArray nodesArray, final JSONArray edgesArray,
      final NetworkBuiltCallback callback)
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
            callback.onNetworkBuildComplete();
          }
        };
        delayTimer2.schedule(250);
      }
    };
    delayTimer.schedule(250);
  }

  private static void buildNetworkByChunks(final JSONArray nodesArray, final JSONArray edgesArray,
      final NetworkBuiltCallback callback)
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
                callback.onNetworkBuildComplete();
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
    ssaProjectMenuBar.setEnabled(false);
    clearNetwork();
    GwtToJsDispatch.toggleNetworkLayout(layoutAsHierarchical);
    SsaProjectNetworkData networkData = SsaManager.getLoadedSsaProject();
    if (networkData != null)
    {
      displayNetwork(networkData);
    }
    return layoutAsHierarchical;
  }

  private static interface NetworkBuiltCallback
  {
    void onNetworkBuildComplete();
  }
}
