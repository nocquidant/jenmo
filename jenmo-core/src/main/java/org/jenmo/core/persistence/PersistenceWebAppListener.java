package org.jenmo.core.persistence;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 * 
 * @author Nicolas Ocquidant (thanks to puncherico)
 * @since 1.0
 */
public class PersistenceWebAppListener implements ServletContextListener {

   public void contextInitialized(ServletContextEvent evt) {
   }

   public void contextDestroyed(ServletContextEvent evt) {
      PersistenceManager.getInstance().closeEntityManagerFactory();
   }
}
