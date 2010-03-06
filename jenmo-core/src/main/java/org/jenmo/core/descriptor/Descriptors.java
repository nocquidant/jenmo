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
package org.jenmo.core.descriptor;

import java.lang.ref.SoftReference;

import org.jenmo.common.cache.SoftValueMap;
import org.jenmo.core.descriptor.INodeDescriptor.INodeDescAsList;
import org.jenmo.core.descriptor.INodeDescriptor.INodeDescAsMap;
import org.jenmo.core.descriptor.INodeDescriptor.INodeDescAsSet;
import org.jenmo.core.descriptor.INodeDescriptor.INodeDescAsSingle;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsList;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsMap;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsSet;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsSingle;

/**
 * A factory class to get {@link IDescriptor}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class Descriptors {
   private Descriptors() {

   }

   @SuppressWarnings("unchecked")
   private static SoftValueMap<Class, PropDescAsList> cachePropList = new SoftValueMap<Class, PropDescAsList>();

   @SuppressWarnings("unchecked")
   private static SoftValueMap<Class, SoftValueMap<Class, PropDescAsMap>> cachePropMap = new SoftValueMap<Class, SoftValueMap<Class, PropDescAsMap>>();

   @SuppressWarnings("unchecked")
   private static SoftValueMap<Class, PropDescAsSet> cachePropSet = new SoftValueMap<Class, PropDescAsSet>();

   @SuppressWarnings("unchecked")
   private static SoftValueMap<Class, PropDescAsSingle> cachePropSingle = new SoftValueMap<Class, PropDescAsSingle>();

   private static SoftReference<NodeDescAsList> cacheNodeList = null;

   @SuppressWarnings("unchecked")
   private static SoftValueMap<Class, NodeDescAsMap> cacheNodeMap = new SoftValueMap<Class, NodeDescAsMap>();

   private static SoftReference<NodeDescAsSet> cacheNodeSet = null;

   private static SoftReference<NodeDescAsSingle> cacheNodeSingle = null;

   /**
    * Gets a descriptor as {@link IPropDescAsList} for the given class T.
    */
   @SuppressWarnings("unchecked")
   public static <T> IPropDescAsList<T> listForProps(Class<T> clazz) {
      PropDescAsList<?> v = cachePropList.get(clazz);
      if (v == null) {
         v = new PropDescAsList<T>(clazz);
         cachePropList.put(clazz, v);
      }
      return (IPropDescAsList<T>) v;
   }

   /**
    * Gets a descriptor as {@link INodeDescAsList}.
    */
   public static INodeDescAsList listForNode() {
      if (cacheNodeList == null) {
         cacheNodeList = new SoftReference<NodeDescAsList>(new NodeDescAsList());
      }
      return cacheNodeList.get();
   }

   /**
    * Gets a descriptor as {@link IPropDescAsMap} for the given classes K, V.
    */
   @SuppressWarnings("unchecked")
   public static <K, V> IPropDescAsMap<K, V> mapForProps(Class<K> clazz1, Class<V> clazz2) {
      SoftValueMap<Class, PropDescAsMap> vmap = cachePropMap.get(clazz1);
      if (vmap == null) {
         vmap = new SoftValueMap<Class, PropDescAsMap>();
         cachePropMap.put(clazz1, vmap);
      }
      PropDescAsMap<?, ?> v = vmap.get(clazz2);
      if (v == null) {
         v = new PropDescAsMap<K, V>(clazz1, clazz2);
         vmap.put(clazz2, v);
      }
      return (IPropDescAsMap<K, V>) v;
   }

   /**
    * Gets a descriptor as {@link INodeDescAsMap}.
    */
   @SuppressWarnings("unchecked")
   public static <K> INodeDescAsMap<K> mapForNode(Class<K> clazz) {
      NodeDescAsMap<?> v = cacheNodeMap.get(clazz);
      if (v == null) {
         v = new NodeDescAsMap<K>(clazz);
         cacheNodeMap.put(clazz, v);
      }
      return (INodeDescAsMap<K>) v;
   }

   /**
    * Gets a descriptor as {@link IPropDescAsSet} for the given class T.
    */
   @SuppressWarnings("unchecked")
   public static <T> IPropDescAsSet<T> setForProps(Class<T> clazz) {
      PropDescAsSet<?> v = cachePropSet.get(clazz);
      if (v == null) {
         v = new PropDescAsSet<T>(clazz);
         cachePropSet.put(clazz, v);
      }
      return (IPropDescAsSet<T>) v;
   }

   /**
    * Gets a descriptor as {@link INodeDescAsSet}.
    */
   public static INodeDescAsSet setForNode() {
      if (cacheNodeSet == null) {
         cacheNodeSet = new SoftReference<NodeDescAsSet>(new NodeDescAsSet());
      }
      return cacheNodeSet.get();
   }

   /**
    * Gets a descriptor as {@link IPropDescAsSingle} for the given class T.
    */
   @SuppressWarnings("unchecked")
   public static <T> IPropDescAsSingle<T> singleForProps(Class<T> clazz) {
      PropDescAsSingle<?> v = cachePropSingle.get(clazz);
      if (v == null) {
         v = new PropDescAsSingle<T>(clazz);
         cachePropSingle.put(clazz, v);
      }
      return (IPropDescAsSingle<T>) v;
   }

   /**
    * Gets a descriptor as {@link INodeDescAsSingle}.
    */
   public static INodeDescAsSingle singleForNode() {
      if (cacheNodeSingle == null) {
         cacheNodeSingle = new SoftReference<NodeDescAsSingle>(new NodeDescAsSingle());
      }
      return cacheNodeSingle.get();
   }
}