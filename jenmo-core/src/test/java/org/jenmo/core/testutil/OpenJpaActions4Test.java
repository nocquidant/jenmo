package org.jenmo.core.testutil;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.jenmo.core.orm.IllegalJpaSpiActionException;
import org.jenmo.core.orm.OpenJpaActions;

public class OpenJpaActions4Test extends JpaSpiActions4Test {
   private static final String strOpenJPAPersistence = "org.apache.openjpa.persistence.OpenJPAPersistence";

   private OpenJpaActions dg = new OpenJpaActions();

   @Override
   public EntityManager getEntityManager(Object pc) {
      return dg.getEntityManager(pc);
   }

   @Override
   public void persistAll(EntityManager em, Collection<?> pcs) {
      dg.persistAll(em, pcs);
   }

   @Override
   public void removeAll(EntityManager em, Collection<?> pcs) {
      dg.removeAll(em, pcs);
   }

   @Override
   public Connection getConnection(EntityManager em) {
      // return ((DelegatingConnection) OpenJPAPersistence.cast(em).getConnection()).getDelegate();
      try {
         Method cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
         Object openJpaEm = cast.invoke(null, em);
         Method getConnection = openJpaEm.getClass().getMethod("getConnection");
         Object delegatingConnection = getConnection.invoke(openJpaEm);
         Method getDelegate = delegatingConnection.getClass().getMethod("getDelegate");
         return (Connection) getDelegate.invoke(delegatingConnection);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public Object refresh(EntityManager em, Object pc) {
      throw new UnsupportedOperationException("TODO");
   }

   @Override
   public void evictAll(EntityManager em, Collection<?> pcs) {
      // OpenJPAPersistence.cast(em).evictAll(pcs);
      try {
         Method cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
         Object openJpa = cast.invoke(null, em);
         Method evictAll = openJpa.getClass().getMethod("evictAll", Collection.class);
         evictAll.invoke(openJpa, pcs);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public void evict(EntityManager em, Object pc) {
      // OpenJPAPersistence.cast(em).evict(pc);
      try {
         Method cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
         Object openJpa = cast.invoke(null, em);
         Method evictAll = openJpa.getClass().getMethod("evict", Object.class);
         evictAll.invoke(openJpa, pc);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public Collection<?> getManagedObjects(EntityManager em) {
      // return OpenJPAPersistence.cast(em).getManagedObjects();
      try {
         Method cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
         Object openJpa = cast.invoke(null, em);
         Method getManagedObjects = openJpa.getClass().getMethod("getManagedObjects");
         return (Collection<?>) getManagedObjects.invoke(openJpa);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public void releaseAll(EntityManager em, Collection<?> pcs) {
      // OpenJPAPersistence.cast(em).releaseAll(pcs);
      try {
         Method cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
         Object openJpa = cast.invoke(null, em);
         Method releaseAll = openJpa.getClass().getMethod("releaseAll", Collection.class);
         releaseAll.invoke(openJpa, pcs);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }
}
