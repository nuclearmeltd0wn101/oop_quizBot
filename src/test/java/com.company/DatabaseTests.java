package com.company;

import com.company.database.*;
import com.company.quiz.QuizScore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.function.Function;

public class DatabaseTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

    // Score Repository tests

    @Test
    void ScoreRepository_GetTable_NoChatIdRecord_returnsNull() {
        Mockito.when(
                dbCore.Get(Mockito.anyString(),
                        Mockito.any(Function.class)))
                .thenReturn(new ArrayList<QuizScore>());

        var sut = new ScoreRepositorySQLite(dbCore);

        var scoreTable = sut.GetTable(1337);

        Assertions.assertNull(scoreTable);
        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                                1337)
                            ),
                    Mockito.any(Function.class));
    }

    @Test
    void ScoreRepository_GetTable_sortedByScoreDescending() {
        var dbCoreGetMockResponse = new ArrayList<QuizScore>();
        dbCoreGetMockResponse.add(new QuizScore(1337, 4, 172));
        dbCoreGetMockResponse.add(new QuizScore(1337, 1, 16));
        dbCoreGetMockResponse.add(new QuizScore(1337, 3, 8));
        dbCoreGetMockResponse.add(new QuizScore(1337, 2, 3));

        Mockito.when(dbCore.Get(Mockito.anyString(),
                                Mockito.any(Function.class)))
                .thenReturn(dbCoreGetMockResponse);

        var scoreRepo = new ScoreRepositorySQLite(dbCore);
        var response = scoreRepo.GetTable(1337);


        for (var i = 0; i < response.length - 1; i++)
            Assertions.assertTrue((response[i + 1]).score <= (response[i]).score);

        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                    SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                                    1337)
                    ), Mockito.any(Function.class));
    }

    @Test
    void ScoreRepository_Increment_passesCorrectCoreRequest() {
        var chatId = 1337;
        var userId = 127001;

        var sut = new ScoreRepositorySQLite(dbCore);

        sut.Increment(chatId, userId);

        Mockito.verify(dbCore,
                Mockito.times(1))
                .Save(
                    Mockito.eq(
                            new String[] {
                                    String.format(
                                            SQLRequestsTemplates.ScoreRepo_InsertRecord.value,
                                            chatId, userId),
                                    String.format(
                                            SQLRequestsTemplates.ScoreRepo_Increment.value,
                                            chatId, userId)
                            }
                    )
        );
    }

    // States Repository tests

    @Test
    void StatesRepository_Get_NonexistentChatId_ReturnsZero() {
        var chatIdNonexistent = 321;
        var defaultValue = 0L;

        Mockito.when(dbCore.Get(
                Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new StatesRepositorySQLite(dbCore);

        var responseNonexistent = sut.Get(chatIdNonexistent);

        Assertions.assertEquals(responseNonexistent, defaultValue);
        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                    SQLRequestsTemplates.StatesRepo_GetRecord.value,
                                    chatIdNonexistent)),
                    Mockito.eq("state"),
                    Mockito.eq(defaultValue));
    }

    @Test
    void StatesRepository_Get_ExistentChatId_GeneralCorrectness() {
        var chatIdExistent = 123569;
        var defaultValue = 0L;
        var existentValue = 3L;

        Mockito.when(dbCore.Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.StatesRepo_GetRecord.value,
                                        chatIdExistent)
                        ),
                        Mockito.eq("state"),
                        Mockito.eq(defaultValue)))
                .thenReturn(existentValue);

        var sut = new StatesRepositorySQLite(dbCore);

        var responseExistent = sut.Get(chatIdExistent);

        Assertions.assertEquals(responseExistent, existentValue);
        Mockito.verify(dbCore,
                Mockito.times(1))
                .Get(
                    Mockito.eq(
                            String.format(
                                    SQLRequestsTemplates.StatesRepo_GetRecord.value,
                                    chatIdExistent)),
                    Mockito.eq("state"),
                    Mockito.eq(defaultValue));
    }

    @Test
    void StatesRepository_Set_GeneralCorrectness() {
        var chatId = 123569;
        var stateToSet = 3;

        var sut = new StatesRepositorySQLite(dbCore);

        sut.Set(chatId, stateToSet);

        Mockito.verify(dbCore,
                Mockito.times(1))
                .Save(
                    Mockito.eq(
                        new String[] {
                            String.format(SQLRequestsTemplates.StatesRepo_InsertRecord.value,
                                    stateToSet, chatId),
                            String.format(SQLRequestsTemplates.StatesRepo_UpdateRecord.value,
                                    stateToSet, chatId)
                        }));
    }

    // QuestionId Repository tests

    @Test
    void QuestionIdRepository_Get_noQuestionIdForChat_returnsZero() {
        var defaultValue = 0L;
        var chatId = 123569;

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new QuestionIdRepositorySQLite(dbCore);

        var response = sut.Get(chatId);

        Assertions.assertEquals(defaultValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                    SQLRequestsTemplates.QuestionIdRepo_GetRecord.value,
                                        chatId)),
                        Mockito.eq("questionId"),
                        Mockito.eq(defaultValue)
                );
    }

    @Test
    void QuestionIdRepository_Set_GeneralCorrectness() {
        var chatId = 123569;
        var questionId = 3;

        var sut = new QuestionIdRepositorySQLite(dbCore);

        sut.Set(chatId, questionId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[] {
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_InsertRecord.value,
                                                questionId, chatId),
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_UpdateRecord.value,
                                                questionId, chatId)
                                }));
    }

    // Count Repository tests (covers BOTH WrongAnswersCount and GiveUpRequestsCount Repos!)

    @Test
    void CountRepository_Get_noRecordForChatId_returnsZero()
    {
        var defaultValue = 0L;
        var chatId = 123569;
        var countName = "test";

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new CountRepositorySQLite(dbCore, countName);

        var response = sut.Get(chatId);

        Assertions.assertEquals(defaultValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.CountRepo_GetRecord.value,
                                        countName, chatId)),
                        Mockito.eq("count"),
                        Mockito.eq(defaultValue)
                );
    }

