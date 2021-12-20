package com.company;

import com.company.database.DatabaseCoreSQLite;
import com.company.database.SQLRequestsTemplates;
import com.company.database.UserNamesRepositorySQLite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UserNamesRepositorySQLiteTests {

    DatabaseCoreSQLite dbCore;

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
    }

    @Test
    void UserNamesRepository_Get_UnknownUserId_ReturnsCorrectDefaultString() {
        var unknownUserId = 321;
        var defaultValue = String.format("ID %d", unknownUserId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        var sut = new UserNamesRepositorySQLite(dbCore);

        var responseNonexistent = sut.Get(unknownUserId);

        Assertions.assertEquals(responseNonexistent, defaultValue);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.UserNamesRepo_GetRecord.value,
                                        unknownUserId)),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void UserNamesRepository_Get_KnownUserId_GeneralCorrectness() {
        var knownUserId = 123569;
        var defaultValue = String.format("ID %d", knownUserId);
        var knownValue = "aboba";
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.UserNamesRepo_GetRecord.value,
                knownUserId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultValue)))
                .thenReturn(defaultValue);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue)))
                .thenReturn(knownValue);

        var sut = new UserNamesRepositorySQLite(dbCore);

        var response = sut.Get(knownUserId);

        Assertions.assertEquals(knownValue, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultValue));
    }

    @Test
    void UserNamesRepository_Set_passesCorrectCoreRequest() {
        var userId = 1337;
        var userName = "herbal bebra";
        var escapedName = userName.replace("'", "''");

        var sut = new UserNamesRepositorySQLite(dbCore);

        sut.Set(userId, userName);

        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Save(
                        Mockito.eq(
                                new String[]{
                                        String.format(
                                                SQLRequestsTemplates.UserNamesRepo_CreateRecord.value,
                                                escapedName, userId),
                                        String.format(
                                                SQLRequestsTemplates.UserNamesRepo_UpdateRecord.value,
                                                escapedName, userId)
                                }));
    }
}