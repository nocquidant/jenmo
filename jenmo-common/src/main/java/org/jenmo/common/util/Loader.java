package org.jenmo.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.log4j.Logger;

public final class Loader {
   /** The logger. */
   protected static final Logger LOGGER = Logger.getLogger(Loader.class);

   private Loader() {

   }

   /**
    * This method will search for <code>resource</code> in different places. The search order is as
    * follows:
    * 
    * <ol>
    * 
    * <p>
    * <li>Search for <code>resource</code> using the thread context class loader. If that fails,
    * search for <code>resource</code> using the class loader that loaded this class (
    * <code>Loader</code>).
    * 
    * <p>
    * <li>Try one last time with <code>ClassLoader.getSystemResource(resource)</code>, that is is
    * using the system class loader.
    * 
    * </ol>
    */
   static public URL getResource(String resource) {
      URL url = null;

      try {
         ClassLoader classLoader = getTCL();
         if (classLoader != null) {
            LOGGER.debug("Trying to find [" + resource + "] using context classloader "
                  + classLoader + ".");
            url = classLoader.getResource(resource);
            if (url != null) {
               return url;
            }
         }

         // We could not find resource. Let us now try with the
         // classloader that loaded this class.
         classLoader = Loader.class.getClassLoader();
         if (classLoader != null) {
            LOGGER.debug("Trying to find [" + resource + "] using " + classLoader
                  + " class loader.");
            url = classLoader.getResource(resource);
            if (url != null) {
               return url;
            }
         }
      } catch (Throwable t) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
      }

      // Last ditch attempt: get the resource from the class path. It
      // may be the case that clazz was loaded by the Extentsion class
      // loader which the parent of the system class loader. Hence the
      // code below.
      LOGGER.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
      return ClassLoader.getSystemResource(resource);
   }

   /**
    * Get the Thread Context Loader which is a JDK 1.2 feature. If we are running under JDK 1.1 or
    * anything else goes wrong the method returns <code>null<code>.
    */
   private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException {
      // Are we running on a JDK 1.2 or later system?
      Method method = null;
      try {
         method = Thread.class.getMethod("getContextClassLoader");
      } catch (NoSuchMethodException e) {
         // We are running on JDK 1.1
         return null;
      }

      return (ClassLoader) method.invoke(Thread.currentThread());
   }
}
