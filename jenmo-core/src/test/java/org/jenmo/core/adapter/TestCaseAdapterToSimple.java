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
package org.jenmo.core.adapter;

import java.util.Iterator;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToSingle;
import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeProperty;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCaseAdapterToSimple {
   private AdapterToSingle<Double, NodeProperty> adapter;

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);
      Node node = Node.newRoot(type, "node");
      adapter = node.getProperties(prop, Descriptors.singleForProps(Double.class));
      fillAdapters();
   }

   private void fillAdapters() {
      adapter.set(999.99);
      Assert.assertEquals(adapter.get(), 999.99, 10e-9);
   }

   @Test
   public void testSet() {
      adapter.clear();
      Assert.assertTrue(adapter.get() == null);
      fillAdapters();
   }

   @Test
   public void testIterator() {
      Iterator<Double> it = adapter.iterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(999.99, it.next(), 10e-9);
      Assert.assertFalse(it.hasNext());
   }
}
