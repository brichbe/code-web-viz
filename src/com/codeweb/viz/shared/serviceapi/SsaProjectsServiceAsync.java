package com.codeweb.viz.shared.serviceapi;

import java.util.Collection;

import com.codeweb.viz.shared.dto.SavedSsaProjectDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SsaProjectsServiceAsync
{
  void getSavedProjects(AsyncCallback<Collection<SavedSsaProjectDto>> callback);

  void getNetworkJsonForProject(String id, AsyncCallback<String> callback);
}
