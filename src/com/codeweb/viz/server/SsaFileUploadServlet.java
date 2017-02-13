package com.codeweb.viz.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.google.gson.Gson;

public class SsaFileUploadServlet extends HttpServlet
{
  private static final long serialVersionUID = 6479188937373775788L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    ServletFileUpload upload = new ServletFileUpload();
    try
    {
      FileItemIterator iter = upload.getItemIterator(req);
      while (iter.hasNext())
      {
        FileItemStream item = iter.next();
        if ("upload".equalsIgnoreCase(item.getFieldName()))
        {
          InputStream stream = item.openStream();

          ByteArrayOutputStream out = new ByteArrayOutputStream();
          int len;
          byte[] buffer = new byte[8192];
          while ((len = stream.read(buffer, 0, buffer.length)) != -1)
          {
            out.write(buffer, 0, len);
          }

          String uploadFileContents = new String(out.toByteArray());
          String json = StringEscapeUtils.escapeHtml4(parseSsaContents(uploadFileContents).toString());
          resp.getWriter().write(json);
          resp.getWriter().flush();
          break;
        }
      }
    }
    catch (Exception e)
    {
      System.err.println("Error parsing SSA file: " + e.getMessage());
      return;
    }
  }

  private static JSONObject parseSsaContents(String ssaFileContents) throws Exception
  {
    JSONObject responseObj = new JSONObject();
    ProjectStructure project = new Gson().fromJson(ssaFileContents, ProjectStructure.class);

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
