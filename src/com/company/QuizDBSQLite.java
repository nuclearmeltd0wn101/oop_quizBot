package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Function;

public class QuizDBSQLite implements IQuizDB {
    private static final String initStatesTableQuery
            = "CREATE table if NOT EXISTS states (chatId INTEGER PRIMARY KEY, state INTEGER)";
    private static final String initScoreTableQuery
            = "CREATE table if NOT EXISTS score (chatId INTEGER,"
                + "userId INTEGER, score INTEGER, PRIMARY KEY (chatId, userId))";
    private static final String initQuestionIdTableQuery
            = "CREATE table if NOT EXISTS questionIds (chatId INTEGER PRIMARY KEY, questionId INTEGER)";
    private static final String initWrongAnswersCountTableQuery
            = "CREATE table if NOT EXISTS wrongAnswers (chatId INTEGER PRIMARY KEY, count INTEGER)";
    private static final String initGiveUpRequestsCountTableQuery
            = "CREATE table if NOT EXISTS giveUpRequests (chatId INTEGER PRIMARY KEY, count INTEGER)";
    private static final String initUserNamesTableQuery
            = "CREATE table if NOT EXISTS userNames (userId INTEGER PRIMARY KEY, name TEXT)";
    private static final String initChatsInactiveTableQuery
            = "CREATE table if NOT EXISTS chatsInactive (chatId INTEGER PRIMARY KEY, "
                + "lastActiveTimestampUnix INTEGER, remindAttemptsCount)";

    private static final String scoreInitQuery
            = "insert or ignore into score(chatId, userId, score) values (%d, %d, 0)";
    private static final String scoreIncrementQuery
            = "update score SET score = score + 1 WHERE (chatId = %d) and (userId = %d)";
    private static final String getScoreTableQuery
            = "SELECT userId, score FROM score WHERE chatId = %d ORDER BY score DESC";

    private static final String getStateQuery
            = "SELECT state FROM states WHERE chatId = %d";
    private static final String setStateInitQuery
            = "insert or ignore into states(state, chatId) values (%d, %d)";
    private static final String setStateUpdateQuery
            = "update states SET state = %d WHERE chatId = %d";

    private static final String getQuestionIdQuery
            = "SELECT questionId FROM questionIds WHERE chatId = %d";
    private static final String setQuestionIdInitQuery
            = "insert or ignore into questionIds(questionId, chatId) values (%d, %d)";
    private static final String setQuestionIdUpdateQuery
            = "update questionIds SET questionId = %d WHERE chatId = %d";

    private static final String getWrongAnswersCountQuery
            = "SELECT count FROM wrongAnswers WHERE chatId = %d";
    private static final String setWrongAnswersCountInitQuery
            = "insert or ignore into wrongAnswers(count, chatId) values (0, %d)";
    private static final String setWrongAnswersCountIncrementQuery
            = "update wrongAnswers SET count = count + 1 WHERE chatId = %d";
    private static final String setWrongAnswersCountResetQuery
            = "update wrongAnswers SET count = 0 WHERE chatId = %d";

    private static final String getGiveUpRequestsCountQuery
            = "SELECT count FROM giveUpRequests WHERE chatId = %d";
    private static final String setGiveUpRequestsCountInitQuery
            = "insert or ignore into giveUpRequests(count, chatId) values (0, %d)";
    private static final String setGiveUpRequestsCountIncrementQuery
            = "update giveUpRequests SET count = count + 1 WHERE chatId = %d";
    private static final String setGiveUpRequestsCountResetQuery
            = "update giveUpRequests SET count = 0 WHERE chatId = %d";

    private static final String getUsernameQuery
            = "SELECT name FROM userNames WHERE userId = %d";
    private static final String setUserNameInitQuery
            = "insert or ignore into userNames(name, userId) values ('%s', %d)";
    private static final String setUserNameUpdateQuery
            = "update userNames SET name = '%s' WHERE userId = %d";

