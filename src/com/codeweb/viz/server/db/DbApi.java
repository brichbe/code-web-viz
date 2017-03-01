package com.codeweb.viz.server.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import com.codeweb.viz.server.db.dao.SsaProjectDao;
import com.codeweb.viz.server.log.LoggerFactory;

public class DbApi
{
  private static final Logger LOG = LoggerFactory.createLogger(DbApi.class);

  private static EntityManagerFactory ssaProjectsEmf;

  public static synchronized void init()
  {
    LOG.info("Starting to init DB...");

    com.objectdb.Enhancer.enhance("com.codeweb.viz.server.db.dao.*");

    ssaProjectsEmf = Persistence.createEntityManagerFactory("$objectdb/db/ssaProjects.odb");
    LOG.info("Init complete");
  }

  public static synchronized void close()
  {
    LOG.info("Starting to close DB connection...");

    ssaProjectsEmf.close();
    LOG.info("Close complete");
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
