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
 *   - added a "I" prefix
 *   - added a getEntityManager() method
 *   - added a setEntityManager() method
 */
package org.jenmo.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

/**
 * DAO implementation using JPA.
 * 
 * @author Nicolas Ocquidant (thanks to Bill Dudney)
 * @since 1.0
 */
public interface IBaseDao<T, PK extends Serializable> {

   /**
    * Create a new instance and persist it to the database. If there is a problem creating the
    * instance a runtime exception is thrown.
    */
   T create();

   /**
    * Persist the given instance to the database.
    */
   void persist(T o);

   /**
    * Pull a single instance with id as its primary key.
    * 
    * @return null if the object can't be found, otherwise the object.
    */
   T read(PK id);

   /**
    * Update the object <code>o</code> in the database. If <code>o</code> is not already a managed
    * object it will become so and then persisted. The newly persisted object is returned, it is not
    * guarenteed to be the same object so you should discard your reference to <code>o</code> after
    * calling this method i.e. reassign o to the instance returned from this method.
    * 
    * @param o
    */
   T update(T o);

   /**
    * Remove the object identified by <code>id</code> from the db. If the object does not exist this
    * is equivalent to a no-op.
    * 
    * @param id
    *           the identifier of the object to delete.
    */
   void delete(PK id);

   /**
    * Find the object based on the property values in the map. The keys must be the name of
    * persistent properties of the type <code>T</code>. This is conceptually equivalent to SELECT t
    * FROM T t WHERE t.key1 = propertyValues.get(key1) AND t.key2 = propertyValues.get(key2) AND ...
    * t.keyN = propertyValues.get(keyN) in JPQL.
    * 
    * @param propertyValues
    * 
    * @throws IllegalArgumentException
    *            if one of the properties is not a property of the type <code>T</code>
    * 
    * @return the set of all objects that match the criteria.
    */
   List<T> find(Map<String, Object> propertyValues);

   /**
    * Find the object based on the property values in the map. The keys must be the name of
    * persistent properties of the type <code>T</code>. The results are orderd according the
    * <code>ascending</code> flag corresponding to the <code>orderBy</code> property.
    * 
    * @param propertyValues
    *           properties of VO to use as filters
    * @param orderBy
    *           fields to order by in descending order assumes ascending order
    * 
    * @throws IllegalArgumentException
    *            if one of the properties (in the <code>propertyValues</code> or
    *            <code>orderBy</code>) is not a property of the type <code>T</code>
    * 
    * @return the list of all objects that match the criteria ordered as specified
    */
   List<T> find(Map<String, Object> propertyValues, String[] orderBy);

   /**
    * Find the object based on the property values in the map. The keys must be the name of
    * persistent properties of the type <code>T</code>. The results are orderd according the
    * <code>ascending</code> flag corresponding to the <code>orderBy</code> property.
    * 
    * @param propertyValues
    *           properties of VO to use as filters
    * @param orderBy
    *           fields to order by in descending order
    * @param ascending
    *           flags indicating ascending or descending order
    * 
    * @throws IllegalArgumentException
    *            if one of the properties (in the <code>propertyValues</code> or
    *            <code>orderBy</code>) is not a property of the type <code>T</code>. Also throws
    *            this exception if the <code>ascending</code> and <code>orderBy</code> arrays are
    *            not of the same length.
    * 
    * @return the list of all objects that match the criteria ordered as specified
    */
   List<T> find(Map<String, Object> propertyValues, String[] orderBy, boolean[] ascending);

   /**
    * Find the object based on the property values in the map. The keys must be the name of
    * persistent properties of the type <code>T</code>. The results are ordered by the
    * <code>orderBy</code> property according the <code>ascending</code> flag.
    * 
    * @param propertyValues
    *           properties of VO to use as filters
    * @param orderBy
    *           fields to order by in descending order
    * @param ascending
    *           flags indicating ascending or descending order
    * 
    * @throws IllegalArgumentException
    *            if one of the properties (in the <code>propertyValues</code> or
    *            <code>orderBy</code>) is not a property of the type <code>T</code>.
    * 
    * @return the list of all objects that match the criteria ordered as specified
    */
   List<T> find(Map<String, Object> propertyValues, String orderBy, boolean ascending);

   /**
    * Find the object based on the <code>property</code> and <code>value</code> specified. The
    * property must be one of the persistent properties on type <code>T</code>. This is equivalent
    * to SELECT t FROM T t WHERE t.property = value in JPQL.
    * 
    * @param property
    *           persistent property of <code>T</code>
    * @param value
    *           value to match
    * 
    * @throws IllegalArgumentException
    *            if the property is not a property of the type <code>T</code>.
    * 
    * @return objects of type <code>T</code> matching the <code>property</code> and
    *         <code>value</code>.
    */
   List<T> find(String property, Object value);

   /**
    * Find the object based on the property values in the map. The keys must be the name of
    * persistent properties of the type <code>T</code>. Throws {@link NonUniqueResultException} if
    * more than one result.
    */
   T findSingle(Map<String, Object> propertyValues);

   /**
    * Find the object based on the <code>property</code> and <code>value</code> specified. The
    * property must be one of the persistent properties on type <code>T</code>. Throws
    * {@link NonUniqueResultException} if more than one result.
    */
   T findSingle(String property, Object value);

   /**
    * Return the EntityManager or Session object use by this DAO.
    */
   Object getEntityManager();

   /**
    * Set the EntityManager or Session object use by this DAO.
    */
   void setEntityManager(Object entityManager);
}