    private static final String getInactiveUserQuery = "SELECT chatId FROM chatsInactive WHERE "
            + "(abs(strftime('%%s', 'now') - lastActiveTimestampUnix) >= %d)"
            + " AND (remindAttemptsCount < %d)";
    private static final String registerInactiveChatTimestampQuery = "INSERT OR IGNORE INTO "
            + "chatsInactive(chatId, lastActiveTimestampUnix, remindAttemptsCount) "
            + "VALUES (%d, strftime('%%s', 'now'), 0)";
    private static final String incrementInactiveChatRemindAttemptsQuery = "UPDATE chatsInactive"
            + " SET remindAttemptsCount = remindAttemptsCount + 1 WHERE chatId = %s";
    private static final String updateInactiveChatTimestampQuery = "UPDATE chatsInactive"
            + " SET lastActiveTimestampUnix = strftime('%%s', 'now') WHERE chatId = %d";
    private static final String cleanUpInactiveChatsTableQuery = "DELETE FROM chatsInactive"
            + " WHERE remindAttemptsCount >= %d";

    private final Connection connection;

    private int m_maxRemindAttempts = 5;
    private long m_remindDelaySeconds = 3 * 24 * 60 * 60;

    public QuizDBSQLite(String dbFilePath)
    {
        Connection connection = null;
        dbFilePath = dbFilePath == null
                ? ":memory:"
                : dbFilePath;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        this.connection = connection;
        if (connection == null)
            throw new IllegalStateException();

        executeUpdates(new String[] {
                initStatesTableQuery,
                initScoreTableQuery,
                initQuestionIdTableQuery,
                initWrongAnswersCountTableQuery,
                initGiveUpRequestsCountTableQuery,
                initUserNamesTableQuery,
                initChatsInactiveTableQuery
        });
    }

