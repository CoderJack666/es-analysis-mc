package com.chachazhan.analyzer.smart.util;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author jack
 * @date 29/12/2017
 * @time 13:12
 */
public class JdbcUtils {

  private static final Logger logger = ESLoggerFactory.getLogger(JdbcUtils.class);

  private JdbcUtils() {}

  public static void safeCloseResultSet(ResultSet resultSet) {
    try {
      if (resultSet != null && !resultSet.isClosed()) {
        resultSet.close();
      }
    } catch (SQLException e) {
      logger.error("关闭ResultSet失败!", e);
    }
  }

  public static void safeCloseStatement(Statement statement) {
    try {
      if (statement != null && !statement.isClosed()) {
        statement.close();
      }
    } catch (SQLException e) {
      logger.error("关闭Statement失败!", e);
    }
  }

  public static void safeCloseConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      logger.error("关闭Connection失败!", e);
    }
  }

}
