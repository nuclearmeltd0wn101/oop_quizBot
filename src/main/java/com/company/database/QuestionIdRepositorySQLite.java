package com.company.database;

public class QuestionIdRepositorySQLite implements IQuestionIdRepository {
    private final IDatabaseCoreSQLite db;

    public QuestionIdRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;
    }

    public int Get(long chatId) {
        return (int)db.Get(String.format("SELECT questionId FROM questionIds WHERE chatId = %d", chatId),
                "questionId", 0);
    }

    public void Set(long chatId, int questionId) {
        db.Save(new String[]{
                String.format("INSERT OR IGNORE into questionIds(questionId, chatId) values (%d, %d)",
                        questionId, chatId),
                String.format("UPDATE questionIds SET questionId = %d WHERE chatId = %d",
                        questionId, chatId)
        });
    }
}
