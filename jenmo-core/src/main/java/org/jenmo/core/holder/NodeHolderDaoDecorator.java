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
package org.jenmo.core.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jenmo.common.repository.IBaseDao;
import org.jenmo.core.domain.Node;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;


/**
 * {@link IDefaultDao} decorator for {@link INodeHolder}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class NodeHolderDaoDecorator<T extends INodeHolder> {
   /** The type of the {@link INodeHolder} */
   private Class<T> type = null;

   /** The delegate we refer to */
   private IDefaultDao<Node, Long> delegate;

   protected NodeHolderDaoDecorator() {
   }

   public NodeHolderDaoDecorator(final Class<T> aType) {
      this.type = aType;
      // Default
      decorate(new DefaultDaoJPA<Node, Long>(Node.class));
   }

   /**
    * Decorates the given {@link IDefaultDao} to be able to manage {@link INodeHolder}.
    */
   public void decorate(IDefaultDao<Node, Long> delegate) {
      this.delegate = delegate;
   }

   private String getHolderName() {
      return type.getSimpleName();
   }

   private T wrap(Node node) {
      return node.getHolder(type);
   }

   private List<T> wrap(List<Node> nodes) {
      List<T> out = new ArrayList<T>(nodes.size());
      for (Node node : nodes) {
         T newInstance = wrap(node);
         out.add(newInstance);
      }
      return out;
   }

   private String parse(String str) {
      return str.replaceAll(getHolderName(), "Node");
   }

   private Map<String, Object> parse(Map<String, Object> propertyValues) {
      Map<String, Object> out = new HashMap<String, Object>(propertyValues.size());
      for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
         out.put(parse(entry.getKey()), entry.getValue());
      }
      return out;
   }

   /**
    * Persists the given {@link INodeHolder} using its inner entity (see
    * {@link INodeHolder#getInnerEntity()}).
    */
   public void persist(T o) {
      Node node = o.getInnerEntity();
      if (node.getHolder() == null) {
         node.setHolder(o);
      }
      delegate.persist(node);
   }

   /**
    * Delete the {@link INodeHolder} identified with the given id.
    */
   public void delete(Long id) {
      delegate.delete(id);
   }

   // /**
   // * Delete (without checking first) the {@link INodeHolder} identified with the given id.
   // */
   // public void deleteWithoutTest(Long id) {
   // delegate.deleteWithoutTest(id);
   // }
   //
   // /**
   // * Tests whether or not the {@link INodeHolder} identified with the given id is deletable.
   // */
   // public boolean isDeletable(Long id) {
   // return delegate.isDeletable(id);
   // }

   /**
    * Creates a new {@link Node}, and wraps it by a newly created T object (see
    * {@link Node#getHolder(Class)}).
    */
   public T create() {
      Node node = delegate.create();
      return wrap(node);
   }

   /**
    * Find the {@link Node}s based on the property values in the map (see {@link IBaseDao#find(Map)}
    * ) and wraps them by newly created T objects (see {@link Node#getHolder(Class)}).
    */
   public List<T> find(Map<String, Object> propertyValues) {
      List<Node> nodes = delegate.find(parse(propertyValues));
      return wrap(nodes);
   }

   /**
    * Find the {@link Node}s based on the property values in the map (see
    * {@link IBaseDao#find(Map, String[])}) and wraps them by newly created T objects (see
    * {@link Node#getHolder(Class)}).
    */
   public List<T> find(Map<String, Object> propertyValues, String[] orderBy) {
      List<Node> nodes = delegate.find(parse(propertyValues), orderBy);
      return wrap(nodes);
   }

   /**
    * Find the {@link Node}s based on the property values in the map (see
    * {@link IBaseDao#find(Map, String[], boolean[])}) and wraps them by newly created T objects
    * (see {@link Node#getHolder(Class)}).
    */
   public List<T> find(Map<String, Object> propertyValues, String[] orderBy, boolean[] ascending) {
      List<Node> nodes = delegate.find(parse(propertyValues), orderBy, ascending);
      return wrap(nodes);
   }

   /**
    * Find the {@link Node}s based on the property values in the map (see
    * {@link IBaseDao#find(Map, String, boolean)}) and wraps them by newly created T objects (see
    * {@link Node#getHolder(Class)}).
    */
   public List<T> find(Map<String, Object> propertyValues, String orderBy, boolean ascending) {
      List<Node> nodes = delegate.find(parse(propertyValues), orderBy, ascending);
      return wrap(nodes);
   }

   /**
    * Find the {@link Node}s based on the property values in the map (see
    * {@link IBaseDao#find(String, Object)}) and wraps them by newly created T objects (see
    * {@link Node#getHolder(Class)}).
    */
   public List<T> find(String property, Object value) {
      List<Node> nodes = delegate.find(parse(property), value);
      return wrap(nodes);
   }

   /**
    * See {@link IBaseDao#getEntityManager()}.
    */
   public Object getEntityManager() {
      return delegate.getEntityManager();
   }

   /**
    * See {@link IBaseDao#setEntityManager(Object)}.
    */
   public void setEntityManager(Object entityManager) {
      delegate.setEntityManager(entityManager);
   }

   /**
    * Fetches the {@link Node} identified with the given id and wraps it by a newly created T object
    * (see {@link Node#getHolder(Class)}).
    */
   public T read(Long id) {
      Node node = delegate.read(id);
      return wrap(node);
   }

   /**
    * Updates the {@link Node} identified with the given id and wraps it by a newly created T object
    * (see {@link Node#getHolder(Class)}).
    */
   public T update(T o) {
      Node node = delegate.update(o.getInnerEntity());
      return wrap(node);
   }
}
