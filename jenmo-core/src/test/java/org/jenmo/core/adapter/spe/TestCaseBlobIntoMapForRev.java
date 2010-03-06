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
package org.jenmo.core.adapter.spe;

import java.util.HashMap;
import java.util.Map;

import org.jenmo.core.adapter.AbstractSizedCase;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeField;
import org.jenmo.core.domain.NodeRevision;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.domain.SplitBlob;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCaseBlobIntoMapForRev extends AbstractSizedCase {
   private Node revised;
   private NodeRevision revision;

   private static SplitBlob newSplitBlob() {
      return SplitBlob.newInstance(new int[] { 1 });
   }

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);

      revised = Node.newRoot(type, "node");
      AdapterToMap<String, SplitBlob, NodeField> adapterRevised = revised.getFields();
      for (int i = 0; i < SIZE; i++) {
         adapterRevised.put("type#" + i, newSplitBlob());
      }

      revision = revised.createRevision("rev1");

      Map<String, SplitBlob> revisedMap = revised.getFields();
      Map<String, SplitBlob> revisionMap = revision.getfields();
      Assert.assertEquals(revisedMap.size(), SIZE);
      Assert.assertEquals(revisionMap.size(), SIZE);
      for (Map.Entry<String, SplitBlob> each : revisedMap.entrySet()) {
         revisionMap.containsKey(each.getKey());
         revisionMap.containsValue(each.getValue());
      }
   }

   @Test
   public void testPut() {
      revision.getfields().put("type#?", newSplitBlob());
      Assert.assertEquals(revised.getFields().size(), SIZE);
      Assert.assertEquals(revision.getfields().size(), SIZE + 1);
   }

   @Test
   public void testPutAll() {
      Map<String, SplitBlob> buff = new HashMap<String, SplitBlob>();
      buff.put("type#?1", newSplitBlob());
      buff.put("type#?2", newSplitBlob());

      revision.getfields().putAll(buff);
      Assert.assertEquals(revised.getFields().size(), SIZE);
      Assert.assertEquals(revision.getfields().size(), SIZE + 2);
   }

   @Test
   public void testContains() {
      SplitBlob blob = newSplitBlob();
      revision.getfields().put("type#?", blob);

      Assert.assertFalse(revised.getFields().containsValue(blob));
      Assert.assertTrue(revision.getfields().containsValue(blob));
      Assert.assertFalse(revised.getFields().containsKey("type#?"));
      Assert.assertTrue(revision.getfields().containsKey("type#?"));
   }

   @Test
   public void testEntrySet() {
      // TODO
   }

   @Test
   public void testKeySet() {
      // TODO
   }

   @Test
   public void testValues() {
      revised.getFields().clear();
      revision.getfields().clear();
      Assert.assertTrue(revised.getFields().isEmpty());
      Assert.assertTrue(revision.getfields().isEmpty());

      Map<String, SplitBlob> buff = new HashMap<String, SplitBlob>();
      buff.put("type#1", newSplitBlob());
      buff.put("type#2", newSplitBlob());
      revised.getFields().putAll(buff);

      buff.put("type#3", newSplitBlob());
      revision.getfields().putAll(buff);

      Assert.assertEquals(revised.getFields().size(), 2);
      Assert.assertEquals(revision.getfields().size(), 3);

      Assert.assertEquals(revision.getfields().size(), 3);

      for (Map.Entry<String, SplitBlob> each : revision.getfields().entrySet()) {
         buff.containsKey(each.getKey());
         buff.containsValue(each.getValue());
      }
   }

   @Test
   public void testToArray() {
      revised.getFields().clear();
      revision.getfields().clear();
      Assert.assertTrue(revised.getFields().isEmpty());
      Assert.assertTrue(revision.getfields().isEmpty());

      Map<String, SplitBlob> buff = new HashMap<String, SplitBlob>();
      buff.put("type#1", newSplitBlob());
      buff.put("type#2", newSplitBlob());
      revised.getFields().putAll(buff);

      buff.put("type#3", newSplitBlob());
      revision.getfields().putAll(buff);

      Assert.assertEquals(revised.getFields().size(), 2);
      Assert.assertEquals(revision.getfields().size(), 3);

      Object[] array = revision.getfields().values().toArray();
      Assert.assertEquals(array.length, 3);

      for (Object each : array) {
         Assert.assertTrue(buff.containsValue(each));
      }
   }

   @Test
   public void testRemove() {
      SplitBlob blob = revised.getFields().get("type#0");
      Assert.assertTrue(revision.getfields().containsValue(blob));
      Assert.assertNull(revision.getfields().remove(blob));
   }
}
