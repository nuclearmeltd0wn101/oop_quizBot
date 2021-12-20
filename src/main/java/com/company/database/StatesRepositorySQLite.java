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
                String.format(SQLRequestsTemplates.StatesRepo_GetRecord.value, chatId),
                SQLRequestsTemplates.StatesRepo_GetColumnLabel.value, 0L);
    }

    public void Set(long chatId, long state) {
        db.Save(new String[]{
                String.format(SQLRequestsTemplates.StatesRepo_InsertRecord.value,
                        state, chatId),
                String.format(SQLRequestsTemplates.StatesRepo_UpdateRecord.value,
                        state, chatId)
        });
    }
}
