package com.company.database;

import com.google.inject.Inject;

public class StatesRepositorySQLite implements IStatesRepository {
    private final IDatabaseCoreSQLite db;

    @Inject
    public StatesRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;
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
