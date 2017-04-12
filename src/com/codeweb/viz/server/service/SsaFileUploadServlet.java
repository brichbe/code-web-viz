package com.codeweb.viz.server.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import org.slf4j.Logger;

import com.codeweb.ssa.SSA;
import com.codeweb.ssa.model.ProjectStructure;
import com.codeweb.viz.server.db.DbApi;
import com.codeweb.viz.server.db.dao.SsaProjectDao;
import com.codeweb.viz.server.log.LoggerFactory;
import com.codeweb.viz.server.util.SsaConverter;

public class SsaFileUploadServlet extends HttpServlet
{
  private static final long serialVersionUID = 6479188937373775788L;

  private static final String TEMP_OUT_DIR = System.getProperty("java.io.tmpdir", "temp");
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
          ProjectStructure project = SsaConverter.parseSsaJson(uploadFileContents);
          long savedId = persist(project, uploadFileContents);
          resp.setContentType("text/html");
          resp.getWriter().write(String.valueOf(savedId));
          resp.getWriter().flush();
          break;
        }
        else if ("upload_compressed".equalsIgnoreCase(item.getFieldName()))
        {
          InputStream stream = item.openStream();

          ByteArrayOutputStream out = new ByteArrayOutputStream();
          int len;
          byte[] buffer = new byte[8192];
          while ((len = stream.read(buffer, 0, buffer.length)) != -1)
          {
            out.write(buffer, 0, len);
          }

          String extractTo = item.getName();
          File file = new File(TEMP_OUT_DIR, extractTo);
          try (FileOutputStream fileOut = new FileOutputStream(file))
          {
            out.writeTo(fileOut);
            SSA ssa = SSA.fromArchive(file.getAbsolutePath());
            ProjectStructure project = ssa.getProjStructure();
            long savedId = persist(project, SsaConverter.getJson(project));
            resp.setContentType("text/html");
            resp.getWriter().write(String.valueOf(savedId));
            resp.getWriter().flush();
          }
          finally
          {
            if (!file.delete())
            {
              file.deleteOnExit();
            }
          }
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

  private static long persist(ProjectStructure project, String projectJson)
  {
    Collection<SsaProjectDao> saved = DbApi.getAll();
    String projName = project.getProjName();
    projectJson = projectJson.replaceAll("\\s", "");

    long savedId = -1;
    for (SsaProjectDao s : saved)
    {
      if (projName.equals(s.getName()) && projectJson.equals(s.getJson()))
      {
        savedId = s.getId();
        break;
      }
    }
    if (savedId == -1)
    {
      LOG.info("Saving project: " + projName);
      savedId = DbApi.save(new SsaProjectDao(projName, projectJson));
    }
    return savedId;
  }
}
