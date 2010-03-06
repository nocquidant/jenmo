/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * --
 * From OpenJPA: http://openjpa.apache.org/
 * Modifications to genuine file:
 *   - remove methods regarding serp in comments
 *   - keep only used methods
 */
package org.jenmo.common.util;

import java.security.PrivilegedAction;

/**
 * Helper class to obtain the Privilege(Exception)Action object to perform Java 2 doPrivilege
 * security sensitive function call in the following methods:
 * <ul>
 * <li>AccessibleObject.setAccessible
 * <li>Class.forName
 * <li>Class.getClassLoader
 * <li>Class.getDeclaredField
 * <li>Class.getDeclaredFields
 * <li>Class.getDeclaredMethod
 * <li>Class.getDeclaredMethods
 * <li>Class.getResource
 * <li>Class.newInstance
 * <li>ClassLoader.getParent
 * <li>ClassLoader.getResource
 * <li>ClassLoader.getResources
 * <li>ClassLoader.getSystemClassLoader
 * <li>File.delete
 * <li>File.exists
 * <li>File.getAbsoluteFile
 * <li>File.getAbsolutePath
 * <li>File.getCanonicalPath
 * <li>File.listFiles
 * <li>File.length
 * <li>File.isDirectory
 * <li>File.mkdirs
 * <li>File.renameTo
 * <li>File.toURL
 * <li>FileInputStream new
 * <li>FileOutputStream new
 * <li>System.getProperties
 * <li>InetAddress.getByName
 * <li>MultiClassLoader new
 * <li>ServerSocket new
 * <li>Socket new
 * <li>Socket.accept
 * <li>System.getProperty
 * <li>Thread.getContextClassLoader
 * <li>Thread new
 * <li>TemporaryClassLoader new
 * <li>URL.openStream
 * <li>URLConnection.getContent
 * <li>ZipFile new
 * </ul>
 * 
 * If these methods are used, the following sample usage patterns should be followed to ensure
 * proper privilege is granted:
 * <p>
 * 1) No security risk method call. E.g.
 * 
 * <pre>
 * private static final String SEP = J2DoPrivUtils.getLineSeparator();
 * </pre>
 * 
 * 2) Methods with no exception thrown. PrivilegedAction is returned from J2DoPrivUtils.*Action().
 * E.g.
 * 
 * <pre>
 * ClassLoader loader = (ClassLoader) AccessController.doPrivileged(J2DoPrivUtils
 *       .getClassLoaderAction(clazz));
 * 
 * ClassLoader loader = (ClassLoader) (System.getSecurityManager() == null) ? clazz.getClassLoader()
 *       : AccessController.doPrivileged(J2DoPrivUtils.getClassLoaderAction(clazz));
 * </pre>
 * 
 * 3) Methods with exception thrown. PrivilegedExceptionAction is returned from
 * J2DoPrivUtils.*Action(). E.g.
 * 
 * <pre>
 *    try {
 *      method = (Method) AccessController.doPrivileged(
 *        J2DoPrivUtils.getDeclaredMethodAction(clazz, name, parameterType));
 *    } catch (PrivilegedActionException pae) {
 *      throw (NoSuchMethodException) pae.getException();
 *    }
 *    
 *    try {
 *      method = (System.getSecurityManager() == null)
 *        ? clazz.getDeclaredMethod(name,parameterType)
 *        : (Method) AccessController.doPrivileged(
 *            J2DoPrivUtils.getDeclaredMethodAction(
 *              clazz, name, parameterType));
 *    } catch (PrivilegedActionException pae) {
 *        throw (NoSuchMethodException) pae.getException()
 *    }
 * </pre>
 * 
 * @author Nicolas Ocquidant (thanks to Albert Lee)
 * @since 1.0
 */
public class J2DoPrivUtils {
   /**
    * Return a PrivilegeAction object for clazz.getClassloader().
    * 
    * Notes: No doPrivilege wrapping is required in the caller if: "the caller's class loader is not
    * null and the caller's class loader is not the same as or an ancestor of the class loader for
    * the class whose class loader is requested". E.g.
    * 
    * this.getClass().getClassLoader();
    * 
    * Requires security policy: 'permission java.lang.RuntimePermission "getClassLoader";'
    * 
    * @return Classloader
    */
   public static final PrivilegedAction<Object> getClassLoaderAction(final Class<?> clazz) {
      return new PrivilegedAction<Object>() {
         public Object run() {
            return clazz.getClassLoader();
         }
      };
   }

   /**
    * Return a PrivilegeAction object for System.getProperty().
    * 
    * Requires security policy: 'permission java.util.PropertyPermission "read";'
    * 
    * @return String
    */
   public static final PrivilegedAction<Object> getPropertyAction(final String name) {
      return new PrivilegedAction<Object>() {
         public Object run() {
            return System.getProperty(name);
         }
      };
   }
}
