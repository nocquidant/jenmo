package org.jenmo.core.persistence;

import javax.persistence.EntityManagerFactory;

public class ScopedPersistenceManager extends PersistenceManager {
   
   protected ScopedPersistenceManager() {
   }
   
   @Override
   protected EntityManagerFactory createEntityManagerFactory() {
     return new ScopedEntityManagerFactory(super.createEntityManagerFactory());
   }
 }
