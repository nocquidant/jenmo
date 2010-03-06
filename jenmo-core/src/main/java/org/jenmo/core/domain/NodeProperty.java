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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jenmo.common.marker.ICopyable;

/**
 * A {@link NodeProperty} entity holds a property value as String in database. The
 * <code>index</code> attribute is there if we want the set of {@link NodeProperty}s to be ordered
 * in {@link Node}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "NODEPROPERTY")
public class NodeProperty implements ICopyable {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NodePropertySeq")
   @SequenceGenerator(name = "NodePropertySeq", sequenceName = "NODEPROPERTY_ID_SEQ", allocationSize = 50)
   @Column(name = "ID")
   private long id;

   @Version
   @Column(name = "VERSION")
   private int version;

   /** The node which holds this property */
   @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
   @JoinColumn(name = "NODE_ID", nullable = false, updatable = false)
   private Node node;

   /** The property name */
   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   // Manage deletion by code
   @JoinColumn(name = "PROP_ID", nullable = false, updatable = false)
   private Property prop;

   /** The value of this PropertyValue */
   // Could be null as it may be the novalue
   // Lazy here as we have a cache
   @Basic(fetch = FetchType.LAZY)
   @Column(name = "VALUE", updatable = false)
   private String value;

   /** The index to be able to map the value in List or in Map */
   // Could be null as it may be useless
   @Basic(fetch = FetchType.LAZY)
   @Column(name = "IDX")
   private String idx;

   private NodeProperty() {
   }

   /**
    * Creates a new instance with the given parameters.
    */
   public static NodeProperty newInstance(Node node, Property prop, String value) {
      return newInstance(node, prop, null, value);
   }

   /**
    * Creates a new instance with the given parameters.
    */
   public static NodeProperty newInstance(Node node, Property prop, String idx, String value) {
      // Assertions
      // - value could be null as it may be the novalue
      // - idx could be null as it may be useless
      if (node == null || prop == null) {
         throw new NullPointerException();
      }
      NodeProperty instance = new NodeProperty();
      instance.node = node;
      instance.prop = prop;
      instance.idx = idx;
      instance.value = value;
      return instance;
   }

   /**
    * The copy factory.
    */
   public static NodeProperty copy(Node newNode, NodeProperty toCopy) {
      // Assertions (see above)
      if (newNode == null || toCopy == null || toCopy.node == null || toCopy.prop == null) {
         throw new NullPointerException();
      }
      NodeProperty instance = new NodeProperty();
      instance.node = newNode;
      // Do not copy Property
      instance.prop = toCopy.prop;
      instance.idx = toCopy.idx;
      instance.value = toCopy.value;
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
    * Gets {@link Node} this {@link NodeProperty} belongs to.
    */
   public Node getNode() {
      return node;
   }

   /**
    * Gets {@link Property} of this {@link NodeProperty}.
    * <p>
    * <b>Warning:</b> the associated {@link Property} is not dependent.
    */
   public Property getProperty() {
      return prop;
   }

   /**
    * Gets value of this {@link NodeProperty} as string.
    */
   public String getValue() {
      return value;
   }

   /**
    * Gets index which allow this {@link NodeProperty} to be ordered in its parent {@link Node} if
    * any, <code>null</null> otherwise.
    */
   public String getIndex() {
      return idx;
   }

   /**
    * Sets index which allow this {@link NodeProperty} to be ordered in its parent {@link Node}.
    */
   // Keep it protected
   protected void setIndex(String idx) {
      this.idx = idx;
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ")");
   }
}
