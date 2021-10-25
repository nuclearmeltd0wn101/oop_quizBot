package com.company;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QuizDBSQLiteTest {

    @Test
    void getScoreTable_NoChatIdRecord_returnsNull() {
        var db = new QuizDBSQLite(null);
        Assertions.assertNull(db.getScoreTable(312));
    }

    @Test
    void getScoreTable_sortedByScoreDescending() {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.scoreIncrement(123, 456);
        for (var i = 0; i < 15; i++)
            db.scoreIncrement(123, 789);
        for (var i = 0; i < 512; i++)
            db.scoreIncrement(123, 321);

        var response = db.getScoreTable(123);

        for (var i = 0; i < response.length - 1; i++)
            Assertions.assertTrue((response[i + 1]).score <= (response[i]).score);
    }

    @Test
    void scoreIncrement_keepsMultipleScorePerChat() {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.scoreIncrement(123, 456);
        for (var i = 0; i < 15; i++)
            db.scoreIncrement(123, 789);

        var response = db.getScoreTable(123);

        Assertions.assertNotNull(response);

        boolean firstFoundAndOk = false, secondFoundAndOk = false;
        for (var score : response) {
            if ((score.chatId == 123) && (score.userId == 456) && (score.score == 192))
                firstFoundAndOk = true;

            if ((score.chatId == 123) && (score.userId == 789) && (score.score == 15))
                secondFoundAndOk = true;
        }

        Assertions.assertTrue(firstFoundAndOk);
        Assertions.assertTrue(secondFoundAndOk);
    }

    @Test
    void scoreIncrement_keepsMultipleChatsPerUser() {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.scoreIncrement(123, 789);
        for (var i = 0; i < 15; i++)
            db.scoreIncrement(456, 789);

        var response1 = db.getScoreTable(123);
        var response2 = db.getScoreTable(456);

        Assertions.assertNotNull(response1);
        Assertions.assertNotNull(response2);

        boolean firstFoundAndOk = false, secondFoundAndOk = false;
        for (var score : response1) {
            if ((score.chatId == 123) && (score.userId == 789) && (score.score == 192))
                firstFoundAndOk = true;
        }
        for (var score : response2) {
            if ((score.chatId == 456) && (score.userId == 789) && (score.score == 15))
                secondFoundAndOk = true;
        }

        Assertions.assertTrue(firstFoundAndOk);
        Assertions.assertTrue(secondFoundAndOk);
    }

    @Test
    void getState_noStateDefined_returnsZero() {
        var db = new QuizDBSQLite(null);
        Assertions.assertEquals(0, db.getState(123569));
    }

    @Test
    void setState_keepsState() {
        var db = new QuizDBSQLite(null);

        db.setState(159951, 123);

        Assertions.assertEquals(123, db.getState(159951));
    }

    @Test
    void setState_keepsMultipleRecords() {
        var db = new QuizDBSQLite(null);

        db.setState(159951, 12345);
        db.setState(132, 551);

        Assertions.assertEquals(12345, db.getState(159951));
        Assertions.assertEquals(551, db.getState(132));
    }

    @Test
    void getQuestionId_noQuestionIdDefined_returnsZero() {
        var db = new QuizDBSQLite(null);
        Assertions.assertEquals(0, db.getQuestionId(123569));
    }

    @Test
    void setQuestionId_keepsState() {
        var db = new QuizDBSQLite(null);

        db.setState(159951, 123);

        Assertions.assertEquals(123, db.getState(159951));
    }

    @Test
    void setQuestionId_keepsMultipleRecords() {
        var db = new QuizDBSQLite(null);

        db.setQuestionId(159951, 12345);
        db.setQuestionId(132, 551);

        Assertions.assertEquals(12345, db.getQuestionId(159951));
        Assertions.assertEquals(551, db.getQuestionId(132));
    }
    @Test
    void getWrongAnswers_noWrongAnswers()
    {
        var db = new QuizDBSQLite(null);
        Assertions.assertEquals(0, db.getWrongAnswersCount(123569));
    }
    @Test
    void getWrongAnswersIncrement_keepsMultipleChat()
    {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.wrongAnswersCountIncrement(123);
        for (var i = 0; i < 15; i++)
            db.wrongAnswersCountIncrement(456);

        var response1 = db.getWrongAnswersCount(123);
        var response2 = db.getWrongAnswersCount(456);

        Assertions.assertTrue(response1 != 0);
        Assertions.assertTrue(response2 != 0);

        boolean firstFoundAndOk = false, secondFoundAndOk = false;

        if (response1 == 192)
            firstFoundAndOk = true;
        if (response2 == 15)
            secondFoundAndOk = true;

        Assertions.assertTrue(firstFoundAndOk);
        Assertions.assertTrue(secondFoundAndOk);
    }
    @Test
    void wrongAnswerReset_TestWork()
    {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.wrongAnswersCountIncrement(123);
        var before=db.getWrongAnswersCount(123);
        db.wrongAnswersCountReset(123);
        var after=db.getWrongAnswersCount(123);
        Assertions.assertNotEquals(before, after);
        Assertions.assertEquals(0, after);

    }
    @Test
    void getGiveUpCount_noGiveUp()
    {
        var db = new QuizDBSQLite(null);
        Assertions.assertEquals(0, db.getGiveUpRequestsCount(123569));
    }
    @Test
    void getGiveUpIncrement_keepsMultipleChat()
    {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.giveUpRequestsCountIncrement(123);
        for (var i = 0; i < 15; i++)
            db.giveUpRequestsCountIncrement(456);

        var response1 = db.getGiveUpRequestsCount(123);
        var response2 = db.getGiveUpRequestsCount(456);
        boolean firstFoundAndOk = false, secondFoundAndOk = false;
        if (response1 == 192)
            firstFoundAndOk = true;
        if (response2 == 15)
            secondFoundAndOk = true;

        Assertions.assertTrue(firstFoundAndOk);
        Assertions.assertTrue(secondFoundAndOk);
    }
    @Test
    void giveUpReset_TestWork()
    {
        var db = new QuizDBSQLite(null);
        for (var i = 0; i < 192; i++)
            db.giveUpRequestsCountIncrement(123);
        var before=db.getGiveUpRequestsCount(123);
        db.giveUpRequestsCountReset(123);
        var after=db.getGiveUpRequestsCount(123);
        Assertions.assertTrue(before!=after&&after==0);
    }
    @Test
    void getUsername_TestWork()
    {
        var db = new QuizDBSQLite(null);
        db.getUserName(0);
        Assertions.assertEquals(db.getUserName(0),String.format("ID %d",0));
    }
    @Test
    void setUsername_TestWork()
    {
        var db = new QuizDBSQLite(null);
        db.setUserName(123,"alo");
        Assertions.assertEquals(db.getUserName(123),"alo");
    }
}
