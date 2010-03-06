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
 * Copyright 2007 Dudney.Net 
 * From: http://bill.dudney.net/roller/bill/entry/20070428
 * Modifications to genuine file:
 *   - removed annotations
 *   - changed class for abstract
 *   - removed field EntityManager
 *   - changed for abstract: getEntityManager & setEntityManager(EntityManager)
 *   - added method tryFieldNamedId(Class<T>)
 */
package org.jenmo.common.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;

import org.apache.log4j.Logger;

/**
 * Implementation of {@link IBaseDao} interface.
 * 
 * @author Nicolas Ocquidant (thanks to Bill Dudney)
 * @since 1.0
 */
public abstract class BaseDaoJpa<T, PK extends Serializable> implements IBaseDao<T, PK> {
   /** The logger. */
   protected static final Logger LOGGER = Logger.getLogger(BaseDaoJpa.class);

   private Class<T> type = null;

   protected BaseDaoJpa() {
   }

   public BaseDaoJpa(final Class<T> aType) {
      this.type = aType;
   }

   @Override
   public T create() {
      T newInstance = null;
      try {
         newInstance = type.newInstance();
      } catch (InstantiationException e) {
         String errorMsg = "An InstantiationException was thrown trying to create an instance of "
               + type.getName() + " message: " + e.getMessage();
         LOGGER.error(errorMsg, e);
         throw new IllegalArgumentException(errorMsg, e);
      } catch (IllegalAccessException e) {
         String errorMsg = "An InstantiationException was thrown trying to create an instance of "
               + type.getName() + " message: " + e.getMessage();
         LOGGER.error(errorMsg, e);
         throw new IllegalArgumentException(errorMsg, e);
      }
      getEntityManager().persist(newInstance);
      return newInstance;
   }

   @Override
   public void persist(T o) {
      getEntityManager().persist(o);
   }

   @Override
   public void delete(PK id) {
      // TODO: unclear if using reflection to find the pkfield name is worth
      // not reading the object from the DB, do some analysis
      String entityName = getEntityName();
      String pkFieldName = getPKFieldName();
      String queryString = "DELETE FROM " + entityName + " AS o WHERE o." + pkFieldName + " = :id";
      EntityManager entityManager = getEntityManager();
      Query query = entityManager.createQuery(queryString);
      query.setParameter(pkFieldName, id);
      query.executeUpdate();
   }

   @Override
   public List<T> find(Map<String, Object> propertyValues) {
      return find(propertyValues, null, null);
   }

   @Override
   public List<T> find(Map<String, Object> propertyValues, String[] orderBy) {
      boolean ascending[] = null;
      if (null != orderBy) {
         ascending = new boolean[orderBy.length];
         for (int i = 0; i < orderBy.length; i++) {
            ascending[i] = true;
         }
      }
      return find(propertyValues, orderBy, ascending);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<T> find(Map<String, Object> propertyValues, String[] orderBy, boolean[] ascending) {
      String entityName = getEntityName();
      String queryString = constructQueryString(entityName, propertyValues, orderBy, ascending);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("query = " + queryString);
      }
      Query query = constructQuery(propertyValues, queryString, getEntityManager());
      for (String key : propertyValues.keySet()) {
         query.setParameter(key, propertyValues.get(key));
      }
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public T findSingle(Map<String, Object> propertyValues) {
      String entityName = getEntityName();
      String queryString = constructQueryString(entityName, propertyValues, null, null);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("query = " + queryString);
      }
      Query query = constructQuery(propertyValues, queryString, getEntityManager());
      for (String key : propertyValues.keySet()) {
         query.setParameter(key, propertyValues.get(key));
      }
      return (T) query.getSingleResult();
   }

   @Override
   public T findSingle(String property, Object value) {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(property, value);
      return findSingle(properties);
   }

   @Override
   public List<T> find(Map<String, Object> propertyValues, String orderBy, boolean ascending) {
      return find(propertyValues, new String[] { orderBy }, new boolean[] { ascending });
   }

   @Override
   public List<T> find(String property, Object value) {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(property, value);
      return find(properties);
   }

   @Override
   public T read(PK id) {
      return getEntityManager().find(type, id);
   }

   @Override
   public T update(T o) {
      // Merge works fine with detached or transient instances
      return getEntityManager().merge(o);
   }

