package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuizDBSQLite implements IQuizDB {
    private static final String initStatesTableQuery
            = "create table if not exists states (chatId integer primary key, state integer)";
    private static final String initScoreTableQuery
            = "create table if not exists score (chatId integer," +
            "userId integer, score integer, primary key (chatId, userId))";
    private static final String initQuestionIdTableQuery
            = "create table if not exists questionIds (chatId integer primary key, questionId integer)";
    private static final String initWrongAnswersCountTableQuery
            = "create table if not exists wrongAnswers (chatId integer primary key, count integer)";
    private static final String initGiveUpRequestsCountTableQuery
            = "create table if not exists giveUpRequests (chatId integer primary key, count integer)";
    private static final String initUserNamesTableQuery
            = "create table if not exists userNames (userId integer primary key, name text)";

    private static final String scoreInitQuery
            = "insert or ignore into score(chatId, userId, score) values (%d, %d, 0)";
    private static final String scoreIncrementQuery
            = "update score set score = score + 1 where (chatId = %d) and (userId = %d)";
    private static final String getScoreTableQuery
            = "select userId, score from score where chatId = %d order by score desc";

    private static final String getStateQuery
            = "select state from states where chatId = %d";
    private static final String setStateInitQuery
            = "insert or ignore into states(state, chatId) values (%d, %d)";
    private static final String setStateUpdateQuery
            = "update states set state = %d where chatId = %d";

    private static final String getQuestionIdQuery
            = "select questionId from questionIds where chatId = %d";
    private static final String setQuestionIdInitQuery
            = "insert or ignore into questionIds(questionId, chatId) values (%d, %d)";
    private static final String setQuestionIdUpdateQuery
            = "update questionIds set questionId = %d where chatId = %d";

    private static final String getWrongAnswersCountQuery
            = "select count from wrongAnswers where chatId = %d";
    private static final String setWrongAnswersCountInitQuery
            = "insert or ignore into wrongAnswers(count, chatId) values (0, %d)";
    private static final String setWrongAnswersCountIncrementQuery
            = "update wrongAnswers set count = count + 1 where chatId = %d";
    private static final String setWrongAnswersCountResetQuery
            = "update wrongAnswers set count = 0 where chatId = %d";

    private static final String getGiveUpRequestsCountQuery
            = "select count from giveUpRequests where chatId = %d";
    private static final String setGiveUpRequestsCountInitQuery
            = "insert or ignore into giveUpRequests(count, chatId) values (0, %d)";
    private static final String setGiveUpRequestsCountIncrementQuery
            = "update giveUpRequests set count = count + 1 where chatId = %d";
    private static final String setGiveUpRequestsCountResetQuery
            = "update giveUpRequests set count = 0 where chatId = %d";

    private static final String getUsernameQuery
            = "select name from userNames where userId = %d";
    private static final String setUserNameInitQuery
            = "insert or ignore into userNames(name, userId) values ('%s', %d)";
    private static final String setUserNameUpdateQuery
            = "update userNames set name = '%s' where userId = %d";

    private final Connection connection;

    public QuizDBSQLite(String dbFilePath)
    {
        Connection connection = null;
        dbFilePath = dbFilePath == null
                ? ":memory:"
                : dbFilePath;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        } catch (SQLException e) {
            System.err.println(e);
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
                initUserNamesTableQuery
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

    public void scoreIncrement(long chatId, long userId) {
        executeUpdates(new String[] {
            String.format(scoreInitQuery, chatId, userId),
            String.format(scoreIncrementQuery, chatId, userId)
        });
    }

    public QuizScore[] getScoreTable(long chatId) {
        var result = new ArrayList<QuizScore>();
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(
                    String.format(getScoreTableQuery, chatId));
            while (response.next())
            {
                var userId = response.getInt("userId");
                var score = response.getInt("score");
                result.add(new QuizScore(chatId, userId, score));
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        if (result.size() == 0)
            return null;

        var arrayResult = new QuizScore[result.size()];
        result.toArray(arrayResult);
        return arrayResult;
    }

    public long getState(long chatId) {
        var result = 0L;
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(
                    String.format(getStateQuery, chatId));
            while (response.next()) {
                result = response.getInt("state");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public void setState(long chatId, long state) {
        executeUpdates(new String[]{
                String.format(setStateInitQuery, state, chatId),
                String.format(setStateUpdateQuery, state, chatId)
        });
    }

    public int getQuestionId(long chatId) {
        var result = 0;
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(
                    String.format(getQuestionIdQuery, chatId));
            while (response.next()) {
                result = response.getInt("questionId");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public void setQuestionId(long chatId, int questionId) {
        executeUpdates(new String[]{
                String.format(setQuestionIdInitQuery, questionId, chatId),
                String.format(setQuestionIdUpdateQuery, questionId, chatId)
        });
    }

    public int getWrongAnswersCount(long chatId) {
        var result = 0;
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(
                    String.format(getWrongAnswersCountQuery, chatId));
            while (response.next()) {
                result = response.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
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
        var result = 0;
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(
                    String.format(getGiveUpRequestsCountQuery, chatId));
            while (response.next()) {
                result = response.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
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
        var result = String.format("ID %d", userId);
        try {
            var statement = connection.createStatement();
            statement.setQueryTimeout(30);
            var response = statement.executeQuery(
                    String.format(getUsernameQuery, userId));
            while (response.next()) {
                result = response.getString("name");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public void setUserName(long userId, String name) {
        var escapedName = name.replace("'", "\\'");
        executeUpdates(new String[] {
                String.format(setUserNameInitQuery, escapedName, userId),
                String.format(setUserNameUpdateQuery, escapedName, userId)
        });
    }
}
