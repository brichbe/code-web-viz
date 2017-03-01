package com.codeweb.viz.server.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.codeweb.viz.server.db.dao.SsaProjectDao;

// TODO: BMB - here and elsewhere use logger

// TODO: BMB - commit lib jars
public class DbApi
{
  private static EntityManagerFactory ssaProjectsEmf;

  public static synchronized void init()
  {
    com.objectdb.Enhancer.enhance("com.codeweb.viz.server.db.dao.*");

    ssaProjectsEmf = Persistence.createEntityManagerFactory("$objectdb/db/ssaProjects.odb");
  }

  public static synchronized void close()
  {
    ssaProjectsEmf.close();
  }

  public static synchronized void save(SsaProjectDao ssaDao)
  {
    EntityManager em = ssaProjectsEmf.createEntityManager();
    try
    {
      em.getTransaction().begin();
      em.persist(ssaDao);
      em.getTransaction().commit();
    }
    finally
    {
      if (em.getTransaction().isActive())
      {
        em.getTransaction().rollback();
      }
      em.close();
    }
  }

  public static synchronized Collection<SsaProjectDao> getAll()
  {
    EntityManager em = ssaProjectsEmf.createEntityManager();
    try
    {
      return em.createQuery("SELECT s FROM SsaProjectDao s", SsaProjectDao.class).getResultList();
    }
    finally
    {
      if (em.getTransaction().isActive())
      {
        em.getTransaction().rollback();
      }
      em.close();
    }
  }
}
