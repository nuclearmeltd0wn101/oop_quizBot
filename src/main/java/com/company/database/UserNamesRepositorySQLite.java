package com.company.database;

public class UserNamesRepositorySQLite implements IUserNamesRepository {
    private final IDatabaseCoreSQLite db;

    public UserNamesRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;
    }

    public String Get(long userId) {
        return db.Get(String.format(
                SQLRequestsTemplates.UserNamesRepo_GetRecord.value, userId),
                SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value,
                String.format("ID %d", userId));
    }

    public void Set(long userId, String name) {
        var escapedName = name.replace("'", "''");
        db.Save(new String[] {
                String.format(SQLRequestsTemplates.UserNamesRepo_CreateRecord.value,
                        escapedName, userId),
                String.format(SQLRequestsTemplates.UserNamesRepo_UpdateRecord.value,
                        escapedName, userId)
        });
    }
}
