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
 */
package org.jenmo.core.orm;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jenmo.common.cache.SoftValueMap;

/**
 * OpenJPA implementation of {@link JpaSpiActions}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class OpenJpaActions extends JpaSpiActions {
   private static final String strOpenJPAPersistence = "org.apache.openjpa.persistence.OpenJPAPersistence";

   private static Map<String, Method> methodsCache = new SoftValueMap<String, Method>();

   @Override
   public EntityManager getEntityManager(Object pc) {
      // Call to OpenJPAPersistence.getEntityManager(pc);
      try {
         Method get = methodsCache.get("getEntityManager");
         if (get == null) {
            get = Class.forName(strOpenJPAPersistence).getMethod("getEntityManager", Object.class);
            methodsCache.put("getEntityManager", get);
         }
         return (EntityManager) get.invoke(null, pc);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public void removeAll(EntityManager em, Collection<?> pcs) {
      // Call to OpenJPAPersistence.cast(em).removeAll(pcs);
      try {
         Method cast = methodsCache.get("cast");
         if (cast == null) {
            cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
            methodsCache.put("cast", cast);
         }
         Object openJpaEm = cast.invoke(null, em);
         Method removeAll = methodsCache.get("removeAll");
         if (removeAll == null) {
            removeAll = openJpaEm.getClass().getMethod("removeAll", Collection.class);
            methodsCache.put("removeAll", removeAll);
         }
         removeAll.invoke(openJpaEm, pcs);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }

   @Override
   public void persistAll(EntityManager em, Collection<?> pcs) {
      // OpenJPAPersistence.cast(em).persistAll(pcs);
      try {
         Method cast = methodsCache.get("cast");
         if (cast == null) {
            cast = Class.forName(strOpenJPAPersistence).getMethod("cast", EntityManager.class);
            methodsCache.put("cast", cast);
         }
         Object openJpaEm = cast.invoke(null, em);
         Method persistAll = methodsCache.get("persistAll");
         if (persistAll == null) {
            persistAll = openJpaEm.getClass().getMethod("persistAll", Collection.class);
            methodsCache.put("persistAll", persistAll);
         }
         persistAll.invoke(openJpaEm, pcs);
      } catch (Exception e) {
         throw new IllegalJpaSpiActionException(e);
      }
   }
}
