package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Function;

public class DatabaseCoreSQLite implements IDatabaseCoreSQLite{

    private final Connection connection;

    public DatabaseCoreSQLite(String dbFilePath)
    {
        Connection connection = null;
        dbFilePath = dbFilePath == null
                ? ":memory:"
                : dbFilePath;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        this.connection = connection;
        if (connection == null)
            throw new IllegalStateException();
    }

    public void Save(String[] queries) {
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            for (var query : queries)
                statement.executeUpdate(query);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public <T> ArrayList<T> Get(String query, Function<ResultSet, T> processor) {
        var result = new ArrayList<T>();
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(query);
            while (response.next())
            {
                var item = processor.apply(response);
                if (item == null)
                    throw new SQLException("unable to process " + query);
                result.add(item);
            }
            return result;
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public long Get(String query, String columnLabel, long defaultValue) {
        var data = this.Get(query, response -> {
            try { // ResultSet.get{typeHere} causes repetitions anyway
                return response.getLong(columnLabel);
            } catch (SQLException e) { return null; }
        });

        var result = defaultValue;
        if (data == null)
            return result;

        for (var item : data)
            result = item;

        return result;
    }

    public String Get(String query, String columnLabel, String defaultValue) {
        var data = this.Get(query, response -> {
            try { // ResultSet.get{typeHere} causes repetitions anyway
                return response.getString(columnLabel);
            } catch (SQLException e) { return null; }
        });

        var result = defaultValue;
        if (data == null)
            return result;

        for (var item : data)
            result = item;

        return result;
    }
}
