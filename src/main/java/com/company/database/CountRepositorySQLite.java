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
        return (int)db.Get(String.format("SELECT count FROM %s WHERE chatId = %d",
                        countName, chatId),
                "count", 0);
    }

    public void Increment(long chatId) {
        db.Save(new String[]{
                String.format("INSERT OR IGNORE into %s(count, chatId) values (0, %d)",
                        countName, chatId),
                String.format("UPDATE %s SET count = count + 1 WHERE chatId = %d",
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
