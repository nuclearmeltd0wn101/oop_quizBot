package com.company;

import com.company.database.DatabaseCoreSQLite;
import com.company.database.QuestionIdRepositorySQLite;
import com.company.database.SQLRequestsTemplates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class QuestionIdRepositorySQLiteTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

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
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)
                );
    }

    @Test
    void QuestionIdRepository_Get_questionIdForChatIsSet_GeneralCorrectness() {
        var defaultValue = 0L;
        var chatId = 123569;
        var questionId = 3L;
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.QuestionIdRepo_GetRecord.value,
                chatId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(questionId);

        var sut = new QuestionIdRepositorySQLite(dbCore);

        var response = sut.Get(chatId);

        Assertions.assertEquals(questionId, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
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
                                new String[]{
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_InsertRecord.value,
                                                questionId, chatId),
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_UpdateRecord.value,
                                                questionId, chatId)
                                }));
    }
}