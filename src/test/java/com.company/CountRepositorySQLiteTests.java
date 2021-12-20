package com.company;

import com.company.database.CountRepositorySQLite;
import com.company.database.DatabaseCoreSQLite;
import com.company.database.SQLRequestsTemplates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CountRepositorySQLiteTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

    // notice: these tests covers BOTH WrongAnswersCount and GiveUpRequestsCount repos too
    // because they're just wrappers above Count Repository

    @Test
    void CountRepository_Get_noRecordForChatId_returnsZero() {
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
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)
                );
    }

    @Test
    void CountRepository_Get_ExistentChatId_GeneralCorrectness() {
        var chatIdExistent = 123569;
        var defaultValue = 0L;
        var existentValue = 3L;
        var countName = "test";
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.CountRepo_GetRecord.value,
                countName, chatIdExistent);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(existentValue);

        var sut = new CountRepositorySQLite(dbCore, countName);

        var responseExistent = sut.Get(chatIdExistent);

        Assertions.assertEquals(responseExistent, existentValue);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void CountRepository_Increment_passesCorrectCoreRequest() {
        var chatId = 1337;
        var countName = "test";

        var sut = new CountRepositorySQLite(dbCore, countName);

        sut.Increment(chatId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[]{
                                        String.format(
                                                SQLRequestsTemplates.CountRepo_InsertRecord.value,
                                                countName, chatId),
                                        String.format(
                                                SQLRequestsTemplates.CountRepo_Increment.value,
                                                countName, chatId)
                                }));
    }

    @Test
    void CountRepository_Reset_passesCorrectCoreRequest() {
        var chatId = 1337;
        var countName = "test";

        var sut = new CountRepositorySQLite(dbCore, countName);

        sut.Reset(chatId);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[]{
                                        String.format(
                                                SQLRequestsTemplates.CountRepo_Reset.value,
                                                countName, chatId)
                                }));
    }
}