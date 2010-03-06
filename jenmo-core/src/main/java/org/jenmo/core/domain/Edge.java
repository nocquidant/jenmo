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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * A {@link Edge} entity modelizes links in graph of {@link Node}s. It holds a <code>kind</code>
 * attribute in order to allow different kinds of links, and an <code>index</code> attribute if we
 * want the {@link Node}s to be ordered.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "EDGE")
public class Edge {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EdgeSeq")
   @SequenceGenerator(name = "EdgeSeq", sequenceName = "EDGE_ID_SEQ", allocationSize = 50)
   @Column(name = "ID")
   private long id;

   @Version
   @Column(name = "VERSION")
   private int version;

   /** The parent */
   @ManyToOne(fetch = FetchType.LAZY)
   // Manage deletion by code
   @JoinColumn(name = "FROM_ID", nullable = false, updatable = false)
   private Node from;

   /** The target */
   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   // Manage deletion by code
   @JoinColumn(name = "TO_ID", nullable = false, updatable = false)
   private Node to;

   /** To know whether the deletion is cascaded to the target {@link Node} or not. */
   @Basic(fetch = FetchType.LAZY)
   @Column(name = "CASCADED", updatable = false)
   private boolean cascaded;

   /** The qualifier of this relationship */
   @Basic(fetch = FetchType.LAZY)
   @Column(name = "KIND", updatable = false)
   private int kind;

   /** The target index to be able to map kinship in List or in Map */
   // To be able to put edges in ArrayList, we must change for mutability (updatable=true)
   @Basic(fetch = FetchType.LAZY)
   @Column(name = "IDX", updatable = false)
   private String idx;

   private Edge() {
   }

   public static Edge newInstance(Node source, Node target) {
      return newInstance(source, target, -1, null);
   }

   public static Edge newInstance(Node source, Node target, String idx) {
      return newInstance(source, target, -1, idx);
   }

   public static Edge newInstance(Node source, Node target, int kind) {
      return newInstance(source, target, kind, null);
   }

   public static Edge newInstance(Node source, Node target, int kind, String idx) {
      if (source == null || target == null) {
         throw new NullPointerException();
      }
      Edge instance = new Edge();
      instance.from = source;
      instance.to = target;
      instance.cascaded = true;
      instance.kind = kind;
      instance.idx = idx;
      return instance;
   }

   protected final void resetForDeletion() {
      from = null;
      to = null;
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
    * Gets source {@link Node}.
    */
   public Node getSource() {
      return from;
   }

   /**
    * Gets target {@link Node}.
    */
   public Node getTarget() {
      return to;
   }

   /**
    * To know whether the deletion is cascaded to the target {@link Node} or not.
    */
   public boolean isCascaded() {
      return cascaded;
   }

   // Keep it protected!
   protected void setCascaded(boolean cascaded) {
      if (this.cascaded != cascaded) {
         this.cascaded = cascaded;
      }
   }

   /**
    * Gets the kind of relationship of this {@link Edge}.
    */
   public int getKind() {
      return kind;
   }

   /**
    * Gets index which allow this {@link Edge} to be ordered in its parent {@link Node} if any,
    * <code>null</null> otherwise.
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
