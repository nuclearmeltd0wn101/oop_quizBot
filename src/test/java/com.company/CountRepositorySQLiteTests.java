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
    CountRepositorySQLite sut;

    String countName = "test";
    Long chatId = 123569L;
    Long defaultValue = 0L;
    Long existentValue = 3L;


    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
        sut = new CountRepositorySQLite(dbCore, countName);
    }

    // notice: these tests covers BOTH WrongAnswersCount and GiveUpRequestsCount repos too
    // because they're just wrappers above Count Repository

    @Test
    void Get_noRecordForChatId_returnsZero() {
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
                                        SQLRequestsTemplates.CountRepo_GetRecord.value,
                                        countName, chatId)),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void Get_ExistentChatId_GeneralCorrectness() {
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.CountRepo_GetRecord.value,
                countName, chatId);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(existentValue);

        var responseExistent = sut.Get(chatId);

        Assertions.assertEquals(responseExistent, existentValue);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.CountRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void Increment_passesCorrectCoreRequest() {
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
    void Reset_passesCorrectCoreRequest() {
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