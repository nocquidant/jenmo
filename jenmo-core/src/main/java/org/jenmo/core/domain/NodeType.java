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
package org.jenmo.core.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.jenmo.common.marker.IImmutable;
import org.jenmo.core.cache.JenmoCache;

/**
 * A {@link NodeType} entity is the name of the type of a {@link Node}, and classify a {@link Node}.
 * One {@link NodeType} accepts only a known set of {@link Property}s.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "NODETYPE", uniqueConstraints = @UniqueConstraint(columnNames = { "NTYPE" }))
public class NodeType implements IImmutable {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NodeTypeSeq")
   @SequenceGenerator(name = "NodeTypeSeq", sequenceName = "NODETYPE_ID_SEQ", allocationSize = 10)
   @Column(name = "ID")
   private long id;

   // No version field as this class is immutable

   /** The type of the Node */
   @Basic(optional = false)
   @Column(name = "NTYPE", nullable = false, updatable = false)
   private String ntype;

   /** M-N to Property as one Property may be shared by several NodeType */
   @ManyToMany(mappedBy = "nodeTypes", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   // @JoinColumn(nullable = false, updatable = false)
   // Manage deletion by code
   private Set<Property> properties;

   /** Indexed by name properties. */
   @Transient
   private volatile Map<String, Property> indexedProps;

   private NodeType() {
   }

   /**
    * A shortcut method to {@link #newInstance(String, Collection)}.
    */
   public static NodeType newInstance(String type, Property... acceptedProps) {
      return newInstance(type, Arrays.asList(acceptedProps));
   }

   /**
    * Creates a new instance for the given {@code type} and {@link Property}s.
    */
   public static NodeType newInstance(String type, Collection<Property> acceptedProps) {
      if (type == null) {
         throw new NullPointerException("Type cannot be null");
      }
      NodeType instance = new NodeType();
      instance.ntype = type;
      instance.properties = new HashSet<Property>(acceptedProps);
      // Don't forget the reverse link
      for (Property each : instance.properties) {
         each.addNodeType(instance);
      }
      return instance;
   }

   /**
    * Gets the persistent identity of the instance.
    */
   public long getId() {
      return id;
   }

   /**
    * Gets type of this {@link NodeType}.
    */
   public String getType() {
      return ntype;
   }

   /**
    * Gets {@link Property} associated with this {@link NodeType} for the given string.
    * <p>
    * <b>Warning:</b> the associated {@link Property}s are not dependent.
    */
   public Property getProperty(String str) {
      return getPropsAsMap().get(str);
   }

   /**
    * Gets {@link Property}s associated with this {@link NodeType}.
    * <p>
    * <b>Warning:</b> the associated {@link Property}s are not dependent.
    */
   public Collection<Property> getProperties() {
      return new ArrayList<Property>(properties);
   }

   private Map<String, Property> getPropsAsMap() {
      Map<String, Property> out = indexedProps;
      if (out == null) {
         // Allow repeated initialization (no locking)
         indexedProps = out = computeIndexedProps();
      }
      return out;
   }

   private Map<String, Property> computeIndexedProps() {
      Map<String, Property> map = new HashMap<String, Property>();
      for (Property each : properties) {
         map.put(JenmoCache.getInstance().getPropertyAsString(each), each);
      }
      return map;
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ")");
   }
}
