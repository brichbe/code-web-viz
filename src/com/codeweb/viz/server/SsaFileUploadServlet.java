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
import org.slf4j.Logger;

import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.codeweb.viz.server.db.DbApi;
import com.codeweb.viz.server.db.dao.SsaProjectDao;
import com.codeweb.viz.server.log.LoggerFactory;
import com.google.gson.Gson;

// TODO: BMB - allow clients to open persisted instead of uploading a new SSA
// doc

// TODO: BMB - separate project that makes a customizable matrix animation
public class SsaFileUploadServlet extends HttpServlet
{
  private static final long serialVersionUID = 6479188937373775788L;

  private static final Logger LOG = LoggerFactory.createLogger(SsaFileUploadServlet.class);

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
          ProjectStructure project = parseSsaContents(uploadFileContents);
          persist(project, uploadFileContents);
          String json = StringEscapeUtils.escapeHtml4(createSsaNetworkJson(project).toString());
          resp.getWriter().write(json);
          resp.getWriter().flush();
          break;
        }
      }
    }
    catch (Exception e)
    {
      LOG.error("Error parsing SSA file: " + e.getMessage(), e);
      return;
    }
  }

  private static void persist(ProjectStructure project, String projectJson)
  {
    Collection<SsaProjectDao> saved = DbApi.getAll();
    String projName = project.getProjName();
    projectJson = projectJson.replaceAll("\\s", "");
    boolean found = false;
    for (SsaProjectDao s : saved)
    {
      if (projName.equals(s.getName()) && projectJson.equals(s.getJson()))
      {
        found = true;
        break;
      }
    }
    if (!found)
    {
      LOG.info("Saving project: " + projName);
      DbApi.save(new SsaProjectDao(projName, projectJson));
    }
  }

  private static ProjectStructure parseSsaContents(String ssaFileContents) throws Exception
  {
    return new Gson().fromJson(ssaFileContents, ProjectStructure.class);
  }

  private static JSONObject createSsaNetworkJson(ProjectStructure project) throws Exception
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
      edgesArray.put(edgeToPkg);
    }

    for (ProjectPackage subPkg : subPkgs)
    {
      createNetworkNodesAndEdges(nodesArray, edgesArray, subPkg, pkg);
    }
  }
}
