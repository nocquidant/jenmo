package org.jenmo.core.testutil;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.jenmo.core.orm.EclipseLinkActions;
import org.jenmo.core.orm.IllegalJpaSpiActionException;

public class EclipseLinkActions4Test extends JpaSpiActions4Test {
   private static final String strEclipseLinkPersistence = "org.eclipse.persistence.jpa.JpaHelper";

   private EclipseLinkActions dg = new EclipseLinkActions();

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
      // return JpaHelper.getEntityManager(em).getServerSession().getAccessor().getConnection();

      try {
         Method getEntityManager = Class.forName(strEclipseLinkPersistence).getMethod(
               "getEntityManager", EntityManager.class);
         Object eclipseLinkEm = getEntityManager.invoke(null, em);
         Method getServerSession = eclipseLinkEm.getClass().getMethod("getServerSession");
         Object serverSession = getServerSession.invoke(eclipseLinkEm);
         Method getAccessor = serverSession.getClass().getMethod("getAccessor");
         Object accessor = getAccessor.invoke(serverSession);
         Method getConnection = accessor.getClass().getMethod("getConnection");
         return (Connection) getConnection.invoke(accessor);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public Object refresh(EntityManager em, Object pc) {
      try {
         Method getEntityManager = Class.forName(strEclipseLinkPersistence).getMethod(
               "getEntityManager", EntityManager.class);
         Object eclipseLinkEm = getEntityManager.invoke(null, em);
         Method refresh = eclipseLinkEm.getClass().getMethod("refresh", Object.class);
         return refresh.invoke(eclipseLinkEm, pc);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public void evictAll(EntityManager em, Collection<?> pcs) {
      for (Object each : pcs) {
         evict(em, each);
      }
   }

   @Override
   public void evict(EntityManager em, Object pc) {
      // Do nothing
   }

   @Override
   public Collection<?> getManagedObjects(EntityManager em) {
      throw new UnsupportedOperationException("TODO");
   }

   @Override
   public void releaseAll(EntityManager em, Collection<?> pcs) {
      // Do nothing
   }
}