    private void executeUpdates(String[] updates) {
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            for (var update : updates)
                statement.executeUpdate(update);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private <T> ArrayList<T> queryMap(Function<ResultSet, T> func, String query)
    {
        var result = new ArrayList<T>();
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(query);
            while (response.next())
            {
                var item = func.apply(response);
                if (item == null)
                    throw new SQLException("unable to process " + query);
                result.add(item);
            }
            return result;
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public long getLastLong(String query, String columnLabel, long defaultValue) {
        var data = queryMap(response -> {
            try { // ResultSet.get{typeHere} causes repetitions anyway
                return response.getLong(columnLabel);
            } catch (SQLException e) { return null; }
        }, query);

        var result = defaultValue;
        if (data == null)
            return result;

        for (var item : data)
            result = item;

        return result;
    }

    public int getLastInt(String query, String columnLabel, int defaultValue) {
        var data = queryMap(response -> {
            try { // ResultSet.get{typeHere} causes repetitions anyway
                return response.getInt(columnLabel);
            } catch (SQLException e) { return null; }
        }, query);

        var result = defaultValue;
        if (data == null)
            return result;

        for (var item : data)
            result = item;

        return result;
    }

    public String getLastString(String query, String columnLabel, String defaultValue) {
        var data = queryMap(response -> {
            try { // ResultSet.get{typeHere} causes repetitions anyway
                return response.getString(columnLabel);
            } catch (SQLException e) { return null; }
        }, query);

        var result = defaultValue;
        if (data == null)
            return result;

        for (var item : data)
            result = item;

        return result;
    }


    public void scoreIncrement(long chatId, long userId) {
        executeUpdates(new String[] {
            String.format(scoreInitQuery, chatId, userId),
            String.format(scoreIncrementQuery, chatId, userId)
        });
    }

    public QuizScore[] getScoreTable(long chatId) {
        var result = queryMap(response -> {
            try {
                var userId = response.getInt("userId");
                var score = response.getInt("score");
                return new QuizScore(chatId, userId, score);
            } catch (SQLException e) { return null; }
        }, String.format(getScoreTableQuery, chatId));

        if (result == null || result.size() == 0)
            return null;

        var arrayResult = new QuizScore[result.size()];
        result.toArray(arrayResult);
        return arrayResult;
    }

    public long getState(long chatId) {
        return getLastLong(String.format(getStateQuery, chatId),
                "state", 0L);
    }

    public void setState(long chatId, long state) {
        executeUpdates(new String[]{
                String.format(setStateInitQuery, state, chatId),
                String.format(setStateUpdateQuery, state, chatId)
        });
    }

    public int getQuestionId(long chatId) {
        return getLastInt(String.format(getQuestionIdQuery, chatId),
                "questionId", 0);
    }

    public void setQuestionId(long chatId, int questionId) {
        executeUpdates(new String[]{
                String.format(setQuestionIdInitQuery, questionId, chatId),
                String.format(setQuestionIdUpdateQuery, questionId, chatId)
        });
    }

    public int getWrongAnswersCount(long chatId) {
        return getLastInt(String.format(getWrongAnswersCountQuery, chatId),
                "count", 0);
    }

    public void wrongAnswersCountIncrement(long chatId) {
        executeUpdates(new String[]{
                String.format(setWrongAnswersCountInitQuery, chatId),
                String.format(setWrongAnswersCountIncrementQuery, chatId)
        });
    }

    public void wrongAnswersCountReset(long chatId) {
        executeUpdates(new String[]{
                String.format(setWrongAnswersCountInitQuery, chatId),
                String.format(setWrongAnswersCountResetQuery, chatId)
        });
    }

    public int getGiveUpRequestsCount(long chatId) {
        return getLastInt(String.format(getGiveUpRequestsCountQuery, chatId),
                "count", 0);
    }

    public void giveUpRequestsCountIncrement(long chatId) {
        executeUpdates(new String[]{
                String.format(setGiveUpRequestsCountInitQuery, chatId),
                String.format(setGiveUpRequestsCountIncrementQuery, chatId)
        });
    }

    public void giveUpRequestsCountReset(long chatId) {
        executeUpdates(new String[]{
                String.format(setGiveUpRequestsCountInitQuery, chatId),
                String.format(setGiveUpRequestsCountResetQuery, chatId)
        });
    }

    public String getUserName(long userId) {
        return getLastString(String.format(getUsernameQuery, userId),
                "name", String.format("ID %d", userId));
    }

    public void setUserName(long userId, String name) {
        var escapedName = name.replace("'", "''");
        executeUpdates(new String[] {
                String.format(setUserNameInitQuery, escapedName, userId),
                String.format(setUserNameUpdateQuery, escapedName, userId)
        });
    }

    public void setRemindPolicy(long remindDelaySeconds, int maxRemindAttempts) {
        m_remindDelaySeconds = remindDelaySeconds;
        m_maxRemindAttempts = maxRemindAttempts;
    }

    public InactiveChatInfo getInactiveChat() {
        var resultBoxed = queryMap(response -> {
                    try {
                        return new InactiveChatInfo(
                                response.getLong("chatId"),
                                response.next()
                        );
                    } catch (SQLException e) { return null; }
                }, String.format(getInactiveUserQuery, m_remindDelaySeconds, m_maxRemindAttempts));

        if (resultBoxed == null || resultBoxed.size() < 1)
            return null;

        executeUpdates(new String[] {
                // increment count assuming this method is called for remind formation
                String.format(incrementInactiveChatRemindAttemptsQuery, resultBoxed.get(0).chatId),
                // and also update timestamp in order to reset delay
                String.format(updateInactiveChatTimestampQuery, resultBoxed.get(0).chatId),
                // and clean up chat records with exceeded remind count limit
                String.format(cleanUpInactiveChatsTableQuery, m_maxRemindAttempts)
        });
        return resultBoxed.get(0);
    }

    public void updateChatLastActiveTimestamp(long chatId) {
        executeUpdates(new String[] { // increment count assuming this method is called for remind formation
                String.format(registerInactiveChatTimestampQuery, chatId),
                String.format(updateInactiveChatTimestampQuery, chatId)
        });
    }
}
