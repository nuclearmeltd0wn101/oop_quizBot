package com.company.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.function.Function;

public interface IDatabaseCoreSQLite {
    void Save(String[] queries);

    <T> ArrayList<T> Get(String query, Function<ResultSet, T> processor);
    long Get(String query, String columnLabel, long defaultValue);
    String Get(String query, String columnLabel, String defaultValue);
}
