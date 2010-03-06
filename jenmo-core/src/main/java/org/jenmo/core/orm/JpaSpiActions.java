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

import java.util.Collection;

import javax.persistence.EntityManager;

import org.jenmo.core.config.JenmoConfig;

/**
 * A class to be able to invoke ORM dependent actions (i.e. methods) in a independent manner.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public abstract class JpaSpiActions {
   private static JpaSpiActions instance;

   public static JpaSpiActions getInstance() {
      JpaSpiActions out = instance;
      if (out == null) { // First check no locking
         synchronized (JpaSpiActions.class) {
            out = instance;
            if (out == null) { // Second check with locking
               Object value = JenmoConfig.getInstance().getValue(
                     JenmoConfig.OPTION_JPA_PROVIDER).get();
               if (value.equals("openjpa")) {
                  instance = out = new OpenJpaActions();
               } else if (value.equals("eclipselink")) {
                  instance = out = new EclipseLinkActions();
               }
            }
         }
      }
      return instance;
   }

   public synchronized static void setInstance(JpaSpiActions instance) {
      JpaSpiActions.instance = instance;
   }

   /**
    * Return the entity manager for the given object, if one can be determined from just the object
    * alone.
    */
   public abstract EntityManager getEntityManager(Object pc);

   /** Persist the given objects. */
   public void persistAll(EntityManager em, Collection<?> pcs) {
      for (Object each : pcs) {
         em.persist(each);
      }
   }

   /** Delete the given persistent objects. */
   public void removeAll(EntityManager em, Collection<?> pcs) {
      for (Object each : pcs) {
         em.remove(each);
      }
   }
}
