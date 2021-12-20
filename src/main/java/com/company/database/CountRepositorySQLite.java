package com.company.database;

public class CountRepositorySQLite implements ICountRepository {
    private final IDatabaseCoreSQLite db;
    private final String countName;

    public CountRepositorySQLite(IDatabaseCoreSQLite db, String countName) {
        if (countName == null)
            throw new IllegalArgumentException();

        this.countName = countName;
        this.db = db;
    }

    public int Get(long chatId) {
        return (int)db.Get(String.format(
                SQLRequestsTemplates.CountRepo_GetRecord.value,
                countName, chatId),
                "count", 0);
    }

    public void Increment(long chatId) {
        db.Save(new String[]{
                String.format(
                        SQLRequestsTemplates.CountRepo_InsertRecord.value,
                        countName, chatId),
                String.format(
                        SQLRequestsTemplates.CountRepo_UpdateRecord.value,
                        countName, chatId)
        });
    }

    public void Reset(long chatId) {
        db.Save(new String[]{
                String.format("UPDATE %s SET count = 0 WHERE chatId = %d",
                        countName, chatId)
        });
    }
}
