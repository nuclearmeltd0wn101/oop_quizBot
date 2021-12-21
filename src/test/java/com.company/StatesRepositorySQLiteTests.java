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
    StatesRepositorySQLite sut;

    Long chatId = 321L;
    Long defaultState = 0L;
    Long state = 3L;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
        sut = new StatesRepositorySQLite(dbCore);
    }

    @Test
    void Get_NonexistentChatId_ReturnsZero() {
        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultState)))
                .thenReturn(defaultState);

        var responseNonexistent = sut.Get(chatId);

        Assertions.assertEquals(responseNonexistent, defaultState);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.StatesRepo_GetRecord.value,
                                        chatId)),
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultState));
    }

    @Test
    void Get_ExistentChatId_GeneralCorrectness() {
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.StatesRepo_GetRecord.value,
                chatId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultState)))
                .thenReturn(defaultState);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultState)))
                .thenReturn(state);

        var responseExistent = sut.Get(chatId);

        Assertions.assertEquals(responseExistent, state);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.StatesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultState));
    }

    @Test
    void Set_GeneralCorrectness() {
        sut.Set(chatId, state);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[]{
                                        String.format(SQLRequestsTemplates.StatesRepo_InsertRecord.value,
                                                state, chatId),
                                        String.format(SQLRequestsTemplates.StatesRepo_UpdateRecord.value,
                                                state, chatId)
                                }));
    }
}