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

import org.jenmo.common.marker.IImmutable;
import org.jenmo.core.adapter.IAdapter;
import org.jenmo.core.adapter.spe.BlobIntoMapForRev;
import org.jenmo.core.descriptor.IPropertyDescriptor;

/**
 * A {@link NodeRevision} entity is a sort of version for a {@link Node}. When creating a revision
 * from {@link Node}, all the properties of the 'revised' {@link Node} are copied, and all the
 * fields of the 'revised' {@link Node} are shared. It is not possible to modify fields from the
 * revised {@link Node} any more. You could only add/remove fields to/from the 'revision'.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "NODEREVISION")
public class NodeRevision implements IImmutable {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NodeRevisionSeq")
   @SequenceGenerator(name = "NodeRevisionSeq", sequenceName = "NODEREVISION_ID_SEQ", allocationSize = 10)
   @Column(name = "ID")
   private long id;

   @Column(name = "CONTEXT", updatable = false)
   private String context;

   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "REVISED_ID", nullable = false, updatable = false)
   private Node revised;

   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
   @JoinColumn(name = "REVISION_ID", nullable = false, updatable = false)
   private Node revision;

   private NodeRevision() {
   }

   protected static NodeRevision newInstance(String context, Node revised, Node revision) {
      if (context == null || context.length() == 0) {
         throw new NullPointerException();
      }
      if (revised == null || revision == null) {
         throw new NullPointerException();
      }
      NodeRevision instance = new NodeRevision();
      instance.context = context;
      instance.revised = revised;
      instance.revision = revision;
      return instance;
   }

   /**
    * Gets the persistent identity of the instance.
    */
   public long getId() {
      return id;
   }

   /**
    * Gets the context of this revision.
    */
   public String getContext() {
      return context;
   }

   /**
    * Gets the revised {@link Node} this {@link NodeRevision} belongs to.
    */
   public Node getRevised() {
      return revised;
   }

   /**
    * Tests whether or not this {@link NodeRevision} has property values for the given property.
    */
   public boolean isPropertyEmpty(Property property) {
      return revision.isPropertyEmpty(property);
   }

   /**
    * Gets size of the property value collection of this {@link NodeRevision}, for the given
    * property.
    */
   public int getPropertyCount(Property property) {
      return revision.getPropertyCount(property);
   }

   /**
    * Gets property values of this {@link NodeRevision}, for the given {@link Property} and
    * {@link IPropertyDescriptor}.
    */
   public <T extends IAdapter<NodeProperty>, A extends IPropertyDescriptor<T>> T getProperties(
         Property property, A desc) {
      return revision.getProperties(property, desc);
   }

   /**
    * Gets this {@link NodeRevision} property value as <code>T</code> object, for the given
    * {@link Property}.
    */
   public <T> T getProperty(Property property, Class<T> clazz) {
      return revision.getProperty(property, clazz);
   }

   /**
    * Sets this {@link NodeRevision} property value, for the given {@link Property}.
    */
   public <T> boolean setProperty(Property property, T value) {
      return revision.setProperty(property, value);
   }

   /**
    * Gets this {@link NodeRevision} fields, for the given field type.
    */
   public BlobIntoMapForRev getfields() {
      // The wanted feature is probably the ability to share fields...
      // (1) But could we revised a revision?
      // (2) Could we add a child node to a revision?
      // Keep it simple for now... (1) -> no, (2) -> no
      // Revision behavior:
      // - try to get fields from revision, get from revised if not found
      // - add/remove new fields to revision only
      BlobIntoMapForRev out = new BlobIntoMapForRev();
      out.decorate(revised.getFields(), revision.getFields());
      return out;
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ")");
   }
}
