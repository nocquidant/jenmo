package org.jenmo.core.persistence;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * Web application lifecycle listener.
 * 
 * @author Nicolas Ocquidant (thanks to puncherico)
 * @since 1.0
 */
public class PersistenceWebAppRequestListener implements ServletRequestListener {
   /**
    * ### Method from ServletRequestListener ###
    * 
    * The request is about to come into scope of the web application.
    */
   public void requestInitialized(ServletRequestEvent evt) {
      // TODO add your code here:
   }

   /**
    * ### Method from ServletRequestListener ###
    * 
    * The request is about to go out of scope of the web application.
    */
   public void requestDestroyed(ServletRequestEvent evt) {

      PersistenceManager pm = PersistenceManager.getInstance();

      if (pm instanceof ScopedPersistenceManager) {
         LazyCloseEntityManager em = ((ScopedEntityManagerFactory) pm.getEntityManagerFactory())
               .getEntityManager();
         if (em != null) {
            em.lazyClose();
         }
      }
   }
}