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
    QuestionIdRepositorySQLite sut;

    Long chatId = 123569L;
    Long defaultValue = 0L;
    Long existentValue = 3L;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
        sut = new QuestionIdRepositorySQLite(dbCore);
    }

    @Test
    void Get_noQuestionIdForChat_returnsZero() {
        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

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
    void Get_questionIdForChatIsSet_GeneralCorrectness() {
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
                .thenReturn(existentValue);

        var response = sut.Get(chatId);

        Assertions.assertEquals(existentValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.QuestionIdRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void Set_GeneralCorrectness() {
        sut.Set(chatId, existentValue.intValue());

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[]{
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_InsertRecord.value,
                                                existentValue, chatId),
                                        String.format(SQLRequestsTemplates.QuestionIdRepo_UpdateRecord.value,
                                                existentValue, chatId)
                                }));
    }
}