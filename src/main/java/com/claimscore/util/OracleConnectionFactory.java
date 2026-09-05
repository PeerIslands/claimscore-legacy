package com.claimscore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Hands out JDBC connections from the container datasource.
 *
 * @author rmendez
 * $Id: OracleConnectionFactory.java,v 1.11 2013/06/18 15:02:11 rmendez Exp $
 */
public final class OracleConnectionFactory {

    private static final Logger log = Logger.getLogger(OracleConnectionFactory.class);

    private static DataSource dataSource;

    private OracleConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            try {
                InitialContext ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup(Constants.JNDI_DATASOURCE);
            } catch (NamingException e) {
                log.warn("Datasource " + Constants.JNDI_DATASOURCE
                        + " not found, falling back to direct JDBC: " + e.getMessage());
                return getFallbackConnection();
            }
        }
        return dataSource.getConnection();
    }

    // FIXME: hard-coded credentials, see CR-2013-088
    private static Connection getFallbackConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle driver not on classpath: " + e.getMessage());
        }
        return DriverManager.getConnection(Constants.FALLBACK_JDBC_URL,
                Constants.FALLBACK_DB_USER, Constants.FALLBACK_DB_PASSWORD);
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.debug("Error closing ResultSet", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.debug("Error closing Statement", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.debug("Error closing Connection", e);
            }
        }
    }
}
