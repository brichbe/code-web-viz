package com.codeweb.viz.client.ssa;

import com.google.gwt.json.client.JSONArray;

public class SsaProjectNetworkData
{
  private final String projectName;
  private final int numPackages;
  private final int numSourceFiles;
  private final int totalSloc;
  private final JSONArray networkNodes;
  private final JSONArray networkEdges;

  public SsaProjectNetworkData(String projName, int numPkgs, int numSrcFiles, int sloc, JSONArray nodes, JSONArray edges)
  {
    projectName = projName;
    numPackages = numPkgs;
    numSourceFiles = numSrcFiles;
    totalSloc = sloc;
    networkNodes = nodes;
    networkEdges = edges;
  }

  public String getProjectName()
  {
    return projectName;
  }

  public int getNumPackages()
  {
    return numPackages;
  }

  public int getNumSourceFiles()
  {
    return numSourceFiles;
  }

  public int getTotalSloc()
  {
    return totalSloc;
  }

  public JSONArray getNetworkNodes()
  {
    return networkNodes;
  }

  public JSONArray getNetworkEdges()
  {
    return networkEdges;
  }
}
