package org.jenmo.core.testutil;

import java.sql.Connection;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.jenmo.core.config.JenmoConfig;
import org.jenmo.core.orm.JpaSpiActions;

public abstract class JpaSpiActions4Test extends JpaSpiActions {

   private static JpaSpiActions4Test instance;

   public static JpaSpiActions4Test getInstance() {
      JpaSpiActions4Test out = instance;
      if (out == null) { // First check no locking
         synchronized (JpaSpiActions4Test.class) {
            out = instance;
            if (out == null) { // Second check with locking
               Object value = JenmoConfig.getInstance().getValue(JenmoConfig.OPTION_JPA_PROVIDER)
                     .get();
               if (value.equals("openjpa")) {
                  instance = out = new OpenJpaActions4Test();
               } else if (value.equals("eclipselink")) {
                  instance = out = new EclipseLinkActions4Test();
               }
            }
         }
      }
      return instance;
   }

   public synchronized static void setInstance(JpaSpiActions4Test instance) {
      JpaSpiActions4Test.instance = instance;
   }

   /**
    * Return the entity manager for the given object, if one can be determined from just the object
    * alone.
    */
   public abstract EntityManager getEntityManager(Object pc);

   /** Persist the given objects. */
   public abstract void persistAll(EntityManager em, Collection<?> pcs);

   /** Delete the given persistent objects. */
   public abstract void removeAll(EntityManager em, Collection<?> pcs);

   /** Return the connection in use by the entity manager, or a new connection if none. */
   public abstract Connection getConnection(EntityManager em);

   /** Return a set of all managed instances. */
   public abstract Collection<?> getManagedObjects(EntityManager em);

   /** Release the given objects from management. */
   public abstract void releaseAll(EntityManager em, Collection<?> pcs);

   /** Refresh the attributes of the object from the database. */
   public abstract Object refresh(EntityManager em, Object pc);

   /** Evict the given object. */
   public abstract void evict(EntityManager em, Object pc);

   /** Evict the given objects. */
   public abstract void evictAll(EntityManager em, Collection<?> pcs);
}
