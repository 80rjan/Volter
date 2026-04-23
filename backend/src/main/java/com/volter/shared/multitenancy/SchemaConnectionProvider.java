package com.volter.shared.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class SchemaConnectionProvider
        implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    public SchemaConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection conn = dataSource.getConnection();
        // "shop_skopje, public" means: look in shop_skopje first, fall back to public
        conn.createStatement()
                .execute("SET search_path TO " + tenantIdentifier + ", public");
        return conn;
    }

    @Override
    public void releaseConnection(String tenantIdentifier,
                                  Connection connection) throws SQLException {
        // Always reset before returning to pool
        connection.createStatement().execute("SET search_path TO public");
        connection.close();
    }

    @Override public boolean supportsAggressiveRelease() { return false; }
    @Override public boolean isUnwrappableAs(Class<?> unwrapType) { return false; }
    @Override public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException();
    }
}
