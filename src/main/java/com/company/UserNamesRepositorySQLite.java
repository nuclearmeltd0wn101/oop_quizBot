package com.company;

public class UserNamesRepositorySQLite implements IUserNamesRepository {
    private final IDatabaseCoreSQLite db;

    public UserNamesRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;

        db.Save(new String[] {
                "CREATE table IF NOT EXISTS userNames (userId INTEGER PRIMARY KEY, name TEXT)"
        });
    }

    public String Get(long userId) {
        return db.Get(String.format("SELECT name FROM userNames WHERE userId = %d", userId),
                "name", String.format("ID %d", userId));
    }

    public void Set(long userId, String name) {
        var escapedName = name.replace("'", "''");
        db.Save(new String[] {
                String.format("INSERT OR IGNORE into userNames(name, userId) values ('%s', %d)",
                        escapedName, userId),
                String.format("UPDATE userNames SET name = '%s' WHERE userId = %d",
                        escapedName, userId)
        });
    }
}