   private Query constructQuery(Map<String, Object> propertyValues, String queryString,
         EntityManager entityManager) {
      try {
         Query query = null;
         try {
            query = entityManager.createQuery(queryString);
         } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
         }
         for (String propName : propertyValues.keySet()) {
            if (propertyValues.get(propName) != null) {
               query.setParameter(propName, propertyValues.get(propName));
            }
         }
         return query;
      } catch (Exception ex) {
         throw new RuntimeException("Could not create query :" + queryString, ex);
      }
   }

   protected String getEntityName(Class<T> clazz) {
      Entity entity = clazz.getAnnotation(Entity.class);
      if (entity == null) {
         return clazz.getSimpleName();
      }
      String entityName = entity.name();

      if (entityName == null) {
         return clazz.getSimpleName();
      } else if (!(entityName.length() > 0)) {
         return clazz.getSimpleName();
      } else {
         return entityName;
      }

   }

   protected String getEntityName() {
      return getEntityName(type);
   }

   private String tryFieldNamedId(Class<T> clazz) {
      try {
         // Try to guess the 'id' field...
         Field field = clazz.getDeclaredField("id");
         if (field != null && field.getAnnotation(Id.class) != null) {
            return field.getName();
         }
      } catch (NoSuchFieldException e) {
         // Go the classic way
      }
      return null;
   }

   protected String getPKFieldName(Class<T> clazz) {
      String fieldName = tryFieldNamedId(clazz);
      if (fieldName != null) {
         return fieldName;
      }

      // traverse the fields looking for the field marked with an @Id
      // annotation
      fieldName = getIdFieldName(clazz);
      // next try the methods
      if (fieldName == null) {
         fieldName = getIdFieldMethod(clazz);
      }
      // return the field name
      return fieldName;
   }

   private String getIdFieldName(Class<?> clazz) {
      String fieldName = null;
      for (Field field : clazz.getDeclaredFields()) {
         Id id = field.getAnnotation(Id.class);
         if (id != null) {
            fieldName = field.getName();
         }
      }
      if (null == fieldName && null != clazz.getSuperclass()) {
         fieldName = getIdFieldName(clazz.getSuperclass());
      }
      return fieldName;
   }

   private String getIdFieldMethod(Class<?> clazz) {
      String fieldName = null;
      for (Method method : clazz.getDeclaredMethods()) {
         Id id = method.getAnnotation(Id.class);
         if (id != null) {
            fieldName = method.getName().substring(3);
            String firstChar = fieldName.substring(0, 1).toLowerCase();
            fieldName = firstChar + fieldName.substring(1);
         }
      }
      if (null == fieldName && null != clazz.getSuperclass()) {
         fieldName = getIdFieldMethod(clazz.getSuperclass());
      }
      return fieldName;
   }

   protected String getPKFieldName() {
      return getPKFieldName(type);
   }

   protected String constructQueryString(String entityName, Map<String, Object> propertyValues,
         String[] orderBy, boolean[] ascending) {
      if ((null != orderBy && null != ascending) && orderBy.length != ascending.length) {
         throw new IllegalArgumentException("orderBy.length must match ascending.length");
      }
      String instanceName = "o";
      StringBuilder query = new StringBuilder("SELECT " + instanceName + " FROM ");
      query.append(entityName);
      query.append(" ");
      query.append(instanceName);
      if (propertyValues.size() > 0) {
         query.append(" WHERE ");
         // keep the property names in a list to make sure we get them out in
         // the right order
         Iterator<String> propItr = propertyValues.keySet().iterator();
         if (propItr.hasNext()) {
            String propName = propItr.next();
            appendValueRepresentation(query, instanceName, propName, propertyValues);
            for (; propItr.hasNext();) {
               propName = propItr.next();
               query.append(" AND ");
               appendValueRepresentation(query, instanceName, propName, propertyValues);
            }
         }
      }
      if (null != orderBy && orderBy.length > 0) {
         query.append(" ORDER BY ");
         for (int i = 0; i < orderBy.length; i++) {
            query.append(instanceName);
            query.append(".");
            query.append(orderBy[i]);
            if (i + 1 < orderBy.length) {
               query.append(", ");
            }
         }
      }
      return query.toString();
   }

   private void appendValueRepresentation(StringBuilder query, String instanceName,
         String propName, Map<String, Object> propertyValues) {
      Object value = propertyValues.get(propName);
      query.append(instanceName);
      query.append(".");
      if (value == null) {
         query.append(propName);
         query.append(" IS NULL ");
      } else {
         query.append(propName);
         query.append(" = :");
         query.append(propName);
      }
   }

   public Class<T> getType() {
      return type;
   }

   public void setType(Class<T> type) {
      this.type = type;
   }

   @Override
   public abstract EntityManager getEntityManager();

   @Override
   public abstract void setEntityManager(Object entityManager);
}