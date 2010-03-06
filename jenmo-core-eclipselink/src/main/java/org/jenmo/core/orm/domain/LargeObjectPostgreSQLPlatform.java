package org.jenmo.core.orm.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;

public class LargeObjectPostgreSQLPlatform extends PostgreSQLPlatform {
   private static final long serialVersionUID = 8007091077580108773L;

   @Override
   public Object getObjectFromResultSet(ResultSet resultSet, int columnNumber, int type,
         AbstractSession session) throws SQLException {
      if (type == Types.BIGINT && columnNumber == 4) {
         Connection conn = resultSet.getStatement().getConnection();
         boolean previous = conn.getAutoCommit();
         conn.setAutoCommit(false);
         
         Object out = resultSet.getBytes(4);
         
         conn.setAutoCommit(previous);
         return out;
      }
      return super.getObjectFromResultSet(resultSet, columnNumber, type, session);
   }
}
