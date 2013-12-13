package com.kirgor.tools.sql.dialect;

/**
 * It is needed to consider database-specific query construction, naming conventions, etc.
 */
public interface Dialect {
    /**
     * Builds SQL query for executing stored procedure with given name and parameters count.
     *
     * @param name        Name of stored procedure or function.
     * @param paramsCount Count of parameters, which is used to set correct amount of "?" signs.
     * @return Builded SQL qury string.
     */
    String buildStoredProcedureQuery(String name, int paramsCount);

    /**
     * Converts specified database convention name to Java convention name.
     *
     * @param name Database convention name.
     * @return Java convention name.
     */
    String fromDatabaseName(String name);

    /**
     * Converts specified Java convention name to database convention name.
     *
     * @param name Java convention name.
     * @return Database convention name.
     */
    String toDatabaseName(String name);
}
