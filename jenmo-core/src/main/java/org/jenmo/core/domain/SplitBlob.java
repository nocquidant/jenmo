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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jenmo.common.marker.ICopyable;
import org.jenmo.common.multiarray.IAccessor;
import org.jenmo.common.multiarray.IndexIterator;
import org.jenmo.common.multiarray.MultiArrayJava;
import org.jenmo.core.listener.IListener;
import org.jenmo.core.listener.SplitBlobEvent;
import org.jenmo.core.multiarray.IBlobPartAccessor;
import org.jenmo.core.multiarray.MultiArrayBlobPart;
import org.jenmo.core.util.SplitBlobUtils;
import org.jenmo.core.util.SplitBlobUtils.PType;

/**
 * A {@link SplitBlob} entity maps a multidimensional Java array into database. It is made of
 * {@link SplitBlobPart}s and the size of the {@link SplitBlobPart} is configurable using the
 * <code>elmtCountForPart</code> attribute.
 * <p>
 * Client code may be notified when extracting a new {@link SplitBlobPart} from database using a
 * {@link IListener} (see <code>getValues</code>/<code>setValues</code> methods or
 * {@link IBlobPartAccessor}).
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
@Entity
@Table(name = "SPLITBLOB")
public class SplitBlob implements ICopyable {
   /** Default element count in each part */
   public static final int DEFAULT_PART_COUNT = 1000000;

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SplitBlobSeq")
   @SequenceGenerator(name = "SplitBlobSeq", sequenceName = "SPLITBLOB_ID_SEQ", allocationSize = 10)
   @Column(name = "ID")
   private long id;

   @Version
   @Column(name = "VERSION")
   private int version;

   /** The type of elements in this Blob */
   @Basic
   @Column(name = "PTYPE", updatable = false)
   private SplitBlobUtils.PType ptype;

   /** The size of this Blob */
   @Basic
   @Column(name = "NUMELT", updatable = false)
   private long elmtCount;

   /** The size of each SplitBlobPart of this Blob. N.B. the last part could have a different value. */
   @Basic
   @Column(name = "NUMELTEACHPART", updatable = false)
   private int elmtCountEachPart = DEFAULT_PART_COUNT;

   /** The number of elements in each dimension (as String, separator = ';') */
   @Basic
   @Column(name = "SHAPE", nullable = false, updatable = false)
   private String shape;

   /** The parts of this blobs */
   @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true)
   @OrderBy(value = "ordr")
   private List<SplitBlobPart> parts;

   private SplitBlob() {
   }

   /**
    * Creates a new empty instance.
    * 
    * @param elmtType
    *           the <code>Class</code> object representing the component type of the new array
    * @param shape
    *           an array of <code>int</code> representing the dimensions of the new array
    */
   public static SplitBlob newInstance(Class<?> elmtType, int[] shape) {
      return newInstance(DEFAULT_PART_COUNT, elmtType, shape);
   }

   /**
    * Creates a new instance and fill it with the given array.
    */
   public static SplitBlob newInstance(Object values) {
      return newInstance(DEFAULT_PART_COUNT, values);
   }

   /**
    * Creates new empty instance.
    * 
    * @param elmtCountForPart
    *           the number of element in each parts
    * @param elmtType
    *           the <code>Class</code> object representing the component type of the new array
    * 
    * @param shape
    *           an array of <code>int</code> representing the dimensions of the new array
    */
   public static SplitBlob newInstance(int elmtCountForPart, Class<?> elmtType, int[] shape) {
      if (elmtCountForPart <= 0) {
         throw new IllegalArgumentException("elmtCountForPart cannot be <= 0");
      }
      if (elmtType == null) {
         throw new IllegalArgumentException("ElmtType cannot be null");
      }
      if (shape == null || shape.length == 0) {
         throw new IllegalArgumentException("shape cannot be null or length = 0");
      }
      SplitBlob instance = new SplitBlob();
      instance.elmtCountEachPart = elmtCountForPart;
      instance.elmtCount = SplitBlobUtils.computeSize(shape);
      instance.ptype = SplitBlobUtils.extractType(elmtType);
      instance.shape = encodeShape(shape);
      instance.preinitParts();
      return instance;
   }

   /**
    * Creates a new instance and fill it with the given array.
    * 
    * @param elmtCountForPart
    *           the number of element in each parts
    */
   public static SplitBlob newInstance(int elmtCountForPart, Object values) {
      if (elmtCountForPart <= 0) {
         throw new IllegalArgumentException("elmtCountForPart cannot be <= 0");
      }
      if (values == null) {
         throw new IllegalArgumentException("Values cannot be null");
      }
      SplitBlob instance = new SplitBlob();
      instance.elmtCountEachPart = elmtCountForPart;
      instance.setValues(values);
      return instance;
   }

   /**
    * The copy factory.
    */
   public static SplitBlob copy(SplitBlob toCopy) {
      if (toCopy == null) {
         throw new IllegalArgumentException("Cannot copy null");
      }
      SplitBlob instance = new SplitBlob();
      instance.ptype = toCopy.ptype;
      instance.elmtCountEachPart = toCopy.elmtCountEachPart;
      instance.shape = toCopy.shape;
      instance.elmtCount = toCopy.elmtCount;
      instance.parts = new ArrayList<SplitBlobPart>(toCopy.parts.size());
      for (SplitBlobPart each : toCopy.parts) {
         instance.parts.add(SplitBlobPart.copy(instance, each));
      }
      return instance;
   }

   private void preinitParts() {
      int numPart = (int) (elmtCount / elmtCountEachPart);
      if ((elmtCount % elmtCountEachPart) != 0) {
         numPart++;
      }
      parts = new ArrayList<SplitBlobPart>(numPart);
      for (int i = 0; i < numPart; i++) {
         parts.add(null);
      }
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
    * Gets the number of elements in this {@link SplitBlob}.
    */
   public long getElmtCount() {
      return elmtCount;
   }

   /**
    * Gets the dimensions of this {@link SplitBlob}.
    * 
    * @return array whose length is the rank of this {@link SplitBlob} and whose elements represent
    *         the length of each of it's dimensions
    */
   public int[] getShape() {
      return decodeShape(shape);
   }

   /**
    * Gets the number of elements for each {@link SplitBlobPart}, in this {@link SplitBlob}.
    */
   public int getElmtCountEachPart() {
      return elmtCountEachPart;
   }

   /**
    * Gets the number of elements for the last {@link SplitBlobPart}as it could be different from
    * the previous ones.
    */
   public int getElmtCountLastPart() {
      int out = (int) (elmtCount % elmtCountEachPart);
      if (out == 0) {
         out = elmtCountEachPart;
      }
      return out;
   }

   /**
    * Gets the type of the elements in this {@link SplitBlob}.
    */
   public PType getElmtType() {
      return ptype;
   }

   /**
    * Gets the {@link SplitBlobPart} at the given position in this {@link SplitBlob}.
    */
   // Thread safe method (!= SplitBlobPart methods not thread safe)
   public SplitBlobPart getPart(int index) {
      SplitBlobPart out = parts.get(index);
      // Must initialize part i?
      if (out == null) { // First check (no locking)
         synchronized (this) {
            out = parts.get(index);
            if (out == null) { // Second check (with locking)
               int elmtCountForPart = (index == (parts.size() - 1)) ? getElmtCountLastPart()
                     : elmtCountEachPart;
               int sizeForPart = (int) (elmtCountForPart) * SplitBlobUtils.sizeOf(ptype);
               out = SplitBlobPart.newInstance(this, index, sizeForPart);
               parts.set(index, out);
            }
         }
      }
      return out;
   }

   private static String encodeShape(final int[] shape) {
      String out = "";
      for (int each : shape) {
         out = out + String.valueOf(each) + ";";
      }
      return out.substring(0, out.length() - 1);
   }

   private static int[] decodeShape(final String shape) {
      String[] strs = shape.split(";");
      int[] out = new int[strs.length];
      for (int i = 0; i < strs.length; i++) {
         out[i] = Integer.parseInt(strs[i]);
      }
      return out;
   }

   /**
    * Gets a {@link IBlobPartAccessor} over this {@link SplitBlob}.
    */
   public IBlobPartAccessor getAccessor() {
      return getAccessor(null);
   }

   /**
    * Gets a {@link IBlobPartAccessor} over this {@link SplitBlob} with the given listener in order
    * to be notified for part rollings during access.
    */
   public IBlobPartAccessor getAccessor(final IListener<SplitBlobEvent> clientListener) {
      IBlobPartAccessor out = new MultiArrayBlobPart(this);
      out.setPartListener(clientListener);
      return out;
   }

   /**
    * Set values of this {@link SplitBlob}.
    */
   public void setValues(Object values) {
      setValues(values, null);
   }

   /**
    * Set values of this {@link SplitBlob} with the given listener in order to be notified for part
    * rollings during write access.
    */
   public void setValues(final Object values, final IListener<SplitBlobEvent> clientListener) {
      if (values.getClass().isArray() == false) {
         throw new IllegalArgumentException("Only arrays are supported");
      }
      int rank = SplitBlobUtils.getRank(values.getClass());
      if (rank == 0) {
         throw new IllegalArgumentException();
      }
      Class<?> componentType = SplitBlobUtils.getComponentType(values.getClass());

      this.ptype = SplitBlobUtils.extractType(componentType);
      int[] lengths = SplitBlobUtils.getShape(values, rank);
      int[] fromPos = new int[rank];
      this.elmtCount = SplitBlobUtils.numberOfElements(lengths);
      this.shape = encodeShape(lengths);

      preinitParts();
      IAccessor reader = new MultiArrayJava(values);
      IBlobPartAccessor writer = getAccessor();
      writer.setPartListener(clientListener);

      switch (ptype) {
      case PDOUBLE:
         rwValuesDouble(fromPos, lengths, reader, writer, null);
         break;
      case PFLOAT:
         rwValuesFloat(fromPos, lengths, reader, writer, null);
         break;
      case PLONG:
         rwValuesLong(fromPos, lengths, reader, writer, null);
         break;
      case PINTEGER:
         rwValuesInteger(fromPos, lengths, reader, writer, null);
         break;
      case PSHORT:
         rwValuesShort(fromPos, lengths, reader, writer, null);
         break;
      case PBYTE:
         rwValuesByte(fromPos, lengths, reader, writer, null);
         break;
      default:
         throw new IllegalArgumentException();
      }

      writer.close();
   }

   /**
    * Gets values as <code>T</code> object of this {@link SplitBlob}.
    */
   public <T> T getValues(Class<T> clazz) {
      int[] lengths = getShape();
      int[] fromPos = new int[lengths.length];
      return getValues(clazz, fromPos, lengths, null);
   }

   /**
    * Gets values as <code>T</code> object of this {@link SplitBlob}, with the given listener in
    * order to be notified for part rollings during read access.
    */
   public <T> T getValues(Class<T> clazz, final IListener<SplitBlobEvent> clientListener) {
      int[] lengths = getShape();
      int[] fromPos = new int[lengths.length];
      return getValues(clazz, fromPos, lengths, clientListener);
   }

   /**
    * Gets values as <code>T</code> object of this {@link SplitBlob}, from <code>fromPos</code>
    * positions, to <code>fromPos</code>.
    */
   public <T> T getValues(final Class<T> clazz, final int[] fromPos, final int[] lengths) {
      return getValues(clazz, fromPos, lengths, null);
   }

   /**
    * Gets values as <code>T</code> object of this {@link SplitBlob}, from <code>fromPos</code>
    * positions, to <code>fromPos</code>. Also with the given listener in order to be notified for
    * part rollings during read access.
    */
   @SuppressWarnings("unchecked")
   public <T> T getValues(final Class<T> clazz, final int[] fromPos, final int[] lengths,
         final IListener<SplitBlobEvent> clientListener) {
      if (fromPos.length != lengths.length) {
         throw new IllegalArgumentException();
      }
      Class<?> componentType = SplitBlobUtils.getComponentType(clazz);
      T values = (T) Array.newInstance(componentType, lengths);

      IAccessor writer = new MultiArrayJava(values);
      IBlobPartAccessor reader = getAccessor();
      reader.setPartListener(clientListener);

      final int rank = fromPos.length;
      final int[] toPosExcl = new int[rank];
      for (int i = 0; i < rank; i++) {
         toPosExcl[i] = fromPos[i] + lengths[i];
      }

      ITransformationIndex transf = new ITransformationIndex() {
         final int[] buff = new int[rank];

         @Override
         public int[] transform(int[] index) {
            for (int i = 0; i < buff.length; i++) {
               buff[i] = index[i] - fromPos[i];
            }
            return buff;
         }
      };

      switch (ptype) {
      case PDOUBLE:
         rwValuesDouble(fromPos, toPosExcl, reader, writer, transf);
         break;
      case PFLOAT:
         rwValuesFloat(fromPos, toPosExcl, reader, writer, transf);
         break;
      case PLONG:
         rwValuesLong(fromPos, toPosExcl, reader, writer, transf);
         break;
      case PINTEGER:
         rwValuesInteger(fromPos, toPosExcl, reader, writer, transf);
         break;
      case PSHORT:
         rwValuesShort(fromPos, toPosExcl, reader, writer, transf);
         break;
      case PBYTE:
         rwValuesByte(fromPos, toPosExcl, reader, writer, transf);
         break;
      default:
         throw new IllegalArgumentException();
      }

      reader.close();
      return values;
   }

   private void rwValuesDouble(final int[] fromPos, final int[] toPosExcl, final IAccessor reader,
         final IAccessor writer, final ITransformationIndex beforeWrite) {
      IndexIterator ii = new IndexIterator(fromPos, toPosExcl);
      for (; ii.notDone(); ii.incr()) {
         int[] index = ii.value();
         double value = reader.getDouble(index);
         int[] transfIndex = index;
         if (beforeWrite != null) {
            transfIndex = beforeWrite.transform(index);
         }
         writer.setDouble(transfIndex, value);
      }
   }

   private void rwValuesFloat(final int[] fromPos, final int[] toPosExcl, final IAccessor reader,
         final IAccessor writer, final ITransformationIndex beforeWrite) {
      IndexIterator ii = new IndexIterator(fromPos, toPosExcl);
      for (; ii.notDone(); ii.incr()) {
         int[] index = ii.value();
         float value = reader.getFloat(index);
         int[] transfIndex = index;
         if (beforeWrite != null) {
            transfIndex = beforeWrite.transform(index);
         }
         writer.setFloat(transfIndex, value);
      }
   }

   private void rwValuesLong(final int[] fromPos, final int[] toPosExcl, final IAccessor reader,
         final IAccessor writer, final ITransformationIndex beforeWrite) {
      IndexIterator ii = new IndexIterator(fromPos, toPosExcl);
      for (; ii.notDone(); ii.incr()) {
         int[] index = ii.value();
         long value = reader.getLong(index);
         int[] transfIndex = index;
         if (beforeWrite != null) {
            transfIndex = beforeWrite.transform(index);
         }
         writer.setLong(transfIndex, value);
      }
   }

   private void rwValuesInteger(final int[] fromPos, final int[] toPosExcl, final IAccessor reader,
         final IAccessor writer, final ITransformationIndex beforeWrite) {
      IndexIterator ii = new IndexIterator(fromPos, toPosExcl);
      for (; ii.notDone(); ii.incr()) {
         int[] index = ii.value();
         int value = reader.getInt(index);
         int[] transfIndex = index;
         if (beforeWrite != null) {
            transfIndex = beforeWrite.transform(index);
         }
         writer.setInt(transfIndex, value);
      }
   }

   private void rwValuesShort(final int[] fromPos, final int[] toPosExcl, final IAccessor reader,
         final IAccessor writer, final ITransformationIndex beforeWrite) {
      IndexIterator ii = new IndexIterator(fromPos, toPosExcl);
      for (; ii.notDone(); ii.incr()) {
         int[] index = ii.value();
         short value = reader.getShort(index);
         int[] transfIndex = index;
         if (beforeWrite != null) {
            transfIndex = beforeWrite.transform(index);
         }
         writer.setShort(transfIndex, value);
      }
   }

   private void rwValuesByte(final int[] fromPos, final int[] toPosExcl, final IAccessor reader,
         final IAccessor writer, final ITransformationIndex beforeWrite) {
      IndexIterator ii = new IndexIterator(fromPos, toPosExcl);
      for (; ii.notDone(); ii.incr()) {
         int[] index = ii.value();
         byte value = reader.getByte(index);
         int[] transfIndex = index;
         if (beforeWrite != null) {
            transfIndex = beforeWrite.transform(index);
         }
         writer.setByte(transfIndex, value);
      }
   }

   @Override
   public String toString() {
      return (super.toString() + "(pk=" + id + ")");
   }

   private static interface ITransformationIndex {
      int[] transform(int[] index);
   }
}
