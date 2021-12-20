package com.company.database;

import com.google.inject.Inject;

public class QuestionIdRepositorySQLite implements IQuestionIdRepository {
    private final IDatabaseCoreSQLite db;

    @Inject
    public QuestionIdRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;
    }

    public int Get(long chatId) {
        return (int)db.Get(String.format(SQLRequestsTemplates.QuestionIdRepo_GetRecord.value, chatId),
                "questionId", 0);
    }

    public void Set(long chatId, int questionId) {
        db.Save(new String[]{
                String.format(SQLRequestsTemplates.QuestionIdRepo_InsertRecord.value,
                        questionId, chatId),
                String.format(SQLRequestsTemplates.QuestionIdRepo_UpdateRecord.value,
                        questionId, chatId)
        });
    }
}
