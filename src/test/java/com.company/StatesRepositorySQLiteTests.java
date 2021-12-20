package com.company;

import com.company.database.DatabaseCoreSQLite;
import com.company.database.SQLRequestsTemplates;
import com.company.database.StatesRepositorySQLite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StatesRepositorySQLiteTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

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
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void StatesRepository_Get_ExistentChatId_GeneralCorrectness() {
        var chatIdExistent = 123569;
        var defaultValue = 0L;
        var existentValue = 3L;
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.StatesRepo_GetRecord.value,
                chatIdExistent);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(existentValue);

        var sut = new StatesRepositorySQLite(dbCore);

        var responseExistent = sut.Get(chatIdExistent);

        Assertions.assertEquals(responseExistent, existentValue);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
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
                                new String[]{
                                        String.format(SQLRequestsTemplates.StatesRepo_InsertRecord.value,
                                                stateToSet, chatId),
                                        String.format(SQLRequestsTemplates.StatesRepo_UpdateRecord.value,
                                                stateToSet, chatId)
                                }));
    }
}