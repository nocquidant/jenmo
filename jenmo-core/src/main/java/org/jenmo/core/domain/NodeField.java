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

import org.jenmo.common.marker.ICopyable;
import org.jenmo.common.marker.IImmutable;

/**
 * A {@link NodeField} entity holds a scalar field for a given <code>type</code>, using a
 * {@link SplitBlob} entity.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "NODEFIELD")
public class NodeField implements IImmutable, ICopyable {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NodeFieldSeq")
   @SequenceGenerator(name = "NodeFieldSeq", sequenceName = "NODEFIELD_ID_SEQ", allocationSize = 10)
   @Column(name = "ID")
   private long id;

   /** The node */
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "NODE_ID", nullable = false, updatable = false)
   private Node node;

   /** The type of the fields */
   @Basic(optional = false)
   @Column(name = "FTYPE", nullable = false, updatable = false)
   private String ftype;

   @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
   @JoinColumn(name = "BLOB_ID", nullable = false, updatable = false)
   private SplitBlob blob;

   private NodeField() {
   }

   /**
    * Creates a new instance for the given {@code node}, {@code type} and {@code blob}.
    */
   public static NodeField newInstance(Node node, String type, SplitBlob blob) {
      if (node == null) {
         throw new NullPointerException();
      }
      if (type == null || type.length() == 0) {
         throw new IllegalArgumentException();
      }
      if (blob == null) {
         throw new NullPointerException();
      }
      NodeField instance = new NodeField();
      instance.node = node;
      instance.ftype = type;
      instance.blob = blob;
      return instance;
   }

   /**
    * Creates a new instance for the given {@code node}, {@code type} and {@code values}.
    */
   public static NodeField newInstance(Node node, String type, Object values) {
      SplitBlob blob = SplitBlob.newInstance(values);
      return newInstance(node, type, blob);
   }

   /**
    * Creates a new instance for the given {@code node}, {@code type}, {@code elmtCountForPart} and
    * {@code values}.
    */
   public static NodeField newInstance(Node node, String type, int elmtCountForPart, Object values) {
      SplitBlob blob = SplitBlob.newInstance(elmtCountForPart, values);
      return newInstance(node, type, blob);
   }

   /**
    * The copy factory.
    */
   public static NodeField copy(Node newParent, NodeField toCopy) {
      if (newParent == null) {
         throw new NullPointerException("Parent cannot be null");
      }
      if (toCopy == null) {
         throw new NullPointerException("Cannot copy null");
      }
      NodeField instance = new NodeField();
      instance.node = newParent;
      instance.ftype = toCopy.ftype;
      instance.blob = SplitBlob.copy(toCopy.blob);
      return instance;
   }

   /**
    * Gets the persistent identity of the instance.
    */
   public long getId() {
      return id;
   }

   /**
    * Gets {@link Node} this {@link NodeField} belongs to.
    */
   public Node getNode() {
      return node;
   }

   /**
    * Gets the type of this field.
    */
   public String getType() {
      return ftype;
   }

   /**
    * Gets the underlying {@link SplitBlob}.
    */
   public SplitBlob getBlob() {
      return blob;
   }

   /**
    * Shortcut to get values directly from the underlying {@link SplitBlob}.
    */
   public <T> T getValues(Class<T> clazz) {
      return blob.getValues(clazz);
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ")");
   }
}
