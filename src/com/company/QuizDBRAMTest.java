package com.company;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QuizDBRAMTest {

    @Test
    void getScoreTable_NoChatIdRecord_returnsNull() {
        var db = new QuizDBRAM();
        Assertions.assertEquals(null,
                db.getScoreTable(312));
    }

    @Test
    void getScoreTable_sortedByScoreDescending() {
        var db = new QuizDBRAM();
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
        var db = new QuizDBRAM();
        for (var i = 0; i < 192; i++)
            db.scoreIncrement(123, 456);
        for (var i = 0; i < 15; i++)
            db.scoreIncrement(123, 789);

        var response = db.getScoreTable(123);

        Assert.assertTrue(response != null);

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
        var db = new QuizDBRAM();
        for (var i = 0; i < 192; i++)
            db.scoreIncrement(123, 789);
        for (var i = 0; i < 15; i++)
            db.scoreIncrement(456, 789);

        var response1 = db.getScoreTable(123);
        var response2 = db.getScoreTable(456);

        Assert.assertTrue(response1 != null);
        Assert.assertTrue(response2 != null);

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
        var db = new QuizDBRAM();
        Assert.assertEquals(0, db.getState(123569));
    }

    @Test
    void setState_keepsState() {
        var db = new QuizDBRAM();

        db.setState(159951, 123);

        Assert.assertEquals(123, db.getState(159951));
    }

    @Test
    void setState_keepsMultipleRecords() {
        var db = new QuizDBRAM();

        db.setState(159951, 12345);
        db.setState(132, 551);

        Assert.assertEquals(12345, db.getState(159951));
        Assert.assertEquals(551, db.getState(132));
    }

    @Test
    void getQuestionId_noQuestionIdDefined_returnsZero() {
        var db = new QuizDBRAM();
        Assert.assertEquals(0, db.getQuestionId(123569));
    }

    @Test
    void setQuestionId_keepsState() {
        var db = new QuizDBRAM();

        db.setState(159951, 123);

        Assert.assertEquals(123, db.getState(159951));
    }

    @Test
    void setQuestionId_keepsMultipleRecords() {
        var db = new QuizDBRAM();

        db.setQuestionId(159951, 12345);
        db.setQuestionId(132, 551);

        Assert.assertEquals(12345, db.getQuestionId(159951));
        Assert.assertEquals(551, db.getQuestionId(132));
    }
}