//    @Test
//    void getWrongAnswersIncrement_keepsMultipleChat()
//    {
//        var db = new QuizDBSQLite(null);
//        for (var i = 0; i < 192; i++)
//            db.wrongAnswersCountIncrement(123);
//        for (var i = 0; i < 15; i++)
//            db.wrongAnswersCountIncrement(456);
//
//        var response1 = db.getWrongAnswersCount(123);
//        var response2 = db.getWrongAnswersCount(456);
//
//        Assertions.assertTrue(response1 != 0);
//        Assertions.assertTrue(response2 != 0);
//
//        boolean firstFoundAndOk = false, secondFoundAndOk = false;
//
//        if (response1 == 192)
//            firstFoundAndOk = true;
//        if (response2 == 15)
//            secondFoundAndOk = true;
//
//        Assertions.assertTrue(firstFoundAndOk);
//        Assertions.assertTrue(secondFoundAndOk);
//    }
//    @Test
//    void wrongAnswerReset_TestWork()
//    {
//        var db = new QuizDBSQLite(null);
//        for (var i = 0; i < 192; i++)
//            db.wrongAnswersCountIncrement(123);
//        var before=db.getWrongAnswersCount(123);
//        db.wrongAnswersCountReset(123);
//        var after=db.getWrongAnswersCount(123);
//        Assertions.assertNotEquals(before, after);
//        Assertions.assertEquals(0, after);
//
//    }

    // UserNames Repository tests

//    @Test
//    void getUsername_TestWork()
//    {
//        var db = new QuizDBSQLite(null);
//        db.getUserName(0);
//        Assertions.assertEquals(db.getUserName(0),String.format("ID %d",0));
//    }
//    @Test
//    void setUsername_TestWork()
//    {
//        var db = new QuizDBSQLite(null);
//        db.setUserName(123,"alo");
//        Assertions.assertEquals(db.getUserName(123),"alo");
//    }
//
//    @Test
//    void setUsername_singleQuoteWorks() {
//        var db = new QuizDBSQLite(null);
//        var name = "le 'soleil";
//
//        db.setUserName(228, name);
//        Assertions.assertEquals(name, db.getUserName(228));
//    }
//
//    @Test
//    void setUsername_noInjectionVulnerable() {
//        var db = new QuizDBSQLite(null);
//        var name = "1”/**/UNION/**/SELECT/**/\n" +
//                "password/**/FROM/**/USERS/**/LIMIT/**/1";
//
//        db.setUserName(1337, name);
//        Assertions.assertEquals(name, db.getUserName(1337));
//
//        name = "\\";
//        db.setUserName(121, name);
//        Assertions.assertEquals(name, db.getUserName(121));
//    }
}