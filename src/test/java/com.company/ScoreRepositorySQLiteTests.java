package com.company;
import com.company.database.*;
import com.company.quiz.QuizScore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.function.Function;

public class ScoreRepositorySQLiteTests {

    DatabaseCoreSQLite dbCore;
    ScoreRepositorySQLite sut;
    Long chatId = 1337L;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
        sut = new ScoreRepositorySQLite(dbCore);
    }

    @Test
    void GetTable_NoChatIdRecord_returnsNull() {
        Mockito.when(
                        dbCore.Get(Mockito.anyString(),
                                Mockito.any(Function.class)))
                .thenReturn(new ArrayList<QuizScore>());

        var scoreTable = sut.GetTable(chatId);

        Assertions.assertNull(scoreTable);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                                        chatId)
                        ),
                        Mockito.any(Function.class));
    }

    @Test
    void GetTable_sortedByScoreDescending() {
        var dbCoreGetMockResponse = new ArrayList<QuizScore>();
        dbCoreGetMockResponse.add(new QuizScore(1337, 4, 172));
        dbCoreGetMockResponse.add(new QuizScore(228, 1, 16));
        dbCoreGetMockResponse.add(new QuizScore(127001, 3, 8));
        dbCoreGetMockResponse.add(new QuizScore(10001, 2, 3));

        Mockito.when(dbCore.Get(Mockito.anyString(),
                        Mockito.any(Function.class)))
                .thenReturn(dbCoreGetMockResponse);

        var response = sut.GetTable(chatId);


        for (var i = 0; i < response.length - 1; i++)
            Assertions.assertTrue((response[i + 1]).score <= (response[i]).score);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.ScoreRepo_GetScoreRecords.value,
                                        chatId)
                        ), Mockito.any(Function.class));
    }

    @Test
    void Increment_passesCorrectCoreRequest() {
        var userId = 127001;

        sut.Increment(chatId, userId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[]{
                                        String.format(
                                                SQLRequestsTemplates.ScoreRepo_InsertRecord.value,
                                                chatId, userId),
                                        String.format(
                                                SQLRequestsTemplates.ScoreRepo_Increment.value,
                                                chatId, userId)
                                }));
    }
}