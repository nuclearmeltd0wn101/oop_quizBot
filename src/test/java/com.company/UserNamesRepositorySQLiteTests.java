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
    UserNamesRepositorySQLite sut;

    Long userId = 321L;
    String defaultUserName;
    String userName = "herbal bebra";

    @BeforeEach
    void SetUp() {
        dbCore = Mockito.mock(DatabaseCoreSQLite.class);
        sut = new UserNamesRepositorySQLite(dbCore);

        defaultUserName = String.format("ID %d", userId);
    }

    @Test
    void Get_UnknownUserId_ReturnsCorrectDefaultString() {

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultUserName)))
                .thenReturn(defaultUserName);

        var responseNonexistent = sut.Get(userId);

        Assertions.assertEquals(responseNonexistent, defaultUserName);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(
                                String.format(
                                        SQLRequestsTemplates.UserNamesRepo_GetRecord.value,
                                        userId)),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultUserName));
    }

    @Test
    void Get_KnownUserId_GeneralCorrectness() {
        var correctSqlRequest = String.format(
                SQLRequestsTemplates.UserNamesRepo_GetRecord.value,
                userId);

        Mockito.when(dbCore.Get(
                        Mockito.anyString(), Mockito.anyString(),
                        Mockito.eq(defaultUserName)))
                .thenReturn(defaultUserName);

        Mockito.when(dbCore.Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultUserName)))
                .thenReturn(userName);

        var response = sut.Get(userId);

        Assertions.assertEquals(userName, response);
        Mockito.verify(dbCore,
                        Mockito.times(1))
                .Get(
                        Mockito.eq(correctSqlRequest),
                        Mockito.eq(SQLRequestsTemplates.UserNamesRepo_GetColumnLabel.value),
                        Mockito.eq(defaultUserName));
    }

    @Test
    void Set_passesCorrectCoreRequest() {
        var escapedName = userName.replace("'", "''");

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