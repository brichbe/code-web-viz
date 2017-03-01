package com.codeweb.viz.shared.dto;

import java.io.Serializable;

public class SavedSsaProjectDto implements Serializable
{
  private static final long serialVersionUID = 6005819616435164138L;
  private long id;
  private String name;
  private long createDtg;

  public SavedSsaProjectDto()
  {
    // Empty.
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getCreateDtg()
  {
    return createDtg;
  }

  public void setCreateDtg(long createDtg)
  {
    this.createDtg = createDtg;
  }
}
