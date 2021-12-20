package com.company.database;

import com.company.quiz.QuizScore;
import com.google.inject.Inject;

import java.sql.SQLException;

public class ScoreRepositorySQLite implements IScoreRepository {
    private final IDatabaseCoreSQLite db;

    @Inject
    public ScoreRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;
    }

    public void Increment(long chatId, long userId) {
        db.Save(new String[] {
                String.format(
                        SQLRequestsTemplates.ScoreRepo_InsertRecord.value,
                        chatId, userId),
                String.format(
                        SQLRequestsTemplates.ScoreRepo_Increment.value,
                        chatId, userId)
        });
    }

    public QuizScore[] GetTable(long chatId) {
        var result = db.Get(
                String.format(
                        SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                        chatId),
                response -> {
                    try {
                        var userId = response.getInt("userId");
                        var score = response.getInt("score");
                        return new QuizScore(chatId, userId, score);
                    } catch (SQLException e) { return null; }
                });

        if (result == null || result.size() == 0)
            return null;

        var arrayResult = new QuizScore[result.size()];
        result.toArray(arrayResult);
        return arrayResult;
    }
}
