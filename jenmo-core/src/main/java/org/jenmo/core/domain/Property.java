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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 * A {@link Property} entity is the name of the property of a {@link NodeProperty}, and classify a
 * {@link NodeProperty}. One {@link NodeType} accepts only a known set of {@link Property}s.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "PROPERTY", uniqueConstraints = @UniqueConstraint(columnNames = { "NAME" }))
public class Property {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PropertySeq")
   @SequenceGenerator(name = "PropertySeq", sequenceName = "PROPERTY_ID_SEQ", allocationSize = 10)
   @Column(name = "ID")
   private long id;

   @Version
   @Column(name = "VERSION")
   // This class cannot be immutable as the 'nodeTypes' collection could grow up
   private int version;

   /** The property name */
   @Basic(optional = false)
   // @Unique
   @Column(name = "NAME", nullable = false, updatable = false)
   private String name;

   /** M-N to NodeType as one NodeType may be shared by several Property */
   // Manage deletion by code
   @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   @JoinTable(name = "PROPERTY_NODETYPE", joinColumns = @JoinColumn(name = "PROPERTY_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "NODETYPE_ID", referencedColumnName = "ID"))
   private Set<NodeType> nodeTypes = new HashSet<NodeType>();

   private Property() {
   }

   /**
    * Creates a new instance for the given {@code name}.
    */
   public static Property newInstance(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null");
      }
      Property instance = new Property();
      instance.name = name;
      return instance;
   }

   /**
    * Gets the persistent identity of the instance.
    */
   public long getId() {
      return id;
   }

   /**
    * Gets the immutable version field. A version field is useful to detect concurrent modifications
    * to the same datastore record.
    */
   public int getVersion() {
      return version;
   }

   /**
    * Gets the name of this {@link Property}.
    */
   public String getName() {
      return name;
   }

   // Keep it protected
   protected void addNodeType(NodeType nodeType) {
      nodeTypes.add(nodeType);
   }

   /**
    * Gets {@link NodeType} associated with this {@link Property}.
    * <p>
    * <b>Warning:</b> the associated {@link NodeType}s are not dependent.
    */
   public Collection<NodeType> getNodeTypes() {
      // Defensive copy
      return new ArrayList<NodeType>(nodeTypes);
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ")");
   }
}