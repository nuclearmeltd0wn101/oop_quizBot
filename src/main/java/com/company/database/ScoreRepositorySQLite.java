package com.company.database;

import com.company.quiz.QuizScore;

import java.sql.SQLException;

public class ScoreRepositorySQLite implements IScoreRepository {
    private final IDatabaseCoreSQLite db;

    public ScoreRepositorySQLite(IDatabaseCoreSQLite db) {
        this.db = db;

        db.Save(new String[] { // todo: migrate to external db init
                "CREATE table if NOT EXISTS score (chatId INTEGER,"
                    + "userId INTEGER, score INTEGER, PRIMARY KEY (chatId, userId))"
        });
    }

    public void Increment(long chatId, long userId) {
        db.Save(new String[] {
                String.format(
                        "INSERT OR IGNORE into score(chatId, userId, score) values (%d, %d, 0)",
                        chatId, userId),
                String.format(
                        "UPDATE score SET score = score + 1 WHERE (chatId = %d) and (userId = %d)",
                        chatId, userId)
        });
    }

    public QuizScore[] GetTable(long chatId) {
        var result = db.Get(
                String.format(
                        "SELECT userId, score FROM score WHERE chatId = %d ORDER BY score DESC",
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
