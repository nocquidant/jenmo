package org.jenmo.core.orm.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.openjpa.jdbc.sql.PostgresDictionary;

public class LargeObjectPostgresDictionary extends PostgresDictionary {
   @Override
   public byte[] getBytes(ResultSet rs, int column) throws SQLException {
      Connection conn = rs.getStatement().getConnection();
      boolean previous = conn.getAutoCommit();
      conn.setAutoCommit(false);
      byte[] out = super.getBytes(rs, column);
      conn.setAutoCommit(previous);
      return out;
   }
}
