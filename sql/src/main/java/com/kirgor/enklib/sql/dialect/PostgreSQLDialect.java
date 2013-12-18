package com.kirgor.enklib.sql.dialect;

import com.kirgor.enklib.common.NamingUtils;

/**
 * PostgreSQL dialect.
 */
public class PostgreSQLDialect implements Dialect {
    /**
     * {@inheritDoc}
     * <p/>
     * Returns something like "SELECT * FROM get_orders(?,?,?)".
     */
    @Override
    public String buildStoredProcedureQuery(String name, int paramsCount) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(name);
        sb.append("(");
        for (int i = 0; i < paramsCount; i++) {
            sb.append("?,");
        }
        if (paramsCount > 0) {
            sb.setLength(sb.length() - 1);
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Converts PostgreSQL conventional names to Java field names, i.e. "last_name" to "lastName".
     */
    public String fromDatabaseName(String name) {
        return NamingUtils.snakeToLowerCamel(name);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Converts to PostgreSQL conventional names, i.e. "lastName" to "last_name".
     */
    public String toDatabaseName(String name) {
        return NamingUtils.camelToLowerSnake(name);
    }
}
