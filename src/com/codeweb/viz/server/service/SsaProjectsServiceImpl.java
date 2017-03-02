package com.codeweb.viz.server.service;

import java.util.ArrayList;
import java.util.Collection;

import com.codeweb.viz.server.db.DbApi;
import com.codeweb.viz.server.db.dao.SsaProjectDao;
import com.codeweb.viz.server.util.SsaConverter;
import com.codeweb.viz.shared.dto.SavedSsaProjectDto;
import com.codeweb.viz.shared.serviceapi.SsaProjectsService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SsaProjectsServiceImpl extends RemoteServiceServlet implements SsaProjectsService
{
  private static final long serialVersionUID = -4470646301837008138L;

  public SsaProjectsServiceImpl()
  {
    super();
  }

  @Override
  public Collection<SavedSsaProjectDto> getSavedProjects()
  {
    Collection<SavedSsaProjectDto> result = new ArrayList<>();
    Collection<SsaProjectDao> saved = DbApi.getAll();
    for (SsaProjectDao dao : saved)
    {
      SavedSsaProjectDto dto = new SavedSsaProjectDto();
      dto.setId(dao.getId());
      dto.setName(dao.getName());
      dto.setCreateDtg(dao.getCreateDtg());
      result.add(dto);
    }
    return result;
  }

  @Override
  public String getNetworkJsonForProject(String id) throws Exception
  {
    SsaProjectDao saved = DbApi.getById(Long.valueOf(id).longValue());
    return SsaConverter.createSsaNetworkJson(SsaConverter.parseSsaJson(saved.getJson())).toString();
  }
}
