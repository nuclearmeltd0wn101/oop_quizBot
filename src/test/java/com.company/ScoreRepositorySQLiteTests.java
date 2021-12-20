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

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

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
        var chatId = 1337;
        var dbCoreGetMockResponse = new ArrayList<QuizScore>();
        dbCoreGetMockResponse.add(new QuizScore(1337, 4, 172));
        dbCoreGetMockResponse.add(new QuizScore(1337, 1, 16));
        dbCoreGetMockResponse.add(new QuizScore(1337, 3, 8));
        dbCoreGetMockResponse.add(new QuizScore(1337, 2, 3));

        Mockito.when(dbCore.Get(Mockito.anyString(),
                        Mockito.any(Function.class)))
                .thenReturn(dbCoreGetMockResponse);

        var scoreRepo = new ScoreRepositorySQLite(dbCore);
        var response = scoreRepo.GetTable(chatId);


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
    void ScoreRepository_Increment_passesCorrectCoreRequest() {
        var chatId = 1337;
        var userId = 127001;

        var sut = new ScoreRepositorySQLite(dbCore);

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
                                }
                        )
                );
    }
}