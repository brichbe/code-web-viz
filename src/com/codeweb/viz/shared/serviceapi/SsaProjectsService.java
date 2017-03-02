package com.codeweb.viz.shared.serviceapi;

import java.util.Collection;

import com.codeweb.viz.shared.dto.SavedSsaProjectDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ssaprojectsservice")
public interface SsaProjectsService extends RemoteService
{
  Collection<SavedSsaProjectDto> getSavedProjects();

  String getNetworkJsonForProject(String id) throws Exception;
}
