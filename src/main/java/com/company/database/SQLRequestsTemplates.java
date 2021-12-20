package com.company.database;

public enum SQLRequestsTemplates {

    ScoreRepo_GetScoreRecords("SELECT userId, score FROM score WHERE chatId = %d ORDER BY score DESC"),
    ScoreRepo_InsertRecord("INSERT OR IGNORE into score(chatId, userId, score) values (%d, %d, 0)"),
    ScoreRepo_Increment("UPDATE score SET score = score + 1 WHERE (chatId = %d) and (userId = %d)"),

    StatesRepo_GetRecord("SELECT state FROM states WHERE chatId = %d"),
    StatesRepo_GetColumnLabel("state"),
    StatesRepo_InsertRecord("INSERT OR IGNORE into states(state, chatId) values (%d, %d)"),
    StatesRepo_UpdateRecord("UPDATE states SET state = %d WHERE chatId = %d"),

    QuestionIdRepo_GetRecord("SELECT questionId FROM questionIds WHERE chatId = %d"),
    QuestionIdRepo_GetColumnLabel("questionId"),
    QuestionIdRepo_InsertRecord("INSERT OR IGNORE into questionIds(questionId, chatId) values (%d, %d)"),
    QuestionIdRepo_UpdateRecord("UPDATE questionIds SET questionId = %d WHERE chatId = %d"),

    CountRepo_GetRecord("SELECT count FROM %s WHERE chatId = %d"),
    CountRepo_GetColumnLabel("count"),
    CountRepo_InsertRecord("INSERT OR IGNORE into %s(count, chatId) values (0, %d)"),
    CountRepo_Increment("UPDATE %s SET count = count + 1 WHERE chatId = %d"),
    CountRepo_Reset("UPDATE %s SET count = 0 WHERE chatId = %d"),

    UserNamesRepo_GetRecord("SELECT name FROM userNames WHERE userId = %d"),
    UserNamesRepo_GetColumnLabel("name"),
    UserNamesRepo_CreateRecord("INSERT OR IGNORE into userNames(name, userId) values ('%s', %d)"),
    UserNamesRepo_UpdateRecord("UPDATE userNames SET name = '%s' WHERE userId = %d"),

    RemindRepo_GetRecord("SELECT chatId FROM chatsInactive WHERE "
                                 + "(abs(strftime('%%s', 'now') - lastActiveTimestampUnix) >= %d)"
                                 + " AND (remindAttemptsCount < %d)"),
    RemindRepo_CreateRecord("INSERT OR IGNORE INTO "
            + "chatsInactive(chatId, lastActiveTimestampUnix, remindAttemptsCount) "
            + "VALUES (%d, strftime('%%s', 'now'), 0)"),
    RemindRepo_UpdateRecordTimestamp("UPDATE chatsInactive"
            + " SET lastActiveTimestampUnix = strftime('%%s', 'now') WHERE chatId = %d"),
    RemindRepo_IncrementAttemptsRecord("UPDATE chatsInactive"
            + " SET remindAttemptsCount = remindAttemptsCount + 1 WHERE chatId = %s"),
    RemindRepo_CleanUpRecords("DELETE FROM chatsInactive WHERE remindAttemptsCount >= %d");



    public final String value;
    SQLRequestsTemplates(final String value) {
        this.value = value;
    }
}
