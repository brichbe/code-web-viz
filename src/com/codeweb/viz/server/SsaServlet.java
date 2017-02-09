package com.codeweb.viz.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.codeweb.ssa.util.FileIO;

public class SsaServlet extends HttpServlet
{
  private static final long serialVersionUID = -6580386317663342003L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    ProjectStructure project = FileIO.read(
        new File(getServletContext().getRealPath("data/ssa/Fibonacci.ssa")), ProjectStructure.class);

    PrintWriter out = resp.getWriter();

    JSONObject responseObj = new JSONObject();
    try
    {
      JSONArray nodesArray = new JSONArray();
      JSONArray edgesArray = new JSONArray();
      Collection<ProjectPackage> packages = project.getTopPackages();
      for (ProjectPackage pkg : packages)
      {
        createNetworkNodesAndEdges(nodesArray, edgesArray, pkg, null);
      }
      responseObj.put("projName", project.getProjName());
      responseObj.put("nodes", nodesArray);
      responseObj.put("edges", edgesArray);
    }
    catch (JSONException e)
    {
      throw new ServletException(e);
    }

    out.write(responseObj.toString());
    out.flush();
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
      srcNode.put("font", "24px arial #ffffff");
      nodesArray.put(srcNode);

      JSONObject edgeToPkg = new JSONObject();
      edgeToPkg.put("from", pkgId);
      edgeToPkg.put("to", srcId);
      edgesArray.put(edgeToPkg);
    }

    for (ProjectPackage subPkg : subPkgs)
    {
      createNetworkNodesAndEdges(nodesArray, edgesArray, subPkg, pkg);
    }
  }
}
