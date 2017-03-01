package com.codeweb.viz.server.db.dao;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SsaProjectDao implements Serializable
{
  private static final long serialVersionUID = -2360968958720282116L;
  @Id
  @GeneratedValue
  private long id;
  private String name;
  private String json;
  private long createDtg;

  public SsaProjectDao(String name, String json)
  {
    this.name = name;
    this.json = json;
    this.createDtg = System.currentTimeMillis();
  }

  public long getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getJson()
  {
    return json;
  }

  public long getCreateDtg()
  {
    return createDtg;
  }
}
