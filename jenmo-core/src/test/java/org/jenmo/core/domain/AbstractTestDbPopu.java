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

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.testutil.DbUseCase;
import org.jenmo.core.testutil.MyTimer;
import org.jenmo.core.testutil.DbUseCase.DbUseCaseException;

public abstract class AbstractTestDbPopu extends AbstractTestDb {
   protected static void populateData(final IPopulator populator, boolean closeEm)
         throws DbUseCaseException {
      DbUseCase dbUc = new DbUseCase() {
         @Override
         protected void reallyExecute(EntityManager em) throws DbUseCaseException {
            try {
               // txBegin before call to reallyExecute in DbUseCase
               populator.execute();
               // txCommit after call to reallyExecute in DbUseCase
            } catch (ProcedureException e) {
               throw new DbUseCaseException(e);
            }
         }
      };
      dbUc.execute(populator.getEm(), closeEm);
   }

   protected static void runPopulator(IPopulator populator) throws Exception {
      if (populator != null) {
         MyTimer timer = new MyTimer();
         populateData(populator, false);
         timer.end("End Populating data");
      }
   }
}
