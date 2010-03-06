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
package org.jenmo.core.multiarray;

import org.jenmo.common.multiarray.ICloseableAccessor;
import org.jenmo.core.domain.SplitBlob;
import org.jenmo.core.listener.IListener;
import org.jenmo.core.listener.SplitBlobEvent;


/**
 * Interface for {@link SplitBlob} data access. One can set a listener on this accessor.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface IBlobPartAccessor extends ICloseableAccessor {
   /**
    * Sets listener to be notified for part rollings during access.
    */
   void setPartListener(IListener<SplitBlobEvent> l);

   /**
    * Removes the listener associated with this accessor.
    */
   public void removePartListener();
}
