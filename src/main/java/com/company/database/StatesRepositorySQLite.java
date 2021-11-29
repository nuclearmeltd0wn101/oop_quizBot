package com.company.database;

public class StatesRepositorySQLite implements IStatesRepository {
    private final IDatabaseCoreSQLite db;

    public StatesRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;

        db.Save(new String[] { // todo: migrate to external db init
                "CREATE table if NOT EXISTS states (chatId INTEGER PRIMARY KEY, state INTEGER)"
        });
    }

    public long Get(long chatId) {
        return db.Get(
                String.format("SELECT state FROM states WHERE chatId = %d", chatId),
                "state", 0L);
    }

    public void Set(long chatId, long state) {
        db.Save(new String[]{
                String.format("INSERT OR IGNORE into states(state, chatId) values (%d, %d)",
                        state, chatId),
                String.format("UPDATE states SET state = %d WHERE chatId = %d",
                        state, chatId)
        });
    }
}
