package com.codeweb.viz.server.util;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.google.gson.Gson;

public class SsaConverter
{
  public static ProjectStructure parseSsaJson(String ssaJson) throws Exception
  {
    return new Gson().fromJson(ssaJson, ProjectStructure.class);
  }

  public static String getJson(ProjectStructure project)
  {
    return new Gson().toJson(project);
  }

  public static JSONObject createSsaNetworkJson(ProjectStructure project) throws Exception
  {
    JSONArray nodesArray = new JSONArray();
    JSONArray edgesArray = new JSONArray();
    Collection<ProjectPackage> packages = project.getTopPackages();
    for (ProjectPackage pkg : packages)
    {
      createNetworkNodesAndEdges(nodesArray, edgesArray, pkg, null);
    }

    int numSrcFiles = 0, totalSloc = 0;
    for (int i = 0; i < nodesArray.length(); i++)
    {
      JSONObject node = nodesArray.getJSONObject(i);
      if (node.optBoolean("isSrcNode", false))
      {
        numSrcFiles++;
        totalSloc += node.getInt("slocCount");
      }
    }

    JSONObject responseObj = new JSONObject();
    responseObj.put("numPkgs", nodesArray.length() - numSrcFiles);
    responseObj.put("numSrcFiles", numSrcFiles);
    responseObj.put("totalSloc", totalSloc);
    responseObj.put("projName", project.getProjName());
    responseObj.put("nodes", nodesArray);
    responseObj.put("edges", edgesArray);

    return responseObj;
  }

  private static void createNetworkNodesAndEdges(JSONArray nodesArray, JSONArray edgesArray, ProjectPackage pkg,
      ProjectPackage parentPkg) throws JSONException
  {
    String pkgName = pkg.getName();
    String pkgId = pkgName;
    Collection<ProjectSrcFile> srcFiles = pkg.getSrcFiles();
    Collection<ProjectPackage> subPkgs = pkg.getSubPackages();

    JSONObject pkgNode = new JSONObject();
    pkgNode.put("id", pkgId);
    pkgNode.put("label", pkgName);
    pkgNode.put("title", "Package: <b>" + pkgName + "</b><br>Sub-packages: <b>" + subPkgs.size() + "</b><br>Classes: <b>"
        + srcFiles.size() + "</b>");
    pkgNode.put("group", pkgId);
    nodesArray.put(pkgNode);

    if (parentPkg != null)
    {
      JSONObject edgeToParent = new JSONObject();
      edgeToParent.put("from", parentPkg.getName());
      edgeToParent.put("to", pkgId);
      edgeToParent.put("selectionWidth", 3);
      edgesArray.put(edgeToParent);
    }

    for (ProjectSrcFile srcFile : srcFiles)
    {
      JSONObject srcNode = new JSONObject();
      String filename = srcFile.getClassName() + "." + srcFile.getFileExt();
      String srcId = pkgName + "." + filename;
      srcNode.put("id", srcId);
      srcNode.put("label", filename);
      srcNode.put("title", "Class: <b>" + srcFile.getClassName() + "</b><br>SLOC: <b>" + srcFile.getSlocCount() + "</b>");
      srcNode.put("group", pkgId);
      srcNode.put("shape", "image");
      srcNode.put("image", "images/java_file.png");
      srcNode.put("size", "16");
      srcNode.put("font", "16px arial #ffffff");
      srcNode.put("isSrcNode", true);
      srcNode.put("slocCount", srcFile.getSlocCount());
      nodesArray.put(srcNode);

      JSONObject edgeToPkg = new JSONObject();
      edgeToPkg.put("from", pkgId);
      edgeToPkg.put("to", srcId);
      edgeToPkg.put("selectionWidth", 3);
      edgesArray.put(edgeToPkg);
    }

    for (ProjectPackage subPkg : subPkgs)
    {
      createNetworkNodesAndEdges(nodesArray, edgesArray, subPkg, pkg);
    }
  }
}
