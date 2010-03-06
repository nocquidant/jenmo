package org.jenmo.core.orm;

import java.util.Collection;

import javax.persistence.EntityManager;

public class EclipseLinkActions extends JpaSpiActions {
   @Override
   public EntityManager getEntityManager(Object pc) {
      throw new UnsupportedOperationException("TODO");
   }

   @Override
   public void removeAll(EntityManager em, Collection<?> pcs) {
      for (Object o : pcs) {
         em.remove(o);
      }
   }

   @Override
   public void persistAll(EntityManager em, Collection<?> pcs) {
      for (Object o : pcs) {
         em.persist(o);
      }
   }
}
