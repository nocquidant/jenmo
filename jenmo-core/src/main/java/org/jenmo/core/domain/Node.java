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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.jenmo.common.marker.ICopyable;
import org.jenmo.core.adapter.AdapterCmd;
import org.jenmo.core.adapter.AdapterFactory;
import org.jenmo.core.adapter.Adapters;
import org.jenmo.core.adapter.IAdapter;
import org.jenmo.core.adapter.IAdapterCmd;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSingle;
import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.constant.KindConstants;
import org.jenmo.core.constant.KindEnricher;
import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.descriptor.INodeCallback;
import org.jenmo.core.descriptor.INodeDescriptor;
import org.jenmo.core.descriptor.IPropertyDescriptor;
import org.jenmo.core.descriptor.INodeCallback.NodeCallback;
import org.jenmo.core.holder.INodeHolder;
import org.jenmo.core.orm.JpaSpiActions;
import org.jenmo.core.util.INodeFilter;

/**
 * {@link Node} entities could be assembled in graph connected by links called {@link Edge}. Those
 * links have an attribute called <code>kind</code> in order to identify the link, and also
 * distinguish between composition and aggregation for {@link Node}s. {@link Node} entities have
 * simple attributes (like <code>name</code>, <code>comment</code>, <code>idOwner</code>...) and are
 * made of:
 * <ul>
 * <li>properties using {@link NodeProperty} entities which are basic Java objects like Number,
 * String...
 * <li>fields using {@link NodeField} entities which are scalar fields
 * <li>revisions using {@link NodeRevision} entities which are versions of {@link Node} regarding
 * fields
 * </ul>
 * The attribute {@link NodeType} allows to clearly classify the current {@link Node}.
 * <p>
 * <img src="diagram.png" alt="class diagram image" ALIGN=center HSPACE=10 VSPACE=7 >
 * <p>
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "NODE")
// @NamedQuery(
// name="countProperty",
// query="SELECT COUNT(p) FROM model.NodeProperty p WHERE p.node= :param1 AND n.idProp= :param2"
// )
public class Node implements ICopyable {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NodeSeq")
   @SequenceGenerator(name = "NodeSeq", sequenceName = "NODE_ID_SEQ", allocationSize = 50)
   @Column(name = "ID")
   private long id;

   @Version
   @Column(name = "VERSION")
   private int version;

   @Basic(fetch = FetchType.LAZY)
   @Column(name = "ISROOT", updatable = false)
   private boolean isRoot;

   @Basic(fetch = FetchType.LAZY)
   @Column(name = "NAME")
   private String name;

   @Basic(fetch = FetchType.LAZY)
   @Column(name = "COMMENT")
   private String comment;

   @Basic(fetch = FetchType.LAZY)
   @Column(name = "DTCREATION", updatable = false)
   private long dtCreation;

   @Basic(fetch = FetchType.LAZY)
   @Column(name = "DTUPDATE")
   private long dtUpdate;

   @Basic(fetch = FetchType.LAZY)
   @Column(name = "IDOWNER", updatable = false)
   private int idOwner;

   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   // Manage deletion by code
   // Shouldn't be 'nullable = false' here => remove constructor + revision
   @JoinColumn(name = "NODETYPE_ID", updatable = false)
   private NodeType nodeType;

   @OneToMany(mappedBy = "to", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<Edge> inputs = new HashSet<Edge>();

   @OneToMany(mappedBy = "from", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<Edge> outputs = new HashSet<Edge>();

   @OneToMany(mappedBy = "node", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<NodeProperty> nodeProps = new HashSet<NodeProperty>();

   @OneToMany(mappedBy = "node", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<NodeField> nodeFields = new HashSet<NodeField>();

   @OneToMany(mappedBy = "revised", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<NodeRevision> nodeRevisions = new HashSet<NodeRevision>();

   private Node() {
   }

   public static Node newRoot(String name) {
      return newRoot(null, name, -1);
   }

   public static Node newRoot(NodeType nodeType, String name) {
      return newRoot(nodeType, name, -1);
   }

   public static Node newRoot(NodeType nodeType, String name, int idOwner) {
      Node instance = new Node();
      instance.isRoot = true;
      instance.nodeType = nodeType;
      instance.name = name;
      instance.dtCreation = GregorianCalendar.getInstance().getTimeInMillis();
      instance.idOwner = idOwner;
      return instance;
   }

   public static Node newOutput(Node input, String name) {
      return newOutput(input, null, true);
   }

   public static Node newOutput(Node input, String name, boolean cascaded) {
      return newOutput(input, null, name, cascaded);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name) {
      return newOutput(input, nodeType, name, true);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name, boolean cascaded) {
      return newOutput(input, nodeType, name, -1, cascaded, null);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name, String outputKey) {
      return newOutput(input, nodeType, name, -1, true, outputKey);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name, int idOwner) {
      return newOutput(input, nodeType, name, idOwner, true, null);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name, int idOwner,
         boolean cascaded) {
      return newOutput(input, nodeType, name, idOwner, cascaded, null);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name, int idOwner,
         String outputKey) {
      return newOutput(input, nodeType, name, -1, true, outputKey);
   }

   public static Node newOutput(Node input, NodeType nodeType, String name, int idOwner,
         boolean cascaded, String outputKey) {
      Node instance = new Node();
      instance.isRoot = false;
      instance.nodeType = nodeType;
      instance.name = name;
      instance.dtCreation = GregorianCalendar.getInstance().getTimeInMillis();
      Edge link = Edge.newInstance(input, instance, outputKey);
      link.setCascaded(cascaded);
      instance.inputs.add(link);
      input.outputs.add(link);
      instance.idOwner = idOwner;
      return instance;
   }

   /**
    * The copy factory.
    */
   public static Node copy(Node toCopy, INodeFilter filter) {
      if (toCopy == null) {
         throw new NullPointerException();
      }
      return doCopy(toCopy, filter, new HashMap<Node, Node>());
   }

   private static Node doCopy(Node toCopy, INodeFilter filter, Map<Node, Node> alreadyCopied) {
      if (filter != null && !filter.accept(toCopy)) {
         return null;
      }
      Node newInstance = new Node();

      // Simple fields: do copy
      newInstance.isRoot = toCopy.isRoot;
      newInstance.name = toCopy.name;
      newInstance.comment = toCopy.comment;
      newInstance.dtCreation = GregorianCalendar.getInstance().getTimeInMillis();
      newInstance.dtUpdate = 0;
      newInstance.isRoot = toCopy.isRoot;
      newInstance.idOwner = toCopy.idOwner;

      // NodeType: do not copy
      newInstance.nodeType = toCopy.nodeType;

      // Property values : do copy
      for (NodeProperty each : toCopy.nodeProps) {
         newInstance.nodeProps.add(NodeProperty.copy(newInstance, each));
      }

      // Field values: do copy
      for (NodeField each : toCopy.nodeFields) {
         newInstance.nodeFields.add(NodeField.copy(newInstance, each));
      }

      // Update map
      alreadyCopied.put(toCopy, newInstance);

      // Several inputs?
      if (toCopy.inputs.size() > 1) {
         // LOGGER.info("Other inputs may be lost! You may need to set them manually...");
      }

      for (Edge each : toCopy.outputs) {
         if (filter == null || filter.acceptOutput(each)) {
            Node output = each.getTarget();
            Node newOutput = alreadyCopied.get(output);
            if (newOutput == null) {
               newOutput = doCopy(output, filter, alreadyCopied);
            }
            if (newOutput != null) {
               Edge link = Edge.newInstance(newInstance, newOutput, each.getIndex());
               newOutput.inputs.add(link);
               newInstance.outputs.add(link);
            }
         }
      }

      // At the moment, do not copy revisions
      return newInstance;
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
    * Tests whether or not this {@link Node} is a source one i.e. has no input.
    */
   public boolean isSource() {
      return isRoot;
   }

   /**
    * Tests whether or not this {@link Node} is a sink one i.e. has no output.
    */
   public boolean isSink() {
      return isOutputEmpty();
   }

   /**
    * Gets the name of this {@link Node}.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name of this {@link Node}.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the comment of this {@link Node}, <code>null</code> if none.
    */
   public String getComment() {
      return comment;
   }

   /**
    * Sets the comment of this {@link Node}.
    */
   public void setComment(String comment) {
      this.comment = comment;
   }

   /**
    * Gets the id of the owner of this {@link Node}, <code>O</code> if none.
    */
   public int getIdOwner() {
      return idOwner;
   }

   /**
    * Gets the creation date for this {@link Node}.
    */
   public long getDtCreation() {
      return dtCreation;
   }

   /**
    * Gets the last update date for this {@link Node}.
    */
   public long getDtUpdate() {
      return dtUpdate;
   }

   /**
    * Gets the type of this {@link Node}.
    * <p>
    * <b>Warning:</b> the associated {@link NodeType} is not dependent.
    */
   public NodeType getNodeType() {
      return nodeType;
   }

   /**
    * Sets the type of this {@link Node}.
    * <p>
    * <b>Warning:</b> the associated {@link NodeType} is not dependent.
    */
   public void setNodeType(NodeType type) {
      this.nodeType = type;
   }

   /**
    * Tests whether or not this {@link Node} has input.
    */
   public boolean isInputEmpty() {
      return inputs.isEmpty();
   }

   /**
    * Gets size of the input collection of this {@link Node}.
    */
   public int getInputCount() {
      return inputs.size();
   }

   /**
    * Gets the input collection as a read-only {@link AdapterToSet}.
    */
   public AdapterToSet<Node, Edge> getInputs() {
      IAdapterCmd<Object, Node, Edge> cmd = new AdapterCmd<Object, Node, Edge>() {
         @Override
         public Node getValue(Edge arg) {
            return arg.getSource();
         }
      };

      // Don't want to modify the Set of inputs
      AdapterToSet<Node, Edge> out = Adapters.unmodifiable(AdapterFactory.set(cmd));
      out.adapt(inputs, getModeCounter(inputs));
      return out;
   }

   /**
    * Tests whether or not this {@link Node} has outputs.
    */
   public boolean isOutputEmpty() {
      return outputs.isEmpty();
   }

   /**
    * Gets size of the ouput collection of this {@link Node}.
    */
   public int getOutputCount() {
      return outputs.size();
   }

   /**
    * Gets outputs of this {@link Node} as a {@link Set}.
    */
   public AdapterToSet<Node, Edge> getOutputs() {
      return getOutputs(ALL_KINDS_AS_SET);
   }

   /**
    * Gets outputs of this {@link Node} for the given {@link Edge} qualifier.
    */
   public AdapterToSet<Node, Edge> getOutputs(Set<Integer> kinds) {
      return getOutputs(Descriptors.setForNode(), kinds);
   }

   /**
    * Gets outputs of this {@link Node} for the given {@link INodeDescriptor}.
    */
   public <T extends IAdapter<Edge>, A extends INodeDescriptor<T>> T getOutputs(A desc) {
      return getOutputs(desc, ALL_KINDS_AS_SET);
   }

   /**
    * Gets outputs of this {@link Node} for the given {@link Edge} qualifier and
    * {@link INodeDescriptor}.
    */
   public <T extends IAdapter<Edge>, A extends INodeDescriptor<T>> T getOutputs(A desc,
         final Set<Integer> kinds) {
      INodeCallback<Edge> callbacks = new INodeCallback<Edge>() {
         private Edge lastCreatedEdge;

         @Override
         public void setIndex(String index, Edge arg) {
            arg.setIndex(index);
         }

         @Override
         public boolean accept(Edge arg) {
            if (kinds != null) {
               if (kinds.contains(KindConstants.getInstance().getKind(KindEnricher.ALL_KEY))) {
                  return true;
               }
               return (kinds.contains(arg.getKind()));
            }
            return true;
         }

         @Override
         public void postNew(Edge arg) {
            Node from = arg.getSource();
            Node to = arg.getTarget();
            to.inputs.add(arg);
            from.outputs.add(arg);
            lastCreatedEdge = arg;
         }

         @Override
         public void postNewAttributes(Map<JenmoConstant, Object> args) {
            if (lastCreatedEdge == null) {
               throw new IllegalStateException();
            }
            if (args != null) {
               Object val = args.get(JenmoConstant.POST_ARG_CASCADED);
               if (val != null) {
                  Boolean b = (Boolean) val;
                  lastCreatedEdge.setCascaded(b);
               }
            }
         }
      };

      T adapter = desc.instantiateAdapter(this, callbacks);
      adapter.adapt(outputs, getModeCounter(outputs));
      return adapter;
   }

   /**
    * Tests whether or not this {@link Node} has property values for the given property.
    */
   public boolean isPropertyEmpty(Property property) {
      return (getPropertyCount(property) == 0);
   }

   /**
    * Gets size of the property value collection of this {@link Node}, for the given property.
    */
   public int getPropertyCount(Property property) {
      EntityManager em = JpaSpiActions.getInstance().getEntityManager(this);
      Query query = em
            .createQuery("SELECT COUNT(p) FROM model.NodeProperty p WHERE p.node= :param1 AND n.idProp= :param2");
      query.setParameter("param1", this);
      query.setParameter("param2", property.getId());
      Number out = (Number) query.getSingleResult();
      return out.intValue();
   }

   /**
    * Gets this {@link Node} property values, for the given {@link Property} and
    * {@link IPropertyDescriptor}.
    */
   public <T extends IAdapter<NodeProperty>, A extends IPropertyDescriptor<T>> T getProperties(
         Property property, A desc) {
      INodeCallback<NodeProperty> callbacks = new NodeCallback<NodeProperty>() {
         @Override
         public void setIndex(String index, NodeProperty arg) {
            arg.setIndex(index);
         }
      };
      T adapter = desc.instantiateAdapter(this, property, callbacks);
      adapter.adapt(nodeProps, getModeCounter(nodeProps));
      return adapter;
   }

   /**
    * Gets this {@link Node} property value as <code>T</code> object, for the given {@link Property}
    * .
    */
   public <T> T getProperty(Property property, Class<T> clazz) {
      AdapterToSingle<T, NodeProperty> adapter = getProperties(property, Descriptors
            .singleForProps(clazz));
      return adapter.get();
   }

   /**
    * Sets this {@link Node} property value for the given {@link Property}.
    */
   @SuppressWarnings("unchecked")
   public <T> boolean setProperty(Property property, T value) {
      AdapterToSingle<T, NodeProperty> adapter = getProperties(property, Descriptors
            .singleForProps((Class<T>) value.getClass()));
      return adapter.set(value);
   }

   /**
    * Tests whether or not this {@link Node} has field objects.
    */
   public boolean isFieldEmpty() {
      return nodeFields.isEmpty();
   }

   /**
    * Gets size of the field collection of this {@link Node}.
    */
   public int getFieldCount() {
      return nodeFields.size();
   }

   /**
    * Gets this {@link Node} fields.
    */
   public AdapterToMap<String, SplitBlob, NodeField> getFields() {
      IAdapterCmd<String, SplitBlob, NodeField> cmd = new AdapterCmd<String, SplitBlob, NodeField>() {
         @Override
         public String getIndex(NodeField arg) {
            return arg.getType();
         }

         @Override
         public SplitBlob getValue(NodeField arg) {
            return arg.getBlob();
         }

         @Override
         public NodeField instantiateAndAdd(String index, SplitBlob value,
               Set<? super NodeField> target) {
            NodeField newInstance = NodeField.newInstance(Node.this, index, value);
            target.add(newInstance);
            return newInstance;
         }
      };

      AdapterToMap<String, SplitBlob, NodeField> adapter = AdapterFactory.map(cmd);
      adapter.adapt(nodeFields, getModeCounter(nodeFields));
      return adapter;
   }

   /**
    * Gets {@link Edge}s of this {@link Node}, either connected to outputs or to inputs.
    */
   public Collection<Edge> getEdges(boolean outputsEdges) {
      if (outputsEdges) {
         return new ArrayList<Edge>(outputs);
      }
      return new ArrayList<Edge>(inputs);
   }

   /**
    * Creates revision for this {@link Node} with the given context.
    * <p>
    * All the properties of this {@link Node} are copied, and all the field objects of this
    * {@link Node} are shared. It is not possible to modify field objects of this {@link Node} any
    * more.
    */
   public NodeRevision createRevision(String context) {
      // TODO forbid modif of this node fields!
      Node revision = Node.newRoot("revision-of-" + this.getName());
      revision.isRoot = false;
      revision.idOwner = this.idOwner;

      // Copy all properties as it should be cheap
      int s = (this.nodeProps.size() < 10) ? 10 : this.nodeProps.size();
      revision.nodeProps = new HashSet<NodeProperty>(s);
      for (NodeProperty each : this.nodeProps) {
         NodeProperty eachCloned = NodeProperty.copy(revision, each);
         revision.nodeProps.add(eachCloned);
      }

      // Create revision
      NodeRevision nodeRevision = NodeRevision.newInstance(context, this, revision);
      nodeRevisions.add(nodeRevision);
      return nodeRevision;
   }

   /**
    * Gets revisions which has been created from this {@link Node}.
    */
   public AdapterToSet<NodeRevision, NodeRevision> getRevisions() {
      IAdapterCmd<Object, NodeRevision, NodeRevision> cmd = new AdapterCmd<Object, NodeRevision, NodeRevision>() {
         @Override
         public NodeRevision getValue(NodeRevision arg) {
            return arg;
         }
      };

      // Don't want to modify the Set of revision
      AdapterToSet<NodeRevision, NodeRevision> out = Adapters.unmodifiable(AdapterFactory.set(cmd));
      out.adapt(nodeRevisions, getModeCounter(nodeRevisions));
      return out;
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ",name=" + name + ")");
   }

   // -- NodeHolder management

   @Transient
   private WeakReference<INodeHolder> holderRef;

   /**
    * Gets {@link INodeHolder} if any, associated with this {@link Node}, <code>null</code>
    * otherwise.
    */
   public INodeHolder getHolder() {
      if (holderRef == null) {
         return null;
      }
      return holderRef.get();
   }

   /**
    * Sets {@link INodeHolder} associated with this {@link Node}.
    */
   public void setHolder(INodeHolder holder) {
      holderRef = new WeakReference<INodeHolder>(holder);
   }

   /**
    * Gets {@link INodeHolder} associated with this {@link Node}. If it doesn't exist yet, creates
    * it and sets this {@link Node} as its inner entity (see
    * {@link INodeHolder#setInnerEntity(Node)}).
    */
   @SuppressWarnings("unchecked")
   public <T extends INodeHolder> T getHolder(Class<T> clazz) {
      if (holderRef == null || holderRef.get() == null) {
         T newInstance = null;
         try {
            newInstance = clazz.newInstance();
         } catch (Exception e) {
            throw new IllegalArgumentException(e);
         }
         newInstance.setInnerEntity(this);
         holderRef = new WeakReference<INodeHolder>(newInstance);
      }
      return (T) holderRef.get();
   }

   // --

   @Transient
   private static final Set<Integer> ALL_KINDS_AS_SET = new HashSet<Integer>(Arrays
         .asList(KindConstants.getInstance().getKind(KindEnricher.ALL_KEY)));

   @Transient
   private volatile ModCounter modeCounterOutputs;
   @Transient
   private volatile ModCounter modeCounterProps;
   @Transient
   private volatile ModCounter modeCounterFields;
   @Transient
   private volatile ModCounter modeCounterRevisions;

   private ModCounter getModeCounter(Set<?> target) {
      if (target == outputs) {
         ModCounter out = modeCounterOutputs;
         if (out == null) { // First check no locking
            synchronized (Node.class) {
               out = modeCounterOutputs;
               if (out == null) { // Second check with locking
                  modeCounterOutputs = out = new ModCounter();
               }
            }
         }
         return out;
      }
      if (target == nodeProps) {
         ModCounter out = modeCounterProps;
         if (out == null) { // First check no locking
            synchronized (Node.class) {
               out = modeCounterProps;
               if (out == null) { // Second check with locking
                  modeCounterProps = out = new ModCounter();
               }
            }
         }
         return out;
      }
      if (target == nodeFields) {
         ModCounter out = modeCounterFields;
         if (out == null) { // First check no locking
            synchronized (Node.class) {
               out = modeCounterFields;
               if (out == null) { // Second check with locking
                  modeCounterFields = out = new ModCounter();
               }
            }
         }
         return out;
      }
      if (target == nodeRevisions) {
         ModCounter out = modeCounterRevisions;
         if (out == null) { // First check no locking
            synchronized (Node.class) {
               out = modeCounterRevisions;
               if (out == null) { // Second check with locking
                  modeCounterRevisions = out = new ModCounter();
               }
            }
         }
         return out;
      }
      return null;
   }

   public static class ModCounter {
      private int modCount;

      public int modCount() {
         return modCount;
      }

      // Do not synchronized here (deliberated choice for performance reasons)
      public int incModCount() {
         return (++modCount);
      }
   }
}
