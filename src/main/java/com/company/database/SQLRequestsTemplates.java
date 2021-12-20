package com.company.database;

public enum SQLRequestsTemplates {
    ScoreRepo_InsertRecord("INSERT OR IGNORE into score(chatId, userId, score) values (%d, %d, 0)"),
    ScoreRepo_Increment("UPDATE score SET score = score + 1 WHERE (chatId = %d) and (userId = %d)"),
    ScoreRepo_GetScoreRecords("SELECT userId, score FROM score WHERE chatId = %d ORDER BY score DESC"),

    StatesRepo_InsertRecord("INSERT OR IGNORE into states(state, chatId) values (%d, %d)"),
    StatesRepo_UpdateRecord("UPDATE states SET state = %d WHERE chatId = %d"),
    StatesRepo_GetRecord("SELECT state FROM states WHERE chatId = %d"),

    QuestionIdRepo_InsertRecord("INSERT OR IGNORE into questionIds(questionId, chatId) values (%d, %d)"),
    QuestionIdRepo_UpdateRecord("UPDATE questionIds SET questionId = %d WHERE chatId = %d"),
    QuestionIdRepo_GetRecord("SELECT questionId FROM questionIds WHERE chatId = %d"),

    CountRepo_InsertRecord("INSERT OR IGNORE into %s(count, chatId) values (0, %d)"),
    CountRepo_UpdateRecord("UPDATE %s SET count = count + 1 WHERE chatId = %d"),
    CountRepo_GetRecord("SELECT count FROM %s WHERE chatId = %d");



    public final String value;
    SQLRequestsTemplates(final String value) {
        this.value = value;
    }
}
