package org.jenmo.core.domain;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.IProcedure0;

public interface IPopulator extends IProcedure0 {
   EntityManager getEm();
}